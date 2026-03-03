<template>
  <div class="ticket-list-page">
    <!-- 页面头部 - 橙色渐变背景 -->
    <div class="page-banner">
      <div class="banner-container">
        <h1 class="banner-title">
          <i class="fas fa-ticket-alt"></i>
          门票预订
        </h1>
        <p class="banner-subtitle">提前预订，享受优惠价格</p>
      </div>
    </div>

    <div class="content-container">
      <!-- 筛选栏 -->
      <div class="filter-section">
        <div class="filter-grid">
          <div class="filter-item">
            <label class="filter-label">景点类型</label>
            <el-select
                v-model="searchForm.ticketType"
                placeholder="全部类型"
                clearable
                class="filter-select"
            >
              <el-option label="成人票" value="成人票" />
              <el-option label="儿童票" value="儿童票" />
              <el-option label="学生票" value="学生票" />
              <el-option label="老人票" value="老人票" />
            </el-select>
          </div>
          <div class="filter-item">
            <label class="filter-label">价格区间</label>
            <el-select
                v-model="searchForm.priceRange"
                placeholder="全部价格"
                clearable
                class="filter-select"
            >
              <el-option label="¥0-100" value="0-100" />
              <el-option label="¥100-200" value="100-200" />
              <el-option label="¥200-500" value="200-500" />
              <el-option label="¥500以上" value="500+" />
            </el-select>
          </div>
          <div class="filter-item filter-action">
            <el-button
                type="primary"
                @click="searchTickets"
                class="search-button"
            >
              <i class="fas fa-search"></i>
              搜索
            </el-button>
          </div>
        </div>
      </div>



      <!-- 全部门票 -->
      <div class="all-tickets-section">
        <div class="section-header">
          <h2 class="section-title">
            <i class="fas fa-list"></i>
            全部门票
          </h2>
          <div class="sort-control">
            <span class="sort-label">排序:</span>
            <el-select
                v-model="sortType"
                @change="handleSortChange"
                class="sort-select"
            >
              <el-option label="综合推荐" value="default" />
              <el-option label="价格从低到高" value="price-asc" />
              <el-option label="价格从高到低" value="price-desc" />
              <el-option label="销量最高" value="sales" />
            </el-select>
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- 空状态 -->
        <div v-else-if="ticketList.length === 0" class="empty-state">
          <i class="fas fa-inbox empty-icon"></i>
          <h3 class="empty-title">暂无符合条件的门票</h3>
          <p class="empty-desc">试试调整搜索条件或浏览其他门票</p>
          <el-button type="primary" @click="resetSearch">
            <i class="fas fa-refresh"></i>
            重新搜索
          </el-button>
        </div>

        <!-- 门票卡片网格 -->
        <div v-else class="tickets-grid">
          <div
              v-for="ticket in ticketList"
              :key="ticket.id"
              class="ticket-card"
              @click="goToBooking(ticket.id)"
          >
            <div class="ticket-image-wrapper">
              <img
                  :src="getImageUrl(ticket.coverImage)"
                  :alt="ticket.ticketName"
                  class="ticket-image"
              >
              <div class="ticket-badge" v-if="ticket.isHot">限时优惠</div>
            </div>
            <div class="ticket-body">
              <h3 class="ticket-title">{{ ticket.ticketName }}</h3>
              <p class="ticket-location">
                <i class="fas fa-map-marker-alt"></i>
                {{ ticket.scenicName || '未知地点' }}
              </p>
              <div class="ticket-stats">
                <span v-if="ticket.salesCount">
                  <i class="fas fa-users"></i>
                  {{ formatSalesCount(ticket.salesCount) }}
                </span>
                <span v-if="ticket.stock">
                  <i class="fas fa-ticket-alt"></i>
                  余票 {{ ticket.stock }}
                </span>
              </div>
              <div class="ticket-footer">
                <div class="ticket-price">
                  <div class="price-original-small" v-if="ticket.discountPrice">
                    ¥{{ ticket.price }}
                  </div>
                  <div class="price-value">¥{{ ticket.discountPrice || ticket.price }}</div>
                </div>
                <button class="book-button" @click.stop="goToBooking(ticket.id)">
                  预订
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              :page-size="pageSize"
              :current-page="currentPage"
              @current-change="handleCurrentChange"
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

const router = useRouter()
const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'

// 分页参数
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)

// 门票列表数据
const ticketList = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  ticketName: '',
  ticketType: '',
  priceRange: '',
  scenicId: null
})

// 排序类型
const sortType = ref('default')

// 景点选择器数据
const scenicOptions = ref([])
const scenicLoading = ref(false)



// 获取门票列表
const fetchTickets = async () => {
  loading.value = true
  try {
    await request.get('/ticket/page', {
      ticketName: searchForm.ticketName,
      ticketType: searchForm.ticketType,
      priceRange: searchForm.priceRange,
      scenicId: searchForm.scenicId,
      sortType: sortType.value,
      currentPage: currentPage.value,
      size: pageSize.value
    }, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        ticketList.value = res.records||[]
        total.value = res.total||0
      }
    })
  } catch (error) {
    console.error('获取门票列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索景点选项
const fetchScenicOptions = async (query) => {
  if (query === '') {
    scenicOptions.value = []
    return
  }

  scenicLoading.value = true
  try {
    await request.get('/scenic/page', {
      name: query,
      currentPage: 1,
      size: 20
    }, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        scenicOptions.value = res.records || []
      }
    })
  } catch (error) {
    console.error('获取景点列表失败:', error)
  } finally {
    scenicLoading.value = false
  }
}

