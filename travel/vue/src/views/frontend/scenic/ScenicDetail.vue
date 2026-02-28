<template>
  <div class="scenic-detail-page">
    <!-- 主容器 -->
    <div class="detail-main-container">
      <!-- 面包屑导航 -->
      <nav class="breadcrumb-nav">
        <ol class="breadcrumb-list">
          <li class="breadcrumb-item">
            <a @click="$router.push('/')" class="breadcrumb-link">首页</a>
          </li>
          <li class="breadcrumb-separator"><i class="fas fa-chevron-right"></i></li>
          <li class="breadcrumb-item">
            <a @click="$router.push('/scenic')" class="breadcrumb-link">景点列表</a>
          </li>
          <li class="breadcrumb-separator"><i class="fas fa-chevron-right"></i></li>
          <li class="breadcrumb-item breadcrumb-current">{{ scenic.name }}</li>
        </ol>
      </nav>

      <div class="detail-grid">
        <!-- 左侧主内容区 -->
        <div class="detail-left-content">
          <!-- 图片展示卡片 -->
          <div class="detail-card image-card">
            <!-- 主图区域 -->
            <div class="main-image-area" v-if="imageList.length > 0">
              <img :src="getImageUrl(currentImage.url)" :alt="scenic.name" class="main-image" />
              <div class="image-badges">
                <span v-if="scenic.price === 0" class="badge-free">免费</span>
                <span v-if="scenic.rating" class="badge-rating">
                  <i class="fas fa-star"></i> {{ getDisplayRating(scenic.rating) }}
                </span>
              </div>
              <div class="image-actions">
                <button
                    class="action-btn action-btn-favorite"
                    :class="{ 'is-collected': isCollected }"
                    :disabled="collectionLoading"
                    @click="handleCollection"
                >
                  <i :class="isCollected ? 'fas fa-heart' : 'far fa-heart'"></i>
                </button>
              </div>
            </div>

            <!-- 缩略图列表 -->
            <div class="thumbnail-area" v-if="imageList.length > 1">
              <img
                  v-for="(image, index) in imageList"
                  :key="index"
                  :src="getImageUrl(image.url)"
                  :alt="`图片${index + 1}`"
                  class="thumbnail-img"
                  :class="{ 'thumbnail-active': index === currentImageIndex }"
                  @click="currentImageIndex = index"
              />
            </div>
          </div>

          <!-- 景点信息卡片 -->
          <div class="detail-card info-card">
            <h1 class="scenic-name">{{ scenic.name }}</h1>
            <div class="scenic-tags">
              <span class="tag-item tag-location">
                <i class="fas fa-map-marker-alt"></i>
                {{ scenic.location }}
              </span>
              <span class="tag-item tag-category" v-if="scenic.categoryInfo">
                <i class="fas fa-tag"></i>
                {{ scenic.categoryInfo.name }}
              </span>
              <span class="tag-item tag-rating" v-if="scenic.rating">
                <i class="fas fa-star"></i>
                {{ getDisplayRating(scenic.rating) }} 评分
              </span>
              <span class="tag-item tag-reviews" v-if="scenic.reviewCount">
                <i class="fas fa-comment"></i>
                {{ scenic.reviewCount }} 条评价
              </span>
            </div>

            <!-- 景点介绍 -->
            <div class="scenic-intro">
              <h3 class="intro-title">景点简介</h3>
              <p class="intro-text">{{ scenic.description }}</p>
            </div>
          </div>

          <!-- 天气信息卡片 -->
          <div class="detail-card weather-card" v-if="weather.now || weatherLoading || weatherForecast.length > 0">
            <h3 class="card-header">
              <i class="fas fa-cloud-sun"></i>天气信息
            </h3>

            <!-- 当前天气 -->
            <div v-if="weather.now" class="weather-current">
              <div class="weather-main-info">
                <div class="weather-temp-big">{{ weather.now.temp }}°C</div>
                <div class="weather-desc">{{ weather.now.text }}</div>
              </div>
              <div class="weather-extra-info">
                <div class="weather-info-item">
                  <span class="info-label">体感温度</span>
                  <span class="info-value">{{ weather.now.feelsLike }}°C</span>
                </div>
                <div class="weather-info-item">
                  <span class="info-label">湿度</span>
                  <span class="info-value">{{ weather.now.humidity }}%</span>
                </div>
                <div class="weather-info-item">
                  <span class="info-label">风向</span>
                  <span class="info-value">{{ weather.now.windDir }}</span>
                </div>
                <div class="weather-info-item">
                  <span class="info-label">风速</span>
                  <span class="info-value">{{ weather.now.windSpeed }} km/h</span>
                </div>
              </div>
              <div class="weather-update-time">
                更新时间: {{ formatWeatherTime(weather.updateTime) }}
              </div>
            </div>

            <!-- 未来天气 -->
            <div v-if="weatherForecast.length > 0" class="weather-forecast">
              <h4 class="forecast-header">未来天气</h4>
              <div class="forecast-items">
                <div
                    v-for="(day, index) in weatherForecast.slice(0, 4)"
                    :key="index"
                    class="forecast-item-card"
                >
                  <div class="forecast-day">{{ formatForecastDate(day.fxDate) }}</div>
                  <div class="forecast-weather">{{ day.textDay }}</div>
                  <div class="forecast-temps">
                    <span class="temp-max">{{ day.tempMax }}°</span>
                    <span class="temp-min">{{ day.tempMin }}°</span>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="weatherLoading" class="weather-loading-state">
              <el-icon class="loading-spinner"><Loading /></el-icon>
              <span>获取天气信息中...</span>
            </div>
          </div>

          <!-- 用户评价卡片 -->
          <div class="detail-card comments-card">
            <h3 class="card-header card-header-with-count">
              <span><i class="fas fa-comments"></i>用户评价</span>
              <span class="comment-count">共 {{ scenic.reviewCount || 328 }} 条评论</span>
            </h3>

            <!-- 评分统计 -->
            <div class="rating-summary">
              <div class="rating-score-area">
                <div class="score-big">{{ getDisplayRating(scenic.rating) }}</div>
                <div class="rating-stars">
                  <i class="fas fa-star"></i>
                  <i class="fas fa-star"></i>
                  <i class="fas fa-star"></i>
                  <i class="fas fa-star"></i>
                  <i class="fas fa-star-half-alt"></i>
                </div>
                <div class="rating-based">基于 {{ scenic.reviewCount || 328 }} 条评价</div>
              </div>
              <div class="rating-bars">
                <div class="rating-bar-row">
                  <span class="bar-label">5星</span>
                  <div class="bar-track">
                    <div class="bar-fill" style="width: 75%"></div>
                  </div>
                  <span class="bar-percent">75%</span>
                </div>
                <div class="rating-bar-row">
                  <span class="bar-label">4星</span>
                  <div class="bar-track">
                    <div class="bar-fill" style="width: 18%"></div>
                  </div>
                  <span class="bar-percent">18%</span>
                </div>
                <div class="rating-bar-row">
                  <span class="bar-label">3星</span>
                  <div class="bar-track">
                    <div class="bar-fill" style="width: 5%"></div>
                  </div>
                  <span class="bar-percent">5%</span>
                </div>
              </div>
            </div>

            <!-- 评论列表组件 -->
            <div class="comments-list-wrapper">
              <CommentList />
            </div>
          </div>
        </div>

        <!-- 右侧预订卡片 -->
        <div class="detail-right-sidebar">
          <!-- 基本信息卡片 -->
          <div class="booking-card">
            <div class="booking-price-section">
              <div v-if="scenic.price === 0" class="price-free-display">
                <div class="price-amount">免费</div>
                <div class="price-note">无需门票</div>
              </div>
              <div v-else class="price-paid-display">
                <div class="price-amount">¥{{ scenic.price }}</div>
                <div class="price-note">门票价格</div>
              </div>
            </div>

            <div class="booking-info-list">
              <div class="booking-info-row" v-if="scenic.openingHours">
                <span class="info-key">开放时间</span>
                <span class="info-val">{{ scenic.openingHours }}</span>
              </div>
              <div class="booking-info-row" v-if="scenic.location">
                <span class="info-key">所在地区</span>
                <span class="info-val">{{ scenic.location }}</span>
              </div>
              <div class="booking-info-row" v-if="scenic.categoryInfo">
                <span class="info-key">景点类型</span>
                <span class="info-val">{{ scenic.categoryInfo.name }}</span>
              </div>
            </div>

            <div class="booking-actions">
              <button
                  class="btn-secondary btn-collect"
                  :class="{ 'is-collected': isCollected }"
                  :disabled="collectionLoading"
                  @click="handleCollection"
              >
                <i :class="isCollected ? 'fas fa-heart' : 'far fa-heart'"></i>
                {{ isCollected ? '已收藏' : '收藏景点' }}
              </button>

            </div>
          </div>

          <!-- 门票预订卡片 -->
          <div class="booking-card ticket-booking-card" v-if="tickets.length > 0 || ticketLoading">
            <h3 class="sidebar-card-title">
              <i class="fas fa-ticket-alt"></i>门票预订
            </h3>
            <div v-loading="ticketLoading" class="sidebar-tickets-content">
              <div v-if="tickets.length === 0" class="sidebar-empty-state">
                <div class="empty-icon-small">🎫</div>
                <p class="empty-text-small">暂无可预订门票</p>
              </div>
              <div v-else class="sidebar-tickets-list">
                <div
                    v-for="ticket in tickets"
                    :key="ticket.id"
                    class="sidebar-ticket-item"
                >
                  <div class="sidebar-ticket-header">
                    <h4 class="sidebar-ticket-name">{{ ticket.ticketName }}</h4>
                    <div class="sidebar-ticket-badge">{{ ticket.ticketType }}</div>
                  </div>

                  <div class="sidebar-ticket-price">
                    <template v-if="ticket.discountPrice">
                      <div class="sidebar-price-old">¥{{ ticket.price }}</div>
                      <div class="sidebar-price-new">¥{{ ticket.discountPrice }}</div>
                    </template>
                    <template v-else>
                      <div class="sidebar-price-current">¥{{ ticket.price }}</div>
                    </template>
                  </div>

                  <div class="sidebar-ticket-meta">
                    <div class="sidebar-meta-item">
                      <i class="fas fa-clock"></i>
                      <span>{{ ticket.validPeriod }}</span>
                    </div>
                    <div class="sidebar-meta-item">
                      <i class="fas fa-ticket-alt"></i>
                      <span>剩余 {{ ticket.stock }} 张</span>
                    </div>
                  </div>

                  <button
                      class="btn-primary btn-book-now"
                      @click="goToBooking(ticket.id)"
                      :disabled="ticket.stock === 0"
                  >
                    <i class="fas fa-ticket-alt"></i>
                    {{ ticket.stock === 0 ? '已售罄' : '立即预订' }}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>



  </div>
