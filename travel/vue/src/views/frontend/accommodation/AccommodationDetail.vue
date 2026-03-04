<template>
  <div class="accommodation-detail-page">
    <!-- Hero区域 - 只显示主图 -->
    <div class="hero-section" v-if="accommodation">
      <img :src="getImageUrl(mainImage)" :alt="accommodation.name" class="hero-image" />
      <div class="hero-overlay">
        <div class="hero-content">
          <!-- 面包屑 -->
          <div class="breadcrumb-wrapper">
            <nav class="breadcrumb-nav">
              <ol class="breadcrumb-list">
                <li class="breadcrumb-item">
                  <a @click="$router.push('/')" class="breadcrumb-link">首页</a>
                </li>
                <li class="breadcrumb-separator">/</li>
                <li class="breadcrumb-item">
                  <a @click="$router.push('/accommodation')" class="breadcrumb-link">住宿推荐</a>
                </li>
                <li class="breadcrumb-separator">/</li>
                <li class="breadcrumb-item breadcrumb-current">{{ accommodation.name }}</li>
              </ol>
            </nav>
          </div>

          <!-- 标题 -->
          <h1 class="hero-title">{{ accommodation.name }}</h1>

          <!-- 元信息 -->
          <div class="hero-meta">
            <div class="meta-item">
              <i class="fas fa-map-marker-alt"></i>
              <span>{{ accommodation.address }}</span>
            </div>
            <div class="meta-item" v-if="accommodation.type">
              <i class="fas fa-hotel"></i>
              <span>{{ accommodation.type }}</span>
            </div>
            <div class="meta-item">
              <i class="fas fa-star star-icon"></i>
              <span>{{ getDisplayRating(accommodation.starLevel) }}</span>
              <span class="review-count">({{ formatReviewCount(reviewTotal) }})</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="hero-actions">
            <button class="btn-primary-action" @click="showReviewDialog = true">
              <i class="fas fa-edit"></i>
              发表评价
            </button>
            <button class="btn-secondary-action">
              <i class="fas fa-share-alt"></i>
              分享
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-wrapper">
      <div class="page-container">
        <el-skeleton :rows="20" animated />
      </div>
    </div>

    <!-- 详细信息区域 -->
    <div class="detail-section" v-else-if="accommodation">
      <div class="page-container">
        <div class="detail-layout">
          <!-- 左侧主要内容 -->
          <div class="content-main">
            <!-- 图片展示 -->
            <div class="content-card image-gallery-card" v-if="imageList.length > 0">
              <!-- 主图展示 -->
              <div class="main-image-container">
                <img :src="getImageUrl(currentImage.url)" :alt="accommodation.name" class="main-image" />

                <!-- 评分标签 -->
                <div class="rating-badge">
                  <i class="fas fa-star"></i>
                  <span class="rating-score">{{ getDisplayRating(accommodation.averageRating) }}</span>
                </div>
              </div>

              <!-- 缩略图列表 -->
              <div class="thumbnail-list" v-if="imageList.length > 1">
                <div
                    v-for="(image, index) in imageList"
                    :key="index"
                    class="thumbnail-item"
                    :class="{ 'active': index === currentImageIndex }"
                    @click="currentImageIndex = index"
                >
                  <img :src="getImageUrl(image.url)" :alt="`图片${index + 1}`" class="thumbnail-image" />
                </div>
              </div>
            </div>

            <!-- 住宿描述 -->
            <div class="content-card">
              <h3 class="section-title">
                <i class="fas fa-file-alt title-icon"></i>
                住宿介绍
              </h3>
              <div class="section-content">
                <p class="description-text">{{ accommodation.description || '暂无详细描述' }}</p>
              </div>
            </div>

            <!-- 特色服务 -->
            <div class="content-card">
              <h3 class="section-title">
                <i class="fas fa-concierge-bell title-icon"></i>
                特色服务
              </h3>
              <div class="section-content">
                <p class="features-text">{{ accommodation.features || '暂无特色服务介绍' }}</p>
              </div>
            </div>

            <!-- 用户评价 -->
            <div class="content-card reviews-section">
              <!-- 标题 -->
              <div class="reviews-header">
                <h3 class="section-title">
                  <i class="fas fa-comments title-icon"></i>
                  用户评价
                </h3>
                <span class="review-total">共 {{ reviewTotal || 0 }} 条评论</span>
              </div>

              <!-- 评分概览 -->
              <div class="rating-overview" v-if="!loadingReviews && accommodation.averageRating">
                <div class="rating-summary">
                  <div class="rating-left">
                    <div class="rating-number">{{ getDisplayRating(accommodation.averageRating) }}</div>
                    <div class="rating-stars-large">
                      <i v-for="n in 5" :key="n" class="fas fa-star" :class="{ 'star-filled': n <= Math.round(accommodation.averageRating) }"></i>
                    </div>
                    <div class="rating-text">基于 {{ reviewTotal || 0 }} 条评价</div>
                  </div>

                  <!-- 星级分布 -->
                  <div class="rating-distribution" v-if="ratingStats">
                    <div v-for="star in [5, 4, 3, 2, 1]" :key="star" class="rating-bar">
                      <span class="star-label">{{ star }}星</span>
                      <div class="bar-container">
                        <div class="bar-fill" :style="{ width: getRatingPercentage(star) + '%' }"></div>
                      </div>
                      <span class="star-percentage">{{ getRatingPercentage(star) }}%</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 加载状态 -->
              <div v-if="loadingReviews" class="reviews-loading">
                <el-skeleton :rows="3" animated />
              </div>

              <!-- 空状态 -->
              <div v-else-if="!reviewList || reviewList.length === 0" class="reviews-empty">
                <div class="empty-icon">💬</div>
                <h4 class="empty-title">暂无评价</h4>
                <p class="empty-desc">成为第一个评价这家住宿的客人</p>
              </div>

              <!-- 评价列表 -->
              <div v-else class="reviews-list">
                <div v-for="(review, index) in reviewList" :key="index" class="review-item">
                  <div class="review-container">
                    <img :src="getAvatarUrl(review.avatar)" :alt="review.username" class="reviewer-avatar" />
                    <div class="review-content">
                      <div class="review-header">
                        <div class="reviewer-info">
                          <div class="reviewer-name">{{ review.username || '匿名用户' }}</div>
                          <div class="review-time">{{ formatDate(review.createTime) }}</div>
                        </div>
                        <div class="review-rating">
                          <div class="stars-display">
                            <i v-for="n in 5" :key="n" class="fas fa-star" :class="{ 'star-filled': n <= review.rating, 'star-empty': n > review.rating }"></i>
                          </div>
                          <span class="rating-score">{{ review.rating }}分</span>
                        </div>
                      </div>

                      <p class="review-text">{{ review.content }}</p>

                      <div class="review-actions" v-if="canDeleteReview(review)">
                        <button class="btn-delete-review" @click="handleDeleteReview(review.id)">
                          <i class="fas fa-trash"></i>删除
                        </button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 分页 -->
                <div class="reviews-pagination" v-if="reviewTotal > reviewPageSize">
                  <nav class="pagination-nav">
                    <button
                        class="pagination-btn"
                        :disabled="reviewPage === 1"
                        @click="reviewPage--; fetchAccommodationReviews()"
                    >
                      <i class="fas fa-chevron-left"></i>
                    </button>

                    <button
                        v-for="page in totalPages"
                        :key="page"
                        class="pagination-btn"
                        :class="{ 'active': page === reviewPage }"
                        @click="reviewPage = page; fetchAccommodationReviews()"
                    >
                      {{ page }}
                    </button>

                    <button
                        class="pagination-btn"
                        :disabled="reviewPage === totalPages"
                        @click="reviewPage++; fetchAccommodationReviews()"
                    >
                      <i class="fas fa-chevron-right"></i>
                    </button>
                  </nav>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧信息栏 -->
          <div class="content-sidebar">
            <div class="sidebar-card-wrapper">
              <!-- 基本信息 -->
              <div class="sidebar-section">
                <h3 class="sidebar-title">
                  <i class="fas fa-info-circle title-icon"></i>
                  基本信息
                </h3>
                <div class="info-items">
                  <div class="info-row">
                    <div class="info-label">
                      <i class="fas fa-money-bill-wave"></i>
                      价格区间
                    </div>
                    <div class="info-value price-highlight">{{ accommodation.priceRange }}</div>
                  </div>
                  <div class="info-row">
                    <div class="info-label">
                      <i class="fas fa-phone"></i>
                      联系电话
                    </div>
                    <div class="info-value">{{ accommodation.contactPhone || '暂无' }}</div>
                  </div>
                  <div class="info-row" v-if="accommodation.scenicName">
                    <div class="info-label">
                      <i class="fas fa-map-marker-alt"></i>
                      附近景点
                    </div>
                    <div class="info-value scenic-clickable" @click="goToScenic(accommodation.scenicId)">
                      {{ accommodation.scenicName }}
                      <span v-if="accommodation.distance" class="distance-badge">（{{ accommodation.distance }}）</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 周边景点推荐 -->
              <div class="sidebar-section" v-if="nearbyScenics.length > 0">
                <h3 class="sidebar-title">
                  <i class="fas fa-map-marker-alt title-icon"></i>
                  周边景点
                </h3>

                <div v-if="loadingScenics" class="sidebar-loading">
                  <el-skeleton :rows="3" animated />
                </div>

                <div v-else class="recommendation-list">
                  <a v-for="scenic in nearbyScenics" :key="scenic.id" class="recommendation-item" @click="goToScenic(scenic.id)">
                    <img :src="getImageUrl(scenic.imageUrl)" :alt="scenic.name" class="recommendation-image" />
                    <div class="recommendation-info">
                      <div class="recommendation-name">{{ scenic.name }}</div>
                      <div class="recommendation-price">{{ scenic.price ? `￥${scenic.price}` : '免费' }}</div>
                    </div>
                  </a>
                </div>
              </div>

              <!-- 更多住宿推荐 -->
              <div class="sidebar-section" v-if="similarAccommodations.length > 0">
                <h3 class="sidebar-title">
                  <i class="fas fa-hotel title-icon"></i>
                  更多住宿
                </h3>

                <div v-if="loadingSimilar" class="sidebar-loading">
                  <el-skeleton :rows="3" animated />
                </div>

                <div v-else class="recommendation-list">
                  <a
                      v-for="item in similarAccommodations"
                      :key="item.id"
                      class="recommendation-item"
                      @click="goToAccommodation(item.id)"
                  >
                    <img :src="getImageUrl(item.imageUrl)" :alt="item.name" class="recommendation-image" />
                    <div class="recommendation-info">
                      <div class="recommendation-name">{{ item.name }}</div>
                      <div class="recommendation-stars">
                        <i v-for="n in 5" :key="n" class="fas fa-star" :class="{ 'star-filled': n <= item.starLevel, 'star-empty': n > item.starLevel }"></i>
                      </div>
                      <div class="recommendation-price">{{ item.priceRange }}</div>
                    </div>
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="not-found-section">
      <div class="page-container">
        <div class="not-found-content">
          <div class="not-found-icon">🏨</div>
          <h3 class="not-found-title">住宿信息不存在</h3>
          <p class="not-found-desc">该住宿可能已下线或不存在</p>
          <button class="btn-back" @click="$router.push('/accommodation')">
            返回住宿列表
          </button>
        </div>
      </div>
    </div>

    <!-- 评价对话框 -->
    <el-dialog
        v-model="showReviewDialog"
        title="发表评价"
        width="500px"
        :close-on-click-modal="false"
    >
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-position="top">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewForm.rating" :colors="colors" show-score />
        </el-form-item>

        <el-form-item label="评价内容" prop="content">
          <el-input
              v-model="reviewForm.content"
              type="textarea"
              :rows="4"
              placeholder="请分享您的住宿体验..."
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" :loading="submittingReview" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import {
  Location, Star, Picture, Phone, Delete, House, MapLocation,
  Document, InfoFilled, Money, ChatDotRound, EditPen, Share
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'

