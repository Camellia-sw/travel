<template>
  <div class="guide-list-page">
    <!-- 页面头部 -->
    <div class="page-hero">
      <div class="hero-content">
        <h1 class="hero-title">
          <i class="fas fa-book-open"></i>
          旅游攻略
        </h1>
        <p class="hero-subtitle">分享旅行故事，发现精彩世界</p>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="page-container">
      <!-- 搜索和分类 -->
      <div class="search-section">
        <div class="search-bar">
          <el-input
              v-model="searchForm.title"
              placeholder="搜索攻略标题、目的地..."
              clearable
              class="search-input"
              @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <i class="fas fa-search"></i>
            搜索
          </el-button>
          <el-button type="success" @click="goEdit" class="publish-btn">
            <i class="fas fa-pen"></i>
            发布攻略
          </el-button>
        </div>
      </div>

      <!-- 主内容区 -->
      <div class="main-content">
        <!-- 结果统计 -->
        <div class="result-header">
          <div class="result-count">
            找到 <span class="count-number">{{ total }}</span> 篇攻略
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="tableData.length === 0" class="empty-state">
          <div class="empty-icon">📝</div>
          <h3 class="empty-title">暂无攻略内容</h3>
          <p class="empty-desc">成为第一个分享旅游攻略的人吧！</p>
          <el-button type="primary" @click="goEdit" class="empty-action">
            <i class="fas fa-pen"></i>
            发布攻略
          </el-button>
        </div>

        <!-- 攻略列表 -->
        <div v-else class="guides-list">
          <div
              v-for="item in tableData"
              :key="item.id"
              class="guide-item"
              @click="goDetail(item.id)"
          >
            <img
                :src="getImageUrl(item.coverImage)"
                :alt="item.title"
                class="guide-image"
                v-if="item.coverImage"
            />
            <div v-else class="guide-image default-image">
              <i class="fas fa-book-open"></i>
            </div>

            <div class="guide-info">
              <h3 class="guide-title">{{ item.title }}</h3>
              <div class="guide-meta">
                  <span class="meta-item">
                    <i class="fas fa-user"></i>
                    {{ item.userNickname || '旅行者' + item.userId }}
                  </span>
                <span class="meta-item">
                    <i class="fas fa-calendar"></i>
                    {{ formatDate(item.createTime) }}
                  </span>
                <span class="meta-item">
                    <i class="fas fa-eye"></i>
                    {{ formatNumber(item.views) }}
                  </span>
              </div>
              <p class="guide-excerpt" v-if="item.content">
                {{ stripHtmlTags(item.content).substring(0, 100) }}...
              </p>
              <div class="guide-actions">
                <div class="guide-tags">
                  <!-- 可以根据实际数据添加标签 -->
                </div>
                <span class="read-more">
                    阅读全文 <i class="fas fa-arrow-right"></i>
                  </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
              background
              layout="total, prev, pager, next, jumper"
              :current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              @current-change="handleCurrentChange"
              class="guide-pagination"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { formatDate } from '@/utils/dateUtils'
import {
  Search, View, Calendar, Edit, User, Reading, RefreshLeft
} from '@element-plus/icons-vue'

const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'
const router = useRouter()
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const totalViews = ref(0)
const searchForm = reactive({
  title: ''
})

const getImageUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : baseAPI + url
}

