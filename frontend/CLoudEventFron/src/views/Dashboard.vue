<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { listArticle, listPublishedArticle, articleDetail } from '@/api/article'
import { listCategory } from '@/api/category'

const userStore = useUserStore()
const articleCount = ref(0)
const categoryCount = ref(0)
const publishedArticles = ref([])

const previewVisible = ref(false)
const previewArticle = ref(null)
const previewLoading = ref(false)

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

const fetchStats = async () => {
  const [articleRes, categoryRes, publishedRes] = await Promise.all([
    listArticle({ pageNum: 1, pageSize: 1 }),
    listCategory(),
    listPublishedArticle({ pageNum: 1, pageSize: 10 })
  ])
  articleCount.value = articleRes.data.total
  categoryCount.value = categoryRes.data.length
  publishedArticles.value = publishedRes.data.items || []
}

onMounted(fetchStats)
</script>

<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <div class="welcome">
        <h1>欢迎回来，{{ userStore.user?.username || '用户' }}</h1>
        <p>欢迎使用 BigEvent 文章管理系统</p>
      </div>
    </el-card>
    <el-row :gutter="20" class="cards">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ articleCount }}</div>
            <div class="stat-label">文章总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ categoryCount }}</div>
            <div class="stat-label">分类总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value">{{ userStore.user?.nickname || '—' }}</div>
            <div class="stat-label">用户昵称</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="published-card">
      <template #header>看一看</template>
      <el-table :data="publishedArticles" stripe empty-text="暂无已发布文章">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="primary" link @click="handlePreview(row.id)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="previewVisible" title="文章预览" width="700px" :close-on-click-modal="false">
      <div v-loading="previewLoading" class="preview-body">
        <h2 class="preview-title">{{ previewArticle?.title }}</h2>
        <div class="preview-meta">
          <span>分类：{{ previewArticle?.categoryName || '—' }}</span>
          <el-tag size="small" type="success">已发布</el-tag>
        </div>
        <img v-if="previewArticle?.coverImg" :src="previewArticle.coverImg" class="preview-cover" @error="$event.target.style.display='none'" />
        <div class="preview-content">{{ previewArticle?.content }}</div>
        <div class="preview-time">发布时间：{{ previewArticle?.createTime }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}
.welcome-card {
  margin-bottom: 20px;
}
.welcome h1 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #303133;
}
.welcome p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
.cards {
  margin-bottom: 20px;
}
.cards .stat-item {
  text-align: center;
  padding: 20px 0;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
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