</template>

<script setup>

import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'
import CommentList from '@/views/frontend/CommentList.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import axios from 'axios'
import {
  Location, CollectionTag, Money, Timer, Sunny, Loading, Star, StarFilled,
  Document, InfoFilled, CopyDocument, Share, Ticket, Tickets, ChatDotRound
} from '@element-plus/icons-vue'

const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const scenic = ref({})
const tickets = ref([])
const ticketLoading = ref(false)
// 收藏相关状态
const isCollected = ref(false)
const collectionLoading = ref(false)

// 图片列表相关状态
const imageList = ref([])
const currentImageIndex = ref(0)



// 检查用户是否登录
const isLoggedIn = computed(() => userStore.isLoggedIn)

// 当前选中的图片
const currentImage = computed(() => {
  if (imageList.value.length === 0) return { url: '', isMain: false }
  return imageList.value[currentImageIndex.value] || imageList.value[0]
})

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return '/default-scenic.jpg'
  return url.startsWith('http') ? url : baseAPI + url
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



// 天气相关状态
const weather = ref({})
const weatherForecast = ref([])
const weatherLoading = ref(false)



// 格式化天气时间
const formatWeatherTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 格式化预报日期
const formatForecastDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const today = new Date()
  const tomorrow = new Date()
  tomorrow.setDate(today.getDate() + 1)

  if (date.toDateString() === today.toDateString()) {
    return '今天'
  } else if (date.toDateString() === tomorrow.toDateString()) {
    return '明天'
  } else {
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    return weekdays[date.getDay()]
  }
}

