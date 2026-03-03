<template>
  <div class="accommodation-page">
    <!-- 页面头部 -->
    <div class="page-banner">
      <div class="banner-container">
        <h1 class="banner-title">
          <i class="fa fa-hotel"></i>
          周边住宿
        </h1>
        <p class="banner-subtitle">精选优质酒店，舒适旅居体验</p>
      </div>
    </div>

    <div class="page-container">
      <!-- 搜索筛选栏 -->
      <div class="search-bar">
        <div class="search-grid">
          <div class="search-item">
            <label class="search-label">住宿名称</label>
            <el-input
                v-model="searchForm.name"
                placeholder="搜索住宿名称"
                clearable
                @keyup.enter="handleSearch"
            />
          </div>
          <div class="search-item">
            <label class="search-label">关联景点</label>
            <el-select
                v-model="filters.scenicId"
                placeholder="选择景点"
                clearable
                style="width: 100%"
            >
              <el-option
                  v-for="item in scenicOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
              />
            </el-select>
          </div>
          <div class="search-item">
            <label class="search-label">价格区间</label>
            <el-select v-model="filters.priceRange" placeholder="不限" clearable>
              <el-option label="不限" value="" />
              <el-option label="¥0-200" value="0-200" />
              <el-option label="¥200-500" value="200-500" />
              <el-option label="¥500-1000" value="500-1000" />
              <el-option label="¥1000以上" value="1000+" />
            </el-select>
          </div>
          <div class="search-item search-btn-wrapper">
            <button class="btn-search" @click="handleSearch">
              <i class="fa fa-search"></i>
              搜索
            </button>
          </div>
        </div>
      </div>

      <div class="content-layout">
        <!-- 侧边栏筛选 -->
        <aside class="sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">
              <i class="fa fa-filter"></i>
              筛选条件
            </h3>

            <!-- 酒店类型 -->
            <div class="filter-section">
              <h4 class="filter-title">酒店类型</h4>
              <div class="filter-options">
                <label
                    v-for="type in typeOptions"
                    :key="type"
                    class="filter-option"
                >
                  <input
                      type="checkbox"
                      :value="type"
                      v-model="selectedTypes"
                      @change="handleSearch"
                  />
                  <span>{{ type }}</span>
                </label>
              </div>
            </div>

            <!-- 酒店星级 -->
            <div class="filter-section">
              <h4 class="filter-title">酒店星级</h4>
              <div class="filter-options">
                <label
                    v-for="star in [5, 4, 3]"
                    :key="star"
                    class="filter-option"
                >
                  <input
                      type="checkbox"
                      :value="star"
                      v-model="selectedStars"
                      @change="handleSearch"
                  />
                  <span class="star-rating">
                    <i v-for="n in star" :key="n" class="fa fa-star"></i>
                    <span class="star-text">{{ star }}星级</span>
                  </span>
                </label>
              </div>
            </div>

            <button class="btn-apply-filter" @click="handleSearch">应用筛选</button>
          </div>
        </aside>

        <!-- 主内容区 -->
        <main class="main-content">
          <!-- 排序栏 -->
          <div class="sort-bar">
            <div class="result-count">
              找到 <span class="count-number">{{ total }}</span> 家酒店
            </div>
            <div class="sort-options">
              <span class="sort-label">排序:</span>
              <el-select v-model="sortBy" @change="handleSearch" class="sort-select">
                <el-option label="综合推荐" value="default" />
                <el-option label="价格从低到高" value="price_asc" />
                <el-option label="价格从高到低" value="price_desc" />
                <el-option label="评分最高" value="rating_desc" />
              </el-select>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="loading-wrapper">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 住宿列表 -->
          <div v-else-if="accommodationList && accommodationList.length > 0" class="accommodation-list">
            <div
                v-for="item in accommodationList"
                :key="item.id"
                class="accommodation-item"
                @click="goToDetail(item.id)"
            >
              <div class="item-image">
                <img :src="getImageUrl(item.imageUrl)" :alt="item.name" />
              </div>
              <div class="item-content">
                <div class="item-header">
                  <div class="item-title-section">
                    <h3 class="item-title">{{ item.name }}</h3>
                    <span v-if="item.starLevel" class="item-stars">
                      <i v-for="n in parseInt(item.starLevel)" :key="n" class="fa fa-star"></i>
                    </span>
                  </div>
                  <span v-if="item.type" class="item-badge">{{ item.type }}</span>
                </div>

                <p class="item-location">
                  <i class="fa fa-map-marker-alt"></i>
                  {{ item.address || '地址待更新' }}
                  <span v-if="item.scenicName"> - 距离{{ item.scenicName }} {{ item.distance || '2.5km' }}</span>
                </p>

                <p class="item-description">
                  {{ truncateText(item.features || '酒店环境优美，设施齐全。拥有温泉、SPA、健身房等配套设施，是您旅游度假的理想选择。', 100) }}
                </p>

                <div class="item-footer">
                  <div class="item-rating-section">
                    <div class="rating-score">
                      <i class="fa fa-star"></i>
                      {{ getDisplayRating(item.starLevel) }}
                    </div>
                    <span class="rating-count">{{ item.reviewCount || '1.2k' }}条评价</span>
                  </div>
                  <div class="item-price-section">
                    <div class="price-label">最低价</div>
                    <div class="price-value">
                      ¥{{ item.priceRange || '688' }}
                      <span class="price-unit">/晚</span>
                    </div>
                    <button class="btn-detail" @click.stop="goToDetail(item.id)">
                      查看详情
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <div class="empty-icon">
              <i class="fa fa-hotel"></i>
            </div>
            <h3 class="empty-title">暂无住宿信息</h3>
            <p class="empty-desc">试试调整搜索条件或浏览其他选项</p>
            <button class="btn-reset" @click="resetSearch">重新搜索</button>
          </div>

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
                background
                layout="prev, pager, next"
                :current-page="currentPage"
                :page-size="pageSize"
                :total="total"
                @current-change="handleCurrentChange"
            />
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { Location, Star, Picture, Search, Refresh, MapLocation } from '@element-plus/icons-vue'