// 数据状态
const accommodation = ref(null)
const loading = ref(false)
const reviewList = ref([])
const reviewTotal = ref(0)
const reviewPage = ref(1)
const reviewPageSize = ref(10)
const loadingReviews = ref(false)
const nearbyScenics = ref([])
const loadingScenics = ref(false)
const similarAccommodations = ref([])
const loadingSimilar = ref(false)

// 图片列表相关
const imageList = ref([])
const currentImageIndex = ref(0)

// 评分统计
const ratingStats = ref(null)

// 主图（Hero区域显示）
const mainImage = computed(() => {
  if (imageList.value.length === 0) {
    return accommodation.value?.imageUrl || ''
  }
  const main = imageList.value.find(img => img.isMain)
  return main ? main.url : imageList.value[0].url
})

// 当前选中的图片（详情页大图）
const currentImage = computed(() => {
  if (imageList.value.length === 0) return { url: '', isMain: false }
  return imageList.value[currentImageIndex.value] || imageList.value[0]
})

// 评分颜色
const colors = ['#99A9BF', '#F7BA2A', '#FF9900']

// 评价表单
const showReviewDialog = ref(false)
const submittingReview = ref(false)
const reviewForm = reactive({
  accommodationId: parseInt(route.params.id),
  rating: 5,
  content: ''
})