// 获取天气信息
const fetchWeatherInfo = async (location) => {
  if (!location) return

  weatherLoading.value = true
  try {
    // 调用和风天气API
    const key = '308d7421f676413aab8b4064aba532d7'

    // 先通过地名查询获取经纬度
    const locationResponse = await axios.get(`https://geoapi.qweather.com/v2/city/lookup`, {
      params: {
        location,
        key,
        number: 1
      }
    })

    if (locationResponse.data.code === '200' && locationResponse.data.location && locationResponse.data.location.length > 0) {
      const locationId = locationResponse.data.location[0].id
      const lon = locationResponse.data.location[0].lon
      const lat = locationResponse.data.location[0].lat

      // 获取实时天气
      const weatherResponse = await axios.get(`https://devapi.qweather.com/v7/weather/now`, {
        params: {
          key,
          location: `${lon},${lat}`
        }
      })

      if (weatherResponse.data.code === '200') {
        weather.value = weatherResponse.data
      }

      // 获取天气预报
      const forecastResponse = await axios.get(`https://devapi.qweather.com/v7/weather/3d`, {
        params: {
          key,
          location: `${lon},${lat}`
        }
      })

      if (forecastResponse.data.code === '200') {
        weatherForecast.value = forecastResponse.data.daily
      }
    }
  } catch (error) {
    console.error('获取天气信息失败:', error)
  } finally {
    weatherLoading.value = false
  }
}

