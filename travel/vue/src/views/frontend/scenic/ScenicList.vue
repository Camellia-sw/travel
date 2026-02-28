<template>
  <div class="scenic-list-page">
    <!-- 页面头部 -->
    <div class="page-banner">
      <div class="banner-content">
        <h1 class="banner-title">
          <i class="fas fa-mountain"></i>
          探索精彩景点
        </h1>
        <p class="banner-subtitle">发现中国最美的风景</p>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="page-container">
      <div class="content-wrapper">
        <!-- 侧边栏筛选 -->
        <aside class="sidebar-filter">
          <div class="filter-card">
            <h3 class="filter-header">
              <i class="fas fa-filter"></i>
              筛选条件
            </h3>

            <!-- 分类筛选 -->
            <div class="filter-section">
              <h4 class="filter-title">景点分类</h4>
              <div class="filter-options">
                <label
                    v-for="category in categoryList"
                    :key="category.id"
                    class="filter-checkbox"
                    :class="{active: searchForm.categoryId === category.id}"
                    @click="handleCategoryChange(category.id)"
                >
                  <input type="checkbox" :checked="searchForm.categoryId === category.id" />
                  <span>{{ category.name }}</span>
                </label>
              </div>
            </div>


          </div>
        </aside>

        <!-- 主内容区 -->
        <main class="main-content">
          <!-- 搜索和重置 -->
          <div class="search-bar-card">
            <div class="search-bar-content">
              <div class="search-input-wrapper">
                <input
                    type="text"
                    v-model="searchForm.name"
                    placeholder="搜索景点名称..."
                    class="search-input"
                    @keyup.enter="handleSearch"
                />
                <i class="fas fa-search search-icon"></i>
              </div>
              <div class="search-actions">
                <button class="search-btn" @click="handleSearch">
                  <i class="fas fa-search"></i> 搜索
                </button>
                <button class="reset-btn" @click="resetSearch">
                  <i class="fas fa-redo"></i> 重置
                </button>
              </div>
            </div>
          </div>

          <!-- 景点列表 -->
          <div v-if="tableData && tableData.length > 0" class="scenic-grid">
            <div
                v-for="item in tableData"
                :key="item.id"
                class="scenic-card"
                @click="goDetail(item.id)"
            >
              <div class="scenic-image">
                <img :src="getImageUrl(item.imageUrl)" :alt="item.name" />
                <div v-if="item.price === 0" class="scenic-badge free">免费</div>
                <div v-else-if="item.price > 0" class="scenic-badge price">¥{{ item.price }}</div>
              </div>
              <div class="scenic-content">
                <h3 class="scenic-name">{{ item.name }}</h3>
                <p class="scenic-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ item.location || '未知地区' }}
                </p>
                <p class="scenic-desc">{{ truncateText(item.description, 60) }}</p>
                <div class="scenic-footer">
                  <div class="scenic-stats">
                    <span><i class="fas fa-heart"></i> {{ formatReviewCount(item.reviewCount) }}</span>
                  </div>
                  <a href="javascript:void(0)" class="detail-link" @click.stop="goDetail(item.id)">
                    查看详情 <i class="fas fa-arrow-right"></i>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <div class="empty-icon">🔍</div>
            <h3 class="empty-title">暂无景点信息</h3>
            <p class="empty-desc">试试调整搜索条件或浏览其他分类</p>
            <button class="empty-action-btn" @click="resetSearch">
              重新搜索
            </button>
          </div>

          <!-- 分页 -->
          <div v-if="total > 0" class="pagination-container">
            <nav class="pagination">
              <button class="pagination-btn" @click="handleCurrentChange(currentPage - 1)" :disabled="currentPage === 1">
                <i class="fas fa-chevron-left"></i>
              </button>
              <button
                  v-for="(page, index) in visiblePages"
                  :key="index"
                  class="pagination-btn"
                  :class="{active: currentPage === page}"
                  @click="page !== '...' && handleCurrentChange(page)"
                  :disabled="page === '...'"
              >
                {{ page }}
              </button>
              <button class="pagination-btn" @click="handleCurrentChange(currentPage + 1)" :disabled="currentPage >= Math.ceil(total / pageSize)">
                <i class="fas fa-chevron-right"></i>
              </button>
            </nav>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import { Search, Location, Refresh, Star, Grid } from '@element-plus/icons-vue'

