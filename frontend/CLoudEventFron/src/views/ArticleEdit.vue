<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addArticle, uploadFile } from '@/api/article'
import { listCategory } from '@/api/category'

const router = useRouter()
const formRef = ref(null)
const fileInput = ref(null)
const uploading = ref(false)

const form = ref({
  title: '',
  content: '',
  coverImg: '',
  categoryId: null,
  state: '草稿'
})

const categories = ref([])
const previewUrl = ref('')

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { pattern: /^\S{1,10}$/, message: '标题需1-10位非空字符', trigger: 'blur' }
  ],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const fetchCategories = async () => {
  const res = await listCategory()
  categories.value = res.data
}

const handleFileSelect = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (!/^image\/(jpeg|png)$/.test(file.type)) {
    ElMessage.error('仅支持 JPG/PNG 格式')
    return
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  const img = new Image()
  img.onload = async () => {
    if (img.width > 800 || img.height > 450) {
      ElMessage.error('图片尺寸过大，请使用不超过 800×450 的图片')
      return
    }
    uploading.value = true
    try {
      const res = await uploadFile(file)
      previewUrl.value = URL.createObjectURL(file)
      form.value.coverImg = res.data
    } finally {
      uploading.value = false
      fileInput.value.value = ''
    }
  }
  img.onerror = () => {
    ElMessage.error('图片加载失败，请选择有效的图片文件')
  }
  img.src = URL.createObjectURL(file)
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  await addArticle(form.value)
  ElMessage.success('发布成功')
  router.push('/article')
}

onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="article-edit">
    <el-card>
      <template #header>
        <span>{{ isEdit ? '编辑文章' : '新建文章' }}</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="文章标题" prop="title">
          <el-input v-model="form.title" maxlength="10" show-word-limit />
        </el-form-item>
        <el-form-item label="文章内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" />
        </el-form-item>
        <el-form-item label="封面图片">
          <div class="cover-upload">
            <input ref="fileInput" type="file" accept="image/jpeg,image/png" @change="handleFileSelect" class="file-input" />
            <el-button type="primary" :loading="uploading" @click="fileInput.click()">
              选择图片
            </el-button>
            <span class="file-hint">支持 JPG / PNG 格式，大小不超过 2MB，建议尺寸 800×450</span>
          </div>
          <img v-if="previewUrl" :src="previewUrl" class="cover-preview" />
        </el-form-item>
        <el-form-item label="文章分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="form.state">
            <el-radio value="草稿">草稿</el-radio>
            <el-radio value="已发布">已发布</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交</el-button>
          <el-button @click="router.push('/article')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.article-edit {
  max-width: 800px;
  margin: 0 auto;
}
.cover-upload {
  display: flex;
  align-items: center;
  gap: 12px;
}
.file-input {
  display: none;
}
.file-hint {
  font-size: 12px;
  color: #999;
}
.cover-preview {
  max-width: 300px;
  max-height: 160px;
  margin-top: 10px;
  border-radius: 4px;
  display: block;
}
</style>