const fetchDetail = async () => {
  const id = route.params.id
  await request.get(`/scenic/${id}`,null, {
    onSuccess: (res) => {
      scenic.value = res

      // 解析图片列表
      imageList.value = parseImageList(res.imageList)
      // 如果没有图片列表但有单张图片，创建一个包含单张图片的列表
      if (imageList.value.length === 0 && res.imageUrl) {
        imageList.value = [{ url: res.imageUrl, isMain: true }]
      }

      fetchTickets(id)
      // 获取评论统计
      fetchCommentStats(id)
      // 加载天气信息
      fetchWeatherInfo(res.location)



      // 如果用户已登录，检查收藏状态
      if (isLoggedIn.value) {
        checkCollectionStatus(id)
      }
    }
  })
}

// 获取评论统计信息
const fetchCommentStats = async (scenicId) => {
  try {
    await request.get('/comment/page', {
      scenicId: scenicId,
      currentPage: 1,
      size: 1  // 只需要获取总数，不需要具体数据
    }, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        // 更新景点的评论统计信息
        if (res) {
          scenic.value.reviewCount = res.total || 0

          // 如果有评论数据，计算平均评分
          if (res.records && res.records.length > 0) {
            // 获取所有评论来计算平均评分
            fetchAllCommentsForRating(scenicId)
          }
        }
      }
    })
  } catch (error) {
    console.error('获取评论统计失败:', error)
    // 如果获取失败，设置默认值
    scenic.value.reviewCount = 0
  }
}

// 获取所有评论来计算平均评分
const fetchAllCommentsForRating = async (scenicId) => {
  try {
    await request.get(`/comment/scenic/${scenicId}`, null, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        if (res && res.length > 0) {
          // 计算平均评分
          const totalRating = res.reduce((sum, comment) => sum + (comment.rating || 0), 0)
          const averageRating = totalRating / res.length
          scenic.value.rating = averageRating.toFixed(1)
        }
      }
    })
  } catch (error) {
    console.error('获取评论评分失败:', error)
  }
}