const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const tableData = ref([])
const categoryList = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)
const searchForm = reactive({
  name: '',
  location: '',
  categoryId: null
})
const collectionStatus = ref({}) // 收藏状态映射

// 检查是否登录
const isLoggedIn = computed(() => userStore.isLoggedIn)

const fetchCategories = async () => {
  try {
    await request.get('/scenic-category/tree', {}, {
      onSuccess: (res) => {
        categoryList.value = res || [];
      }
    });
  } catch (error) {
    console.error('获取分类列表失败:', error);
    categoryList.value = [];
  }
}

const fetchScenicSpots = async () => {
  try {
    await request.get('/scenic/page', {
      name: searchForm.name,
      location: searchForm.location,
      categoryId: searchForm.categoryId,
      currentPage: currentPage.value,
      size: pageSize.value
    }, {
      onSuccess: (res) => {
        tableData.value = res.records || []
        total.value = res.total || 0

        // 如果用户已登录，检查收藏状态
        if (isLoggedIn.value && tableData.value.length > 0) {
          checkCollectionStatus()
        }

        // 获取评论统计信息
        if (tableData.value.length > 0) {
          fetchBatchCommentStats()
        }
      }
    })
  } catch (error) {
    console.error('获取景点列表失败:', error)
    tableData.value = []
    total.value = 0
  }
}

// 检查景点收藏状态
const checkCollectionStatus = async () => {
  // 提取景点ID列表
  const scenicIds = tableData.value.map(item => item.id)
  if (scenicIds.length === 0) return

  try {
    await request.post('/scenic-collection/batch-is-collected', scenicIds, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        collectionStatus.value = res || {}
      }
    })
  } catch (error) {
    console.error('获取收藏状态失败:', error)
  }
}

// 处理URL搜索参数
const handleUrlParams = () => {
  const searchParam = route.query.search
  const categoryParam = route.params.categoryId

  if (searchParam) {
    // 如果有搜索参数，设置到搜索表单中
    searchForm.name = decodeURIComponent(searchParam)
  }

  if (categoryParam) {
    // 如果有分类参数，设置分类ID
    searchForm.categoryId = parseInt(categoryParam)
  }
}

// 监听路由变化
watch(() => route.query, (newQuery) => {
  if (newQuery.search !== undefined) {
    searchForm.name = newQuery.search ? decodeURIComponent(newQuery.search) : ''
    currentPage.value = 1
    fetchScenicSpots()
  }
}, { immediate: false })

watch(() => route.params.categoryId, (newCategoryId) => {
  if (newCategoryId !== undefined) {
    searchForm.categoryId = newCategoryId ? parseInt(newCategoryId) : null
    currentPage.value = 1
    fetchScenicSpots()
  }
}, { immediate: false })

onMounted(() => {
  fetchCategories();
  handleUrlParams();
  fetchScenicSpots();
})

const handleSearch = () => {
  currentPage.value = 1
  fetchScenicSpots()
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.location = ''
  searchForm.categoryId = null // 重置分类ID
  currentPage.value = 1
  fetchScenicSpots()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchScenicSpots()
}

const handleCategoryChange = (categoryId) => {
  // 如果点击当前已选中的分类，则取消选择
  if (searchForm.categoryId === categoryId) {
    searchForm.categoryId = null
  } else {
    searchForm.categoryId = categoryId
  }
  currentPage.value = 1
  fetchScenicSpots()
}

const goDetail = (id) => {
  router.push(`/scenic/${id}`)
}

// 清除搜索条件的方法
const clearSearchName = () => {
  searchForm.name = ''
  handleSearch()
}