const reviewRules = {
  rating: [
    { required: true, message: '请选择评分', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入评价内容', trigger: 'blur' },
    { min: 5, max: 500, message: '评价内容长度在5到500个字符之间', trigger: 'blur' }
  ]
}

// 检查是否为当前用户评价或管理员
const canDelete = (review) => {
  if (!userStore.userInfo) return false
  return userStore.userInfo.id === review.userId || userStore.userInfo.roleCode === 'ADMIN'
}

// 格式化评价数量
const formatReviewCount = (count) => {
  if (!count || count === 0) return '暂无评价'
  if (count === 1) return '1条评价'
  return `${count}条评价`
}

// 获取评分显示
const getDisplayRating = (rating) => {
  if (!rating) return '4.5'
  return parseFloat(rating).toFixed(1)
}

// 解析图片列表
const parseImageList = (imageListStr) => {
  if (!imageListStr) return []
  try {
    const list = JSON.parse(imageListStr)
    return Array.isArray(list) ? list : []
  } catch (e) {
    console.error('解析图片列表失败:', e)
    return []
  }
}

// 获取住宿详情
const fetchAccommodationDetail = async () => {
  loading.value = true
  try {
    await request.get(`/accommodation/${route.params.id}`, {}, {
      onSuccess: (res) => {
        accommodation.value = res
        // 解析图片列表
        imageList.value = parseImageList(res.imageList)
        // 如果没有图片列表但有单张图片，创建一个包含单张图片的列表
        if (imageList.value.length === 0 && res.imageUrl) {
          imageList.value = [{ url: res.imageUrl, isMain: true }]
        }
      }
    })
  } catch (error) {
    console.error('获取住宿详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 计算星级百分比
const getRatingPercentage = (star) => {
  if (!ratingStats.value || reviewTotal.value === 0) return 0
  const count = ratingStats.value[star] || 0
  return Math.round((count / reviewTotal.value) * 100)
}

// 计算星级统计
const calculateRatingStats = (reviews) => {
  const stats = { 5: 0, 4: 0, 3: 0, 2: 0, 1: 0 }
  reviews.forEach(review => {
    const rating = Math.round(review.rating)
    if (rating >= 1 && rating <= 5) {
      stats[rating]++
    }
  })
  return stats
}

// 获取住宿评价列表
const fetchAccommodationReviews = async () => {
  loadingReviews.value = true
  try {
    await request.get('/accommodation/review/page', {
      accommodationId: route.params.id,
      currentPage: reviewPage.value,
      size: reviewPageSize.value
    }, {
      onSuccess: (res) => {
        reviewList.value = res.records || []
        reviewTotal.value = res.total || 0

        // 计算星级统计（需要获取所有评价）
        if (reviewTotal.value > 0) {
          fetchAllReviewsForStats()
        }
      }
    })
  } catch (error) {
    console.error('获取住宿评价失败:', error)
    reviewList.value = []
    reviewTotal.value = 0
  } finally {
    loadingReviews.value = false
  }
}

// 获取所有评价用于统计
const fetchAllReviewsForStats = async () => {
  try {
    await request.get('/accommodation/review/page', {
      accommodationId: route.params.id,
      currentPage: 1,
      size: 1000 // 获取所有评价
    }, {
      onSuccess: (res) => {
        ratingStats.value = calculateRatingStats(res.records || [])
      }
    })
  } catch (error) {
    console.error('获取评价统计失败:', error)
  }
}

// 获取周边景点
const fetchNearbyScenics = async () => {
  if (!accommodation.value?.scenicId) return

  loadingScenics.value = true
  try {
    await request.get('/scenic/page', {
      size: 5,
      currentPage: 1
    }, {
      onSuccess: (res) => {
        // 过滤掉当前关联的景点，只显示其他景点
        nearbyScenics.value = res.records.filter(s => s.id !== accommodation.value?.scenicId).slice(0, 3)
      }
    })
  } catch (error) {
    console.error('获取周边景点失败:', error)
  } finally {
    loadingScenics.value = false
  }
}

// 获取相似住宿
const fetchSimilarAccommodations = async () => {
  if (!accommodation.value) return

  loadingSimilar.value = true
  try {
    await request.get('/accommodation/page', {
      type: accommodation.value.type,
      currentPage: 1,
      size: 3
    }, {
      onSuccess: (res) => {
        // 过滤掉当前住宿，只显示其他住宿
        similarAccommodations.value = res.records.filter(a => a.id !== parseInt(route.params.id)).slice(0, 3)
      }
    })
  } catch (error) {
    console.error('获取相似住宿失败:', error)
  } finally {
    loadingSimilar.value = false
  }
}

// 获取图片URL
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return baseAPI + url
}

// 获取头像URL
const getAvatarUrl = (avatar) => {
  if (!avatar) return 'https://i.pravatar.cc/150?img=3' // 默认头像
  if (avatar.startsWith('http')) return avatar
  return baseAPI + avatar
}

// 处理评价分页
const handleReviewPageChange = (page) => {
  reviewPage.value = page
  fetchAccommodationReviews()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 提交评价
const submitReview = async () => {
  // 检查是否登录
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录再发表评价')
    router.push('/login')
    return
  }

  submittingReview.value = true
  try {
    await request.post('/accommodation/review', reviewForm, {
      successMsg: '评价提交成功',
      onSuccess: () => {
        showReviewDialog.value = false
        reviewForm.rating = 5
        reviewForm.content = ''
        // 刷新评价列表
        reviewPage.value = 1
        fetchAccommodationReviews()
        // 刷新住宿详情以更新评分
        fetchAccommodationDetail()
      }
    })
  } catch (error) {
    console.error('提交评价失败:', error)
  } finally {
    submittingReview.value = false
  }
}

// 判断是否可以删除评价
const canDeleteReview = (review) => {
  if (!userStore.userInfo) return false
  // 只有评价的作者或管理员可以删除
  return review.userId === userStore.userInfo.id || userStore.userInfo.role === 'admin'
}

// 删除评价
const handleDeleteReview = (id) => {
  ElMessageBox.confirm('确认删除该评价?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/accommodation/review/${id}`,  {
        successMsg: '评价已删除',
        onSuccess: () => {
          // 刷新评价列表
          fetchAccommodationReviews()
          // 刷新住宿详情以更新评分
          fetchAccommodationDetail()
        }
      })
    } catch (error) {
      console.error('删除评价失败:', error)
    }
  }).catch(() => {})
}