// 检查收藏状态
const checkCollectionStatus = async (scenicId) => {
  if (!isLoggedIn.value) return

  try {
    await request.get(`/scenic-collection/is-collected/${scenicId}`, null, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        isCollected.value = res
      }
    })
  } catch (error) {
    console.error('获取收藏状态失败:', error)
  }
}

// 收藏/取消收藏操作
const handleCollection = async () => {
  // 检查登录状态
  if (!isLoggedIn.value) {
    ElMessageBox.confirm('收藏功能需要登录，是否前往登录页面？', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'info'
    }).then(() => {
      router.push({
        path: '/login',
        query: { redirect: route.fullPath }
      })
    }).catch(() => {})
    return
  }

  const scenicId = scenic.value.id
  if (!scenicId) return

  collectionLoading.value = true
  try {
    if (isCollected.value) {
      // 取消收藏 - 修复delete请求逻辑
      try {
        await request.delete(`/scenic-collection/${scenicId}`, {
          successMsg: '取消收藏成功'
        })
        // 手动更新状态
        isCollected.value = false
      } catch (error) {
        console.error('取消收藏失败:', error)
        ElMessage.error('取消收藏失败，请稍后重试')
      }
    } else {
      // 添加收藏
      try {
        await request.post(`/scenic-collection/${scenicId}`, null, {
          successMsg: '收藏成功'
        })
        // 手动更新状态
        isCollected.value = true
      } catch (error) {
        console.error('添加收藏失败:', error)
        ElMessage.error('添加收藏失败，请稍后重试')
      }
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    collectionLoading.value = false
  }
}

const fetchTickets = async (scenicId) => {
  ticketLoading.value = true
  try {
    await request.get(`/ticket/scenic/${scenicId}`, {
      currentPage: 1,
      size: 10
    }, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        tickets.value = res.records || []
      }
    })
  } catch (error) {
    console.error('获取景点门票失败:', error)
  } finally {
    ticketLoading.value = false
  }
}

const goToBooking = (ticketId) => {
  router.push(`/ticket/booking/${ticketId}`)
}





onMounted(fetchDetail)
</script>

<style lang="scss" scoped>
/* 页面主容器 */
.scenic-detail-page {
  min-height: 100vh;
  background-color: #f9fafb;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

/* 主容器 */
.detail-main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 24px;
}

/* 面包屑导航 */
.breadcrumb-nav {
  margin-bottom: 24px;
}

.breadcrumb-list {
  display: flex;
  align-items: center;
  list-style: none;
  padding: 0;
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.breadcrumb-item {
  display: inline-flex;
  align-items: center;
}

.breadcrumb-link {
  color: #6b7280;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.2s;

  &:hover {
    color: #2563eb;
  }
}

.breadcrumb-separator {
  margin: 0 8px;
  font-size: 10px;
  color: #9ca3af;
}

.breadcrumb-current {
  color: #111827;
  font-weight: 600;
}

/* 详情网格布局 */
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 420px;
  gap: 32px;
  align-items: start;
}