const clearSearchLocation = () => {
  searchForm.location = ''
  handleSearch()
}

const clearSearchCategory = () => {
  searchForm.categoryId = null
  handleSearch()
}

// 获取当前选中分类的名称
const getCurrentCategoryName = () => {
  if (!searchForm.categoryId) return ''
  const category = categoryList.value.find(cat => cat.id === searchForm.categoryId)
  return category ? category.name : ''
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return '/default-scenic.jpg'
  return url.startsWith('http') ? url : baseAPI + url
}

// 截取文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 格式化评价数量
const formatReviewCount = (count) => {
  if (!count || count === 0) return '暂无评价'
  if (count === 1) return '1条评价'
  return `${count}条评价`
}

// 计算可见页码
const visiblePages = computed(() => {
  const totalPages = Math.ceil(total.value / pageSize.value)
  const current = currentPage.value
  const pages = []

  if (totalPages <= 7) {
    for (let i = 1; i <= totalPages; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i)
      pages.push('...')
      pages.push(totalPages)
    } else if (current >= totalPages - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = totalPages - 4; i <= totalPages; i++) pages.push(i)
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) pages.push(i)
      pages.push('...')
      pages.push(totalPages)
    }
  }

  return pages
})

// 批量获取评论统计信息
const fetchBatchCommentStats = async () => {
  // 为每个景点获取评论统计
  for (const item of tableData.value) {
    try {
      await request.get('/comment/page', {
        scenicId: item.id,
        currentPage: 1,
        size: 1  // 只需要获取总数
      }, {
        showDefaultMsg: false,
        onSuccess: (res) => {
          // 更新景点的评论数量
          item.reviewCount = res.total || 0
        }
      })
    } catch (error) {
      console.error(`获取景点${item.id}评论统计失败:`, error)
      item.reviewCount = 0
    }
  }
}
</script>

<style lang="scss" scoped>
/* SCSS变量定义 */
$color-primary: #4A90E2;
$color-secondary: #357ABD;
$color-success: #27AE60;
$color-warning: #F39C12;
$color-gray-50: #f9fafb;
$color-gray-100: #f3f4f6;
$color-gray-200: #e5e7eb;
$color-gray-300: #d1d5db;
$color-gray-600: #4b5563;
$color-gray-700: #374151;
$color-gray-800: #1f2937;
$color-gray-900: #111827;

.scenic-list-page {
  min-height: 100vh;
  background-color: $color-gray-50;
}

/* ========== 页面头部 Banner ========== */
.page-banner {
  background: linear-gradient(135deg, $color-primary 0%, $color-secondary 100%);
  color: white;
  padding: 48px 0;
}

.banner-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.banner-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    font-size: 32px;
  }
}

.banner-subtitle {
  font-size: 20px;
  opacity: 0.9;
  margin: 0;
}

/* ========== 主容器 ========== */
.page-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 24px;
}

.content-wrapper {
  display: flex;
  gap: 24px;
}

/* ========== 侧边栏筛选 ========== */
.sidebar-filter {
  width: 256px;
  flex-shrink: 0;
}

.filter-card {
  background: white;
  border-radius: 24px;
  padding: 24px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 96px;
}

.filter-header {
  font-size: 18px;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0 0 20px;
  display: flex;
  align-items: center;
  gap: 8px;

  i {
    color: $color-primary;
  }
}

.filter-section {
  margin-bottom: 24px;
}