// 跳转到景点详情
const goToScenic = (id) => {
  router.push(`/scenic/${id}`)
}

// 跳转到住宿详情
const goToAccommodation = (id) => {
  router.push(`/accommodation/${id}`)
  // 如果是同一页面不同ID的情况，需要刷新数据
  if (parseInt(route.params.id) !== id) {
    // 延迟执行以确保路由已经完成
    setTimeout(() => {
      fetchAccommodationDetail()
      fetchAccommodationReviews()
      window.scrollTo(0, 0)
    }, 100)
  }
}

// 初始加载
onMounted(() => {
  fetchAccommodationDetail()
  fetchAccommodationReviews()
})

// 监听住宿数据加载完成，加载相关推荐
watch(() => accommodation.value, (newVal) => {
  if (newVal) {
    fetchNearbyScenics()
    fetchSimilarAccommodations()
  }
}, { immediate: false })
</script>

<style lang="scss" scoped>
/* SCSS变量定义 */
$color-primary: #4A90E2;
$color-secondary: #357ABD;
$color-gray-50: #f9fafb;
$color-gray-100: #f3f4f6;
$color-gray-200: #e5e7eb;
$color-gray-300: #d1d5db;
$color-gray-600: #4b5563;
$color-gray-700: #374151;
$color-gray-800: #1f2937;
$color-gray-900: #111827;