.detail-left-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-right-sidebar {
  position: sticky;
  top: 80px;
  align-self: flex-start;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 卡片通用样式 */
.detail-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

/* 图片卡片 */
.image-card {
  padding: 0;
}

.main-image-area {
  position: relative;
  width: 100%;
  height: 480px;
  overflow: hidden;
}

.main-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-badges {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.badge-free {
  padding: 8px 16px;
  background-color: #10b981;
  color: white;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.badge-rating {
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  color: #eab308;
}

.image-actions {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 48px;
  height: 48px;
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-radius: 50%;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #4b5563;
  transition: all 0.3s;

  &:hover {
    background-color: white;
  }

  &.is-collected {
    color: #ef4444;
  }
}

.thumbnail-area {
  padding: 16px;
  display: flex;
  gap: 8px;
  overflow-x: auto;
  background-color: #f9fafb;

  &::-webkit-scrollbar {
    height: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #d1d5db;
    border-radius: 2px;
  }
}

.thumbnail-img {
  width: 96px;
  height: 64px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s;

  &:hover {
    border-color: #2563eb;
  }

  &.thumbnail-active {
    border-color: #2563eb;
  }
}

/* 信息卡片 */
.info-card {
  padding: 32px;
}

.scenic-name {
  font-size: 32px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;
}

.scenic-tags {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 24px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #6b7280;

  i {
    font-size: 14px;
  }

  &.tag-location i {
    color: #2563eb;
  }

  &.tag-category i {
    color: #10b981;
  }

  &.tag-rating i {
    color: #eab308;
  }

  &.tag-reviews i {
    color: #8b5cf6;
  }
}

.scenic-intro {
  margin-top: 24px;
}

.intro-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 12px;
}

.intro-text {
  font-size: 16px;
  line-height: 1.75;
  color: #4b5563;
  margin: 0;
}

/* 卡片标题通用样式 */
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 16px;

  i {
    color: #2563eb;
  }
}

/* 天气卡片 */
.weather-card {
  padding: 24px;
}

.weather-current {
  margin-bottom: 24px;
}

.weather-main-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.weather-temp-big {
  font-size: 48px;
  font-weight: 700;
  color: #2563eb;
}

.weather-desc {
  font-size: 16px;
  color: #6b7280;
}

.weather-extra-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.weather-info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 8px;

  .info-label {
    font-size: 14px;
    color: #6b7280;
  }

  .info-value {
    font-size: 14px;
    font-weight: 600;
    color: #111827;
  }
}

.weather-update-time {
  font-size: 12px;
  color: #9ca3af;
  text-align: center;
  margin-top: 16px;
}

