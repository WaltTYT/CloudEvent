<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listArticle, deleteArticle, articleDetail, updateArticleState } from '@/api/article'
import { listCategory } from '@/api/category'

const router = useRouter()

const articles = ref([])
const total = ref(0)
const loading = ref(false)
const query = ref({
  pageNum: 1,
  pageSize: 10,
  categoryId: null,
  state: null
})
const categories = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listArticle(query.value)
    articles.value = res.data.items
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  const res = await listCategory()
  categories.value = res.data
}

const handleSizeChange = (size) => {
  query.value.pageSize = size
  query.value.pageNum = 1
  fetchData()
}

const handleCurrentChange = (page) => {
  query.value.pageNum = page
  fetchData()
}

const handleSearch = () => {
  query.value.pageNum = 1
  fetchData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该文章？', '提示', { type: 'warning' })
  await deleteArticle(id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleEdit = (id) => {
  router.push(`/article/edit/${id}`)
}

const previewVisible = ref(false)
const previewArticle = ref(null)
const previewLoading = ref(false)

const formatTime = (time) => {
  if (!time) return '—'
  return time.replace('T', ' ').substring(0, 19)
}

const handlePreview = async (id) => {
  previewLoading.value = true
  try {
    const res = await articleDetail(id)
    previewArticle.value = res.data
    previewVisible.value = true
  } finally {
    previewLoading.value = false
  }
}

const handlePublish = async (row) => {
  await ElMessageBox.confirm('确认发布该文章？', '提示', { type: 'info' })
  await updateArticleState(row.id, '已发布')
  ElMessage.success('发布成功')
  fetchData()
}

onMounted(() => {
  fetchCategories()
  fetchData()
})
</script>

<template>
  <div class="article-list">
    <el-card>
      <template #header>
        <div class="header">
          <span>文章列表</span>
          <el-button type="primary" @click="router.push('/article/add')">新建文章</el-button>
        </div>
      </template>
      <el-form :model="query" inline>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部" clearable style="width:160px">
            <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.state" placeholder="全部" clearable style="width:140px">
            <el-option label="已发布" value="已发布" />
            <el-option label="草稿" value="草稿" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="articles" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="categoryId" label="分类" width="120">
          <template #default="{ row }">
            {{ categories.find(c => c.id === row.categoryId)?.categoryName || '—' }}
          </template>
        </el-table-column>
        <el-table-column prop="state" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.state === '已发布' ? 'success' : 'info'">{{ row.state }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handlePreview(row.id)">预览</el-button>
            <el-button type="primary" link @click="handleEdit(row.id)">编辑</el-button>
            <el-button v-if="row.state === '草稿'" type="success" link @click="handlePublish(row)">发布</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[5, 10, 15, 20]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="previewVisible" title="文章预览" width="700px" :close-on-click-modal="false">
      <div v-loading="previewLoading" class="preview-body">
        <h2 class="preview-title">{{ previewArticle?.title }}</h2>
        <div class="preview-meta">
          <span>分类：{{ categories.find(c => c.id === previewArticle?.categoryId)?.categoryName || '—' }}</span>
          <el-tag :type="previewArticle?.state === '已发布' ? 'success' : 'info'" size="small">{{ previewArticle?.state }}</el-tag>
        </div>
        <img v-if="previewArticle?.coverImg" :src="previewArticle.coverImg" class="preview-cover" @error="$event.target.style.display='none'" />
        <div class="preview-content">{{ previewArticle?.content }}</div>
        <div class="preview-time">创建时间：{{ formatTime(previewArticle?.createTime) }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
.preview-body {
  min-height: 100px;
}
.preview-title {
  margin: 0 0 12px;
  font-size: 20px;
  color: #303133;
}
.preview-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  font-size: 13px;
  color: #909399;
}
.preview-cover {
  max-width: 100%;
  max-height: 300px;
  border-radius: 6px;
  margin-bottom: 16px;
  display: block;
}
.preview-content {
  font-size: 14px;
  line-height: 1.8;
  color: #303133;
  white-space: pre-wrap;
  margin-bottom: 12px;
}
.preview-time {
  font-size: 12px;
  color: #c0c4cc;
}
</style>