// 格式化销售数量
const formatSalesCount = (count) => {
  if (!count) return '0'
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + 'w'
  } else if (count >= 1000) {
    return (count / 1000).toFixed(1) + 'k'
  }
  return count.toString()
}

// 搜索按钮点击事件
const searchTickets = () => {
  currentPage.value = 1
  fetchTickets()
}

// 排序变化处理
const handleSortChange = () => {
  currentPage.value = 1
  fetchTickets()
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.ticketName = ''
  searchForm.ticketType = ''
  searchForm.priceRange = ''
  searchForm.scenicId = null
  sortType.value = 'default'
  currentPage.value = 1
  fetchTickets()
}

// 分页变化事件
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchTickets()
}

// 前往预订页面
const goToBooking = (ticketId) => {
  router.push(`/ticket/booking/${ticketId}`)
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop'
  return url.startsWith('http') ? url : baseAPI + url
}

// 页面加载时获取数据
onMounted(() => {
  fetchTickets()
})
</script>

<style lang="scss" scoped>
.ticket-list-page {
  min-height: 100vh;
  background: #f8f9fa;
}

/* 页面头部 - 蓝色渐变背景 */
.page-banner {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  padding: 48px 0;
}

.banner-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
}

.banner-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 16px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.banner-subtitle {
  font-size: 20px;
  opacity: 0.9;
  margin: 0;
}

/* 内容容器 */
.content-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 32px 24px;
}

/* 筛选栏 */
.filter-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 32px;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  align-items: end;
}

.filter-item {
  display: flex;
  flex-direction: column;
}

.filter-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.filter-select {
  width: 100%;

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    border: 2px solid #e5e7eb;
    padding: 8px 16px;
    transition: all 0.3s;

    &:hover {
      border-color: #4A90E2;
    }

    &.is-focus {
      border-color: #4A90E2;
      box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
    }
  }
}

.filter-action {
  display: flex;
  align-items: flex-start;
}

.search-button {
  width: 100%;
  height: 36px;
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 15px rgba(74, 144, 226, 0.4);
    transform: translateY(-1px);
  }
}

/* 热门推荐区域 */
.hot-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 24px;
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    color: #4A90E2;
  }
}

.hot-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.hot-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }
}

.hot-card-content {
  display: flex;
  height: 100%;
}

.hot-card-image {
  width: 192px;



  img {
    width: 100%;
    height: 100%;
    // 拉伸
    object-fit:cover ;
  }
}

.hot-card-body {
  flex: 1;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.hot-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.hot-card-title {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px;
}

.hot-card-location {
  font-size: 14px;
  color: #6b7280;
  margin: 0;

  i {
    color: #f97316;
    margin-right: 4px;
  }
}

.hot-badge {
  padding: 6px 12px;
  background: #fee2e2;
  color: #dc2626;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.hot-card-description {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
  margin: 0 0 auto;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hot-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.price-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-original {
  font-size: 14px;
  color: #9ca3af;
  text-decoration: line-through;
}

.price-main {
  font-size: 28px;
  font-weight: 700;
  color: #f97316;
}

.price-unit {
  font-size: 14px;
  font-weight: 400;
  color: #6b7280;
  margin-left: 4px;
}

.booking-button {
  padding: 10px 24px;
  background: linear-gradient(to right, #f97316, #ea580c);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    box-shadow: 0 4px 15px rgba(249, 115, 22, 0.4);
  }
}

.hot-card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #6b7280;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  i {
    font-size: 14px;
  }
}

/* 全部门票区域 */
.all-tickets-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.sort-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 14px;
  color: #6b7280;
}

.sort-select {
  width: 180px;

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    border: 2px solid #e5e7eb;

    &:hover {
      border-color: #4A90E2;
    }

    &.is-focus {
      border-color: #4A90E2;
    }
  }
}

/* 加载状态 */
.loading-state {
  padding: 40px;
  background: white;
  border-radius: 16px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 40px;
  background: white;
  border-radius: 16px;
}

.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 24px;
}

/* 门票卡片网格 - 3列布局 */
.tickets-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.ticket-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }
}

.ticket-image-wrapper {
  position: relative;
  height: 192px;
  overflow: hidden;
}

.ticket-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ticket-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  background: #f97316;
  color: white;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.ticket-body {
  padding: 20px;
}

.ticket-title {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ticket-location {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 12px;

  i {
    color: #4A90E2;
    margin-right: 4px;
  }
}

.ticket-stats {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 16px;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  i {
    font-size: 14px;
  }
}

.ticket-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.ticket-price {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.price-original-small {
  font-size: 12px;
  color: #9ca3af;
  text-decoration: line-through;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #4A90E2;
}

.book-button {
  padding: 8px 16px;
  background: #4A90E2;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: #357ABD;
  }
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;

  :deep(.el-pagination) {
    .btn-prev,
    .btn-next,
    .el-pager li {
      border-radius: 8px;
      border: 2px solid #e5e7eb;
      margin: 0 4px;
      min-width: 40px;
      height: 40px;
      line-height: 36px;
      transition: all 0.3s;

      &:hover {
        border-color: #4A90E2;
        color: #4A90E2;
      }

      &.is-active {
        background: #4A90E2;
        border-color: #4A90E2;
        color: white;
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .tickets-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .hot-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .tickets-grid {
    grid-template-columns: 1fr;
  }

  .filter-grid {
    grid-template-columns: 1fr;
  }

  .hot-card-content {
    flex-direction: column;
  }

  .hot-card-image {
    width: 100%;
    height: 200px;
  }
}
</style>