const router = useRouter()
const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'

// 数据状态
const loading = ref(false)
const accommodationList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const scenicOptions = ref([])
const typeOptions = ref([])

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 筛选条件
const filters = reactive({
  scenicId: '',
  type: '',
  minPrice: '',
  maxPrice: '',
  minRating: 0,
  priceRange: ''
})

// 新增筛选变量
const selectedTypes = ref([])
const selectedStars = ref([])
const sortBy = ref('default')

// 评分颜色
const colors = ['#99A9BF', '#F7BA2A', '#FF9900']

// 获取住宿列表
const fetchAccommodations = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params = {
      currentPage: currentPage.value,
      size: pageSize.value
    }

    // 添加搜索条件
    if (searchForm.name) params.name = searchForm.name

    // 添加筛选条件
    if (filters.scenicId) params.scenicId = filters.scenicId

    // 添加酒店类型筛选（支持多选）
    if (selectedTypes.value && selectedTypes.value.length > 0) {
      // 如果只选了一个类型，直接传递
      if (selectedTypes.value.length === 1) {
        params.type = selectedTypes.value[0]
      } else {
        // 多选时，后端需要支持多个类型，这里先传第一个（需要后端支持）
        params.type = selectedTypes.value[0]
        // TODO: 如果需要支持多类型筛选，后端需要修改为接收数组
      }
    }

    // 处理价格区间
    if (filters.priceRange) {
      if (filters.priceRange === '1000+') {
        params.minPrice = '1000'
        params.maxPrice = ''
      } else {
        const [min, max] = filters.priceRange.split('-')
        params.minPrice = min
        params.maxPrice = max
      }
    }

    // 添加酒店星级筛选（注意：后端参数名为minRating，但实际筛选的是酒店星级starLevel）
    // 取选中星级中的最小值作为最低星级，例如选中4星和5星，则显示4星及以上的酒店
    if (selectedStars.value && selectedStars.value.length > 0) {
      params.minRating = Math.min(...selectedStars.value)
    }

    // 添加排序方式
    if (sortBy.value && sortBy.value !== 'default') {
      params.sortBy = sortBy.value
    }

    // 发送请求
    await request.get('/accommodation/page', params, {
      onSuccess: (res) => {
        accommodationList.value = res.records||[]
        total.value = res.total||0
      }
    })
  } catch (error) {
    console.error('获取住宿列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取景点列表（用于筛选）
const fetchScenicOptions = async () => {
  try {
    await request.get('/scenic/all', {}, {
      onSuccess: (res) => {
        scenicOptions.value = res||[]
      }
    })
  } catch (error) {
    console.error('获取景点列表失败:', error)
  }
}

// 获取住宿类型列表（用于筛选）
const fetchAccommodationTypes = async () => {
  try {
    await request.get('/accommodation/types', {}, {
      onSuccess: (res) => {
        typeOptions.value = res||[]
      }
    })
  } catch (error) {
    console.error('获取住宿类型列表失败:', error)
  }
}

// 处理图片URL
const getImageUrl = (url) => {
  if (!url) return require('@/assets/images/no-image.png')
  if (url.startsWith('http')) return url
  return baseAPI + url
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  fetchAccommodations()
}

// 重置搜索和筛选条件
const resetSearch = () => {
  searchForm.name = ''
  filters.scenicId = ''
  filters.type = ''
  filters.minPrice = ''
  filters.maxPrice = ''
  filters.minRating = 0
  filters.priceRange = ''
  selectedTypes.value = []
  selectedStars.value = []
  sortBy.value = 'default'
  currentPage.value = 1
  fetchAccommodations()
}

// 筛选处理（保持兼容性）
const handleFilter = () => {
  handleSearch()
}

// 重置筛选条件（保持兼容性）
const resetFilter = () => {
  resetSearch()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchAccommodations()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchAccommodations()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/accommodation/${id}`)
}

// 截取文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 获取评分显示
const getDisplayRating = (rating) => {
  if (!rating) return '4.5'
  return parseFloat(rating).toFixed(1)
}

// 初始加载
onMounted(() => {
  fetchScenicOptions()
  fetchAccommodationTypes()
  fetchAccommodations()
})
</script>

<style lang="scss" scoped>
.accommodation-page {
  min-height: 100vh;
  background: #f8f9fa;
  overflow: visible;
}

/* 页面头部横幅 */
.page-banner {
  background: linear-gradient(135deg, #9333ea 0%, #a855f7 100%);
  color: white;
  padding: 48px 0;
}

.banner-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.banner-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    font-size: 28px;
  }
}

.banner-subtitle {
  font-size: 18px;
  margin: 0;
  opacity: 0.9;
}

/* 页面容器 */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  overflow: visible;
}

/* 搜索栏 */
.search-bar {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.search-grid {
  display: grid;
  grid-template-columns: 1fr 200px 200px auto;
  gap: 16px;
  align-items: end;
}

.search-item {
  display: flex;
  flex-direction: column;
}

.search-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
  margin-bottom: 4px;
}

.search-btn-wrapper {
  display: flex;
  align-items: flex-end;
}

.btn-search {
  width: 100%;
  padding: 10px 24px;
  background: linear-gradient(135deg, #9333ea 0%, #a855f7 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &:hover {
    box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
    transform: translateY(-1px);
  }
}

/* 内容布局 */
.content-layout {
  display: flex;
  gap: 24px;
  align-items: flex-start;
  overflow: visible;
  position: relative;
}

/* 侧边栏 */
.sidebar {
  width: 256px;
  flex-shrink: 0;
  height: fit-content;
  position: sticky;
  top: 80px;
  z-index: 100;
}

.sidebar-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  max-height: calc(100vh - 96px);
  overflow-y: auto;

  /* 优化滚动条样式 */
  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 3px;

    &:hover {
      background: #94a3b8;
    }
  }
}

.sidebar-title {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 8px;

  i {
    color: #9333ea;
  }
}

.filter-section {
  margin-bottom: 24px;
}

.filter-title {
  font-size: 14px;
  font-weight: 600;
  color: #4b5563;
  margin: 0 0 12px;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-option {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background 0.2s ease;

  &:hover {
    background: #f9fafb;
  }

  input[type="checkbox"] {
    width: 16px;
    height: 16px;
    accent-color: #9333ea;
    cursor: pointer;
  }

  span {
    margin-left: 8px;
    font-size: 14px;
    color: #4b5563;
  }
}

.star-rating {
  color: #fbbf24;
  display: flex;
  align-items: center;
  gap: 2px;

  i {
    font-size: 14px;
  }

  .star-text {
    margin-left: 4px;
    color: #4b5563;
    font-size: 14px;
  }
}

.btn-apply-filter {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, #9333ea 0%, #a855f7 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
  }
}

/* 主内容区 */
.main-content {
  flex: 1;
}

/* 排序栏 */
.sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.result-count {
  font-size: 14px;
  color: #6b7280;
}

.count-number {
  font-weight: 600;
  color: #111827;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 14px;
  color: #6b7280;
}

.sort-select {
  width: 160px;
}

/* 加载状态 */
.loading-wrapper {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 住宿列表 */
.accommodation-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.accommodation-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;



}

.item-image {
  width: 280px;
  height: 280px;
  flex-shrink: 0;
  overflow: hidden;
  border-radius: 12px 0 0 12px;
  background: #f3f4f6;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
  }
}


.item-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.item-title-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-title {
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.item-stars {
  color: #fbbf24;
  display: flex;
  gap: 2px;

  i {
    font-size: 14px;
  }
}

.item-badge {
  padding: 4px 12px;
  background: #dc2626;
  color: white;
  border-radius: 9999px;
  font-size: 12px;
  font-weight: 600;
}

.item-location {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;

  i {
    color: #9333ea;
  }
}

.item-description {
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  padding-top: 8px;
}

.item-rating-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-score {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  font-weight: 600;
  color: #111827;

  i {
    color: #fbbf24;
  }
}

.rating-count {
  font-size: 14px;
  color: #6b7280;
}

.item-price-section {
  text-align: right;
}

.price-label {
  font-size: 12px;
  color: #9ca3af;
  margin-bottom: 4px;
}

.price-value {
  font-size: 20px;
  font-weight: 700;
  color: #9333ea;
  margin-bottom: 8px;
}

.price-unit {
  font-size: 14px;
  font-weight: 400;
  color: #6b7280;
}

.btn-detail {
  padding: 8px 16px;
  background: linear-gradient(135deg, #9333ea 0%, #a855f7 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;

  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
  }
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-icon {
  font-size: 64px;
  color: #d1d5db;
  margin-bottom: 16px;

  i {
    font-size: 64px;
  }
}

.empty-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 24px;
}

.btn-reset {
  padding: 10px 24px;
  background: linear-gradient(135deg, #9333ea 0%, #a855f7 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(147, 51, 234, 0.3);
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
      transition: all 0.3s ease;

      &:hover {
        background: #9333ea;
        color: white;
      }
    }

    .el-pager li.is-active {
      background: #9333ea;
      color: white;
    }
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .content-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .sidebar-card {
    position: static;
  }
}

@media (max-width: 768px) {
  .search-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .accommodation-item {
    flex-direction: column;
  }

  .item-image {
    width: 100%;
    height: 200px;
  }

  .item-footer {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .item-price-section {
    text-align: left;
  }

  .btn-detail {
    width: 100%;
  }
}
</style>