/* ==================== 基础容器 ==================== */
.accommodation-detail-page {
  min-height: 100vh;
  background: $color-gray-50;

}

.page-container {

  margin: 0 auto;

}

/* ==================== Hero区域 ==================== */
.hero-section {
  position: relative;
  width: 100%;
  height: 60vh;
  min-height: 500px;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0,0,0,0.3) 0%, rgba(0,0,0,0.1) 50%, rgba(0,0,0,0.6) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-content {
  position: relative;
  z-index: 10;
  color: white;
  text-align: center;
  max-width: 1200px;
  width: 100%;

}

/* 面包屑导航 */
.breadcrumb-wrapper {
  margin-bottom: 24px;
  text-align: left;
}

.breadcrumb-nav {
  font-size: 14px;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  list-style: none;
  padding: 0;
  margin: 0;
  gap: 8px;
}

.breadcrumb-item {
  display: inline-block;
}

.breadcrumb-link {
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: color 0.3s ease;
  text-decoration: none;
}

.breadcrumb-link:hover {
  color: white;
}

.breadcrumb-current {
  color: white;
  font-weight: 600;
}

.breadcrumb-separator {
  color: rgba(255, 255, 255, 0.6);
}

/* Hero标题 */
.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 24px;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  line-height: 1.2;
}