const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const stripHtmlTags = (html) => {
  if (!html) return ''
  const div = document.createElement('div')
  div.innerHTML = html
  let text = div.textContent || div.innerText || ''

  text = text
      .replace(/#{1,6}\s+/g, '')
      .replace(/\*\*\*(.+?)\*\*\*/g, '$1')
      .replace(/\*\*(.+?)\*\*/g, '$1')
      .replace(/\*(.+?)\*/g, '$1')
      .replace(/__(.+?)__/g, '$1')
      .replace(/_(.+?)_/g, '$1')
      .replace(/~~(.+?)~~/g, '$1')
      .replace(/`{1,3}[^`\n]+`{1,3}/g, '')
      .replace(/$$([^$$]+)]$[^)]+$/g, '$1')
      .replace(/!$$([^$$]*)]$[^)]+$/g, '')
      .replace(/>\s+/g, '')
      .replace(/^\s*[-*+]\s+/gm, '')
      .replace(/^\s*\d+\.\s+/gm, '')
      .replace(/\n+/g, ' ')
      .replace(/\s+/g, ' ')
      .trim()

  return text
}

const fetchGuides = async () => {
  loading.value = true
  try {
    await request.get('/guide/page', {
      title: searchForm.title || undefined,
      currentPage: currentPage.value,
      size: pageSize.value
    }, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        tableData.value = res.records||[]
        total.value = res.total||0
        totalViews.value = tableData.value.reduce((sum, item) => sum + (item.views || 0), 0)
      }
    })
  } catch (error) {
    console.error('获取攻略列表失败', error)
  } finally {
    loading.value = false
  }
}

const clearSearchTitle = () => {
  searchForm.title = ''
  handleSearch()
}

onMounted(fetchGuides)

const handleSearch = () => {
  currentPage.value = 1
  fetchGuides()
}

const resetSearch = () => {
  searchForm.title = ''
  currentPage.value = 1
  fetchGuides()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchGuides()
}

const goDetail = (id) => {
  router.push(`/guide/detail/${id}`)
}

const goEdit = () => {
  router.push('/guide/edit')
}
</script>

<style lang="scss" scoped>
.guide-list-page {
  min-height: 100vh;
  background: #f8f8f8;
}

.page-hero {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  padding: 48px 0;
}

.hero-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.hero-title i {
  font-size: 32px;
}

.hero-subtitle {
  font-size: 20px;
  margin: 0;
  opacity: 0.9;
}

.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 24px;
}

.search-section {
  background: white;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  flex: 1;
  max-width: 500px;
}

.search-input :deep(.el-input__wrapper) {
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #4A90E2;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #4A90E2;
  box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
}

.search-btn,
.publish-btn {
  border-radius: 8px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search-btn {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.3);
}

.publish-btn {
  background: #10b981;
  color: white;
}

.publish-btn:hover {
  background: #059669;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.main-content {
  width: 100%;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.result-count {
  color: #6b7280;
}

.count-number {
  font-weight: 700;
  color: #111827;
}

.loading-state {
  text-align: center;
  padding: 80px 20px;
  color: #6b7280;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid #f3f4f6;
  border-top-color: #4A90E2;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #6b7280;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 24px;
  opacity: 0.6;
}

.empty-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 12px;
  color: #111827;
}

.empty-desc {
  font-size: 16px;
  margin: 0 0 32px;
}

.empty-action {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.empty-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(74, 144, 226, 0.4);
}

.guides-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.guide-item {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  flex-direction: row;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e5e7eb;
  min-height: 180px;
}

.guide-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  border-color: rgba(74, 144, 226, 0.3);
}

.guide-image {
  width: 200px;
  min-height: 180px;
  object-fit: cover;
  flex-shrink: 0;
}

.guide-image.default-image {
  background: linear-gradient(135deg, #4A90E2, #357ABD);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
}

.guide-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.guide-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #111827;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}

.guide-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.meta-item {
  font-size: 14px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item i {
  font-size: 14px;
}

.guide-excerpt {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0 0 16px 0;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.guide-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.guide-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.read-more {
  color: #4A90E2;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
}

.read-more:hover {
  color: #357ABD;
}

.read-more i {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.guide-item:hover .read-more i {
  transform: translateX(4px);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.guide-pagination :deep(.el-pager li) {
  border-radius: 8px;
  margin: 0 4px;
  transition: all 0.3s ease;
}

.guide-pagination :deep(.el-pager li:hover) {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
}

.guide-pagination :deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
}

.guide-pagination :deep(.btn-prev),
.guide-pagination :deep(.btn-next) {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.guide-pagination :deep(.btn-prev:hover),
.guide-pagination :deep(.btn-next:hover) {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
}

@media (max-width: 1024px) {
  .guides-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .guides-list {
    grid-template-columns: 1fr;
  }

  .guide-item {
    flex-direction: column;
  }

  .guide-image {
    width: 100%;
    height: 200px;
  }

  .page-container {
    padding: 20px 16px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 16px;
  }
}
</style>