.filter-title {
  font-size: 14px;
  font-weight: 600;
  color: $color-gray-700;
  margin: 0 0 12px;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-checkbox,
.filter-radio {
  display: flex;
  align-items: center;
  padding: 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.15s ease;

  &:hover {
    background-color: $color-gray-100;
  }

  &.active {
    background-color: rgba(74, 144, 226, 0.1);
  }

  input {
    width: 16px;
    height: 16px;
    accent-color: $color-primary;
    cursor: pointer;
  }

  span {
    margin-left: 8px;
    font-size: 14px;
    color: $color-gray-700;
  }

  .rating-stars {
    color: $color-warning;

    i {
      font-size: 12px;
    }
  }
}



/* ========== 主内容区 ========== */
.main-content {
  flex: 1;
  min-width: 0;
}

/* 搜索栏卡片 */
.search-bar-card {
  background: white;
  border-radius: 24px;
  padding: 16px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.search-bar-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.search-input-wrapper {
  flex: 1;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 48px;
  border: 2px solid $color-gray-200;
  border-radius: 16px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;

  &:hover {
    border-color: $color-primary;
  }

  &:focus {
    border-color: $color-primary;
    box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
  }
}

.search-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: $color-gray-600;
  font-size: 16px;
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-btn,
.reset-btn {
  padding: 12px 20px;
  border: 2px solid $color-gray-200;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  box-sizing: border-box;

  &:hover {
    border-color: $color-primary;
  }

  &:focus {
    outline: none;
    border-color: $color-primary;
  }
}

.search-btn {
  background: linear-gradient(135deg, $color-primary 0%, $color-secondary 100%);
  color: white;
  border-color: $color-primary;

  &:hover {
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  }
}

.reset-btn {
  background: white;
  color: $color-gray-700;

  &:hover {
    background: $color-gray-50;
  }
}

/* ========== 景点网格 ========== */
.scenic-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.scenic-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);

    .scenic-image img {
      transform: scale(1.1);
    }
  }
}

.scenic-image {
  position: relative;
  width: 100%;
  height: 240px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
}

.scenic-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  padding: 8px 16px;
  border-radius: 9999px;
  font-weight: 600;
  font-size: 14px;
  backdrop-filter: blur(10px);

  &.free {
    background: rgba(39, 174, 96, 0.9);
    color: white;
  }

  &.price {
    background: rgba(255, 159, 67, 0.9);
    color: white;
  }
}

.scenic-content {
  padding: 24px;
}

.scenic-name {
  font-size: 20px;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0 0 8px;
  line-height: 1.3;
}

.scenic-location {
  color: $color-gray-600;
  font-size: 14px;
  margin: 0 0 12px;

  i {
    margin-right: 4px;
  }
}

.scenic-desc {
  color: $color-gray-700;
  font-size: 15px;
  line-height: 1.5;
  margin: 0 0 16px;
}

.scenic-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
}

.scenic-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: $color-gray-600;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  i {
    font-size: 12px;
  }
}

.detail-link {
  color: $color-primary;
  font-weight: 600;
  font-size: 14px;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;

  &:hover {
    color: $color-secondary;
  }

  i {
    font-size: 12px;
    transition: transform 0.3s ease;
  }

  &:hover i {
    transform: translateX(4px);
  }
}

/* ========== 空状态 ========== */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 24px;
  font-weight: 600;
  color: $color-gray-800;
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 16px;
  color: $color-gray-600;
  margin: 0 0 24px;
}

.empty-action-btn {
  padding: 12px 24px;
  background: linear-gradient(135deg, $color-primary 0%, $color-secondary 100%);
  color: white;
  border: none;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  }
}

/* ========== 分页 ========== */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-btn {
  min-width: 40px;
  height: 40px;
  padding: 8px 16px;
  border: 2px solid $color-gray-300;
  background: white;
  color: $color-gray-700;
  border-radius: 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    border-color: $color-primary;
    color: $color-primary;
  }

  &.active {
    background: $color-primary;
    border-color: $color-primary;
    color: white;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

/* ========== 响应式设计 ========== */
@media (max-width: 1024px) {
  .scenic-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    flex-direction: column;
  }

  .sidebar-filter {
    width: 100%;
  }

  .filter-card {
    position: static;
  }

  .scenic-grid {
    grid-template-columns: 1fr;
  }

  .search-bar-content {
    flex-direction: column;
  }

  .banner-title {
    font-size: 28px;
  }

  .banner-subtitle {
    font-size: 16px;
  }
}
</style>