.weather-forecast {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.forecast-header {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 16px;
}

.forecast-items {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.forecast-item-card {
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s;

  &:hover {
    background-color: #f3f4f6;
    transform: translateY(-2px);
  }
}

.forecast-day {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  margin-bottom: 8px;
}

.forecast-weather {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 8px;
}

.forecast-temps {
  display: flex;
  justify-content: center;
  gap: 4px;
  font-size: 12px;

  .temp-max {
    font-weight: 600;
    color: #dc2626;
  }

  .temp-min {
    font-weight: 600;
    color: #2563eb;
  }
}

.weather-loading-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #6b7280;
  padding: 20px;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}



/* 评论卡片 */
.comments-card {
  padding: 24px;
}

.card-header-with-count {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-count {
  font-size: 14px;
  font-weight: 400;
  color: #6b7280;
}

.rating-summary {
  background: linear-gradient(135deg, #dbeafe 0%, #e0e7ff 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rating-score-area {
  text-align: center;
}

.score-big {
  font-size: 48px;
  font-weight: 700;
  color: #2563eb;
  margin-bottom: 8px;
}

.rating-stars {
  color: #eab308;
  font-size: 20px;
  margin-bottom: 8px;
}

.rating-based {
  font-size: 14px;
  color: #6b7280;
}

.rating-bars {
  flex: 1;
  margin-left: 48px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-bar-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 48px;
  font-size: 14px;
  color: #6b7280;
}

.bar-track {
  flex: 1;
  height: 8px;
  background-color: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background-color: #eab308;
  border-radius: 4px;
  transition: width 0.3s;
}

.bar-percent {
  width: 48px;
  text-align: right;
  font-size: 14px;
  color: #6b7280;
}

.comments-list-wrapper {
  margin-top: 24px;
}

/* 右侧预订卡片 */
.booking-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.booking-price-section {
  text-align: center;
  padding-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;
  margin-bottom: 24px;
}

.price-free-display .price-amount {
  font-size: 36px;
  font-weight: 700;
  color: #2563eb;
  margin-bottom: 8px;
}

.price-paid-display .price-amount {
  font-size: 36px;
  font-weight: 700;
  color: #2563eb;
  margin-bottom: 8px;
}

.price-note {
  font-size: 14px;
  color: #6b7280;
}

.booking-info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.booking-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e5e7eb;

  &:last-child {
    border-bottom: none;
  }
}

.info-key {
  font-size: 14px;
  color: #6b7280;
}

.info-val {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
}

.booking-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-primary,
.btn-secondary {
  width: 100%;
  padding: 16px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.btn-primary {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;

  &:hover:not(:disabled) {
    box-shadow: 0 8px 20px rgba(37, 99, 235, 0.3);
    transform: translateY(-2px);
  }
}

.btn-secondary {
  background: white;
  color: #2563eb;
  border: 2px solid #2563eb;

  &:hover:not(:disabled) {
    background-color: #eff6ff;
  }

  &.is-collected {
    background-color: #fef2f2;
    color: #ef4444;
    border-color: #ef4444;
  }
}



/* 右侧门票预订卡片 */
.ticket-booking-card {
  margin-top: 0;
}

.sidebar-card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 20px;
  padding: 0 24px;
  padding-top: 24px;

  i {
    color: #2563eb;
  }
}

.sidebar-tickets-content {
  padding: 0 24px 24px;
}

.sidebar-empty-state {
  text-align: center;
  padding: 40px 20px;
}

.empty-icon-small {
  font-size: 48px;
  margin-bottom: 12px;
}

.empty-text-small {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.sidebar-tickets-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-ticket-item {
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s;

  &:hover {
    background-color: #f3f4f6;
    border-color: #d1d5db;
  }
}

.sidebar-ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 8px;
}

.sidebar-ticket-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0;
  flex: 1;
  line-height: 1.4;
}

.sidebar-ticket-badge {
  background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-ticket-price {
  margin-bottom: 12px;
}

.sidebar-price-old {
  font-size: 12px;
  color: #9ca3af;
  text-decoration: line-through;
  margin-bottom: 2px;
}

.sidebar-price-new {
  font-size: 24px;
  font-weight: 700;
  color: #dc2626;
}

.sidebar-price-current {
  font-size: 24px;
  font-weight: 700;
  color: #2563eb;
}

.sidebar-ticket-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.sidebar-meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;

  i {
    color: #2563eb;
    font-size: 12px;
  }
}

.btn-book-now {
  width: 100%;
  padding: 12px;
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;

  i {
    font-size: 14px;
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .detail-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .detail-right-sidebar {
    position: static;
  }

  .rating-summary {
    flex-direction: column;
    text-align: center;
  }

  .rating-bars {
    margin-left: 0;
    margin-top: 24px;
    width: 100%;
  }
}

@media (max-width: 768px) {
  .detail-main-container {
    padding: 20px 16px;
  }

  .scenic-name {
    font-size: 24px;
  }

  .scenic-tags {
    gap: 12px;
  }

  .main-image-area {
    height: 300px;
  }

  .forecast-items {
    grid-template-columns: repeat(2, 1fr);
  }

  .weather-extra-info {
    grid-template-columns: 1fr;
  }

  .sidebar-tickets-list {
    gap: 12px;
  }

  .sidebar-ticket-item {
    padding: 12px;
  }



  :deep(.el-dialog) {
    width: 95% !important;
  }
}

@media (max-width: 480px) {
  .scenic-name {
    font-size: 20px;
  }

  .main-image-area {
    height: 240px;
  }

  .breadcrumb-list {
    font-size: 12px;
  }

  .forecast-items {
    grid-template-columns: 1fr;
  }

  .image-badges {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-btn {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
}



/* 滚动条美化 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f3f4f6;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;

  &:hover {
    background: #9ca3af;
  }
}

/* 选择文本样式 */
::selection {
  background: rgba(37, 99, 235, 0.2);
  color: #111827;
}

::-moz-selection {
  background: rgba(37, 99, 235, 0.2);
  color: #111827;
}
</style>