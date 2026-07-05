<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCategory, addCategory, updateCategory, deleteCategory } from '@/api/category'

const categories = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({ id: null, categoryName: '', categoryAlias: '' })
const loading = ref(false)

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  categoryAlias: [{ required: true, message: '请输入分类别名', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await listCategory()
    categories.value = res.data
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  dialogTitle.value = '新增分类'
  form.value = { id: null, categoryName: '', categoryAlias: '' }
  dialogVisible.value = true
}

const openEdit = (row) => {
  dialogTitle.value = '编辑分类'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (form.value.id) {
    await updateCategory(form.value)
  } else {
    await addCategory(form.value)
  }
  ElMessage.success(form.value.id ? '修改成功' : '新增成功')
  dialogVisible.value = false
  fetchData()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' })
  await deleteCategory(id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="category-page">
    <el-card>
      <template #header>
        <div class="header">
          <span>分类管理</span>
          <el-button type="primary" @click="openAdd">新增分类</el-button>
        </div>
      </template>
      <el-table :data="categories" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="categoryAlias" label="分类别名" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" />
        </el-form-item>
        <el-form-item label="分类别名" prop="categoryAlias">
          <el-input v-model="form.categoryAlias" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