/* Hero元信息 */
.hero-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
}

.meta-item i {
  font-size: 16px;
}

.star-icon {
  color: #fbbf24;
}

.review-count {
  opacity: 0.9;
  margin-left: 4px;
}

/* Hero操作按钮 */
.hero-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.btn-primary-action,
.btn-secondary-action {
  padding: 12px 24px;
  border-radius: 50px;
  font-weight: 600;
  font-size: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.btn-primary-action {
  background: rgba(255, 255, 255, 0.9);
  color: $color-primary;
}

.btn-primary-action:hover {
  background: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.btn-secondary-action {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.btn-secondary-action:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.btn-primary-action i,
.btn-secondary-action i {
  margin-right: 8px;
}

/* ==================== 加载状态 ==================== */
.loading-wrapper {
  background: white;
  padding: 60px 0;
}

/* ==================== 详细内容区域 ==================== */
.detail-section {
  max-width: 1200px;
  margin: 0 auto;
  background: $color-gray-50;
  padding: 32px 0 60px;
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 32px;
  align-items: start;
}

/* 主内容区域 */
.content-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.content-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid $color-gray-200;
  overflow: hidden;
}

/* 图片展示卡片 */
.image-gallery-card {
  padding: 0;
}

.main-image-container {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.rating-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 8px 16px;
  border-radius: 50px;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.rating-badge .fa-star {
  color: #fbbf24;
  font-size: 16px;
}

.rating-score {
  font-size: 16px;
  font-weight: 700;
  color: $color-gray-900;
}

.thumbnail-list {
  padding: 16px;
  display: flex;
  gap: 12px;
  overflow-x: auto;
  background: $color-gray-100;
}

.thumbnail-list::-webkit-scrollbar {
  height: 6px;
}

.thumbnail-list::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.thumbnail-item {
  flex-shrink: 0;
  width: 96px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.thumbnail-item:hover {
  border-color: rgba(74, 144, 226, 0.3);
}

.thumbnail-item.active {
  border-color: $color-primary;
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0;
  padding: 24px 24px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  color: $color-primary;
  font-size: 20px;
}

.section-content {
  padding: 16px 24px 24px;
}

.description-text,
.features-text {
  font-size: 15px;
  line-height: 1.8;
  color: $color-gray-700;
  margin: 0;
}

/* 评价区域样式 */
.reviews-section {
  padding-bottom: 0;
}

.reviews-header {
  padding: 24px 24px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.review-total {
  font-size: 14px;
  font-weight: 400;
  color: $color-gray-600;
}

/* 评分概览 */
.rating-overview {
  background: linear-gradient(135deg, #e6f0fa 0%, #f0f7ff 100%);
  border-radius: 12px;
  padding: 24px;
  margin: 0 24px 24px;
}

.rating-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 48px;
}

.rating-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.rating-number {
  font-size: 48px;
  font-weight: 700;
  color: $color-primary;
  line-height: 1;
  margin-bottom: 12px;
}

.rating-stars-large {
  display: flex;
  gap: 4px;
  margin-bottom: 12px;
}

.rating-stars-large .fa-star {
  font-size: 24px;
  color: #fbbf24;
}

.rating-text {
  font-size: 14px;
  color: $color-gray-600;
}

/* 星级分布 */
.rating-distribution {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.star-label {
  width: 48px;
  font-size: 13px;
  color: $color-gray-600;
  text-align: left;
}

.bar-container {
  flex: 1;
  height: 8px;
  background: $color-gray-200;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: #fbbf24;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.star-percentage {
  width: 48px;
  font-size: 13px;
  color: $color-gray-600;
  text-align: right;
}

.reviews-loading {
  padding: 24px;
}

.reviews-empty {
  text-align: center;
  padding: 60px 24px;
}

.reviews-empty .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.reviews-empty .empty-title {
  font-size: 18px;
  font-weight: 600;
  color: $color-gray-900;
  margin: 0 0 8px;
}

.reviews-empty .empty-desc {
  font-size: 14px;
  color: $color-gray-600;
  margin: 0;
}

/* 评价列表 */
.reviews-list {
  padding: 0 24px 24px;
}

.review-item {
  padding: 24px 0;
  border-bottom: 1px solid $color-gray-200;
}

.review-item:last-child {
  border-bottom: none;
}

.review-container {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.reviewer-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.review-content {
  flex: 1;
  min-width: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reviewer-name {
  font-size: 15px;
  font-weight: 600;
  color: $color-gray-900;
}

.review-time {
  font-size: 13px;
  color: #9ca3af;
}

.review-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars-display {
  display: flex;
  gap: 2px;
}

.stars-display .fa-star {
  font-size: 14px;
}

.star-filled {
  color: #fbbf24;
}

.star-empty {
  color: $color-gray-200;
}

.rating-score {
  font-size: 13px;
  color: $color-gray-600;
  font-weight: 500;
}

.review-text {
  font-size: 14px;
  line-height: 1.7;
  color: $color-gray-700;
  margin: 0 0 12px;
}

.review-actions {
  text-align: right;
}

.btn-delete-review {
  padding: 0;
  background: transparent;
  border: none;
  color: #ef4444;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.2s ease;
}

.btn-delete-review:hover {
  color: #dc2626;
}

.btn-delete-review i {
  margin-right: 4px;
}

.reviews-pagination {
  padding: 24px;
  border-top: 1px solid $color-gray-100;
}

.pagination-nav {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.pagination-btn {
  min-width: 36px;
  height: 36px;
  padding: 0 12px;
  background: white;
  border: 1px solid $color-gray-200;
  border-radius: 8px;
  color: $color-gray-600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: $color-primary;
  border-color: $color-primary;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 144, 226, 0.2);
}

.pagination-btn.active {
  background: $color-primary;
  border-color: $color-primary;
  color: white;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 侧边栏样式 */
.content-sidebar {
  display: flex;
  flex-direction: column;
}

.sidebar-card-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid $color-gray-200;
  overflow: hidden;
}

.sidebar-section {
  padding: 0;
}

.sidebar-section:not(:last-child) {
  border-bottom: 1px solid $color-gray-100;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0;
  padding: 20px 20px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.sidebar-title .title-icon {
  color: $color-primary;
  font-size: 18px;
}

.info-items {
  padding: 16px 20px 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid $color-gray-100;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: $color-gray-600;
  font-weight: 500;
}

.info-label i {
  color: $color-primary;
  font-size: 16px;
}

.info-value {
  font-size: 14px;
  color: $color-gray-900;
  font-weight: 600;
  text-align: right;
}

.price-highlight {
  color: #ef4444;
  font-size: 16px;
}

.scenic-clickable {
  color: $color-primary;
  cursor: pointer;
  transition: color 0.3s ease;
}

.scenic-clickable:hover {
  color: $color-secondary;
  text-decoration: underline;
}

.distance-badge {
  color: #10b981;
  font-weight: 500;
  margin-left: 4px;
}

.sidebar-loading {
  padding: 20px;
}

.recommendation-list {
  padding: 16px 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommendation-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;

  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  outline: none;
}

.recommendation-item:hover {
  background-color: $color-gray-100;
}

.recommendation-item:focus {
  outline: none;
}

.recommendation-image {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

.recommendation-info {
  flex: 1;
  min-width: 0;
}

.recommendation-name {
  font-size: 14px;
  font-weight: 600;
  color: $color-gray-900;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommendation-stars {
  display: flex;
  gap: 2px;
  margin: 4px 0;
}

.recommendation-stars .fa-star {
  font-size: 12px;
}

.recommendation-price {
  font-size: 14px;
  color: $color-primary;
  font-weight: 600;
  margin-top: 4px;
}

/* 空状态 */
.not-found-section {
  background: white;
  padding: 80px 0;
}

.not-found-content {
  text-align: center;
  max-width: 400px;
  margin: 0 auto;
}

.not-found-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.not-found-title {
  font-size: 24px;
  font-weight: 700;
  color: $color-gray-900;
  margin: 0 0 12px;
}

.not-found-desc {
  font-size: 16px;
  color: $color-gray-600;
  margin: 0 0 24px;
}

.btn-back {
  padding: 12px 32px;
  background: $color-primary;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: $color-secondary;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.3);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .detail-layout {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .content-sidebar {
    order: -1;
  }
}

@media (max-width: 768px) {
  .hero-section {
    height: 50vh;
    min-height: 400px;
  }

  .hero-content {
    padding: 0 20px;
  }

  .hero-title {
    font-size: 32px;
  }

  .hero-meta {
    flex-direction: column;
    gap: 12px;
  }

  .hero-actions {
    flex-direction: column;
    width: 100%;
  }

  .btn-primary-action,
  .btn-secondary-action {
    width: 100%;
    max-width: 280px;
  }

  .page-container {
    padding: 0 16px;
  }

  .detail-section {
    padding: 24px 0 40px;
  }

  .detail-layout {
    gap: 20px;
  }

  .section-title {
    font-size: 18px;
    padding: 20px 20px 0;
  }

  .description-text,
  .features-text {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 24px;
  }

  .meta-item {
    font-size: 14px;
  }

  .btn-primary-action,
  .btn-secondary-action {
    padding: 10px 20px;
    font-size: 14px;
  }

  .section-title {
    font-size: 16px;
  }

  .sidebar-title {
    font-size: 16px;
  }

  .recommendation-image {
    width: 56px;
    height: 56px;
  }
}
</style>