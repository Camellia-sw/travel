<template>
  <div class="home-container">
    <!-- Hero静态背景区 -->
    <div class="hero-section">
      <div class="hero-background">
        <img src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=500&fit=crop"
             alt="Hero Background" class="hero-image">
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">发现世界之美</h1>
        <p class="hero-subtitle">探索精彩旅程，创造美好回忆</p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar-wrapper">
      <div class="search-bar-container">
        <smart-search
            placeholder="搜索目的地、景点、攻略..."
            @search="handleSearch"
        />
      </div>
    </div>

    <!-- 协同过滤推荐 -->
    <section class="cf-recommendation-wrapper">
      <div class="content-container">
        <CollaborativeFilteringRecommendation />
      </div>
    </section>

    <!-- 精选景点 -->
    <section class="featured-scenic-wrapper">
      <div class="content-container">
        <div class="section-header-row">
          <h2 class="section-main-title">
            <i class="fas fa-star"></i>
            精选景点
          </h2>
          <router-link to="/scenic" class="view-all-link">
            查看全部 <i class="fas fa-arrow-right"></i>
          </router-link>
        </div>

        <el-skeleton :loading="scenicLoading" animated :count="3" :throttle="500">
          <template #template>
            <div class="scenic-cards-grid">
              <div v-for="i in 3" :key="i" class="skeleton-card">
                <el-skeleton-item variant="image" style="width: 100%; height: 300px" />
                <div style="padding: 16px;">
                  <el-skeleton-item variant="h3" style="width: 80%; margin-bottom: 8px" />
                  <el-skeleton-item variant="text" style="width: 60%; margin-bottom: 8px" />
                  <el-skeleton-item variant="text" style="width: 40%" />
                </div>
              </div>
            </div>
          </template>
          <template #default>
            <div class="scenic-cards-grid">
              <div v-for="item in scenicList.slice(0, 3)" :key="item.id"
                   class="scenic-item-card"
                   @click="goToScenicDetail(item.id)">
                <div class="scenic-card-image">
                  <img :src="getImageUrl(item.imageUrl)" :alt="item.name" />
                  <div class="scenic-price-badge" v-if="item.price === 0">免费</div>
                  <div class="scenic-price-badge price-tag" v-else-if="item.price > 0">¥{{ item.price }}</div>
                  <div class="scenic-rating-badge">
                    <i class="fas fa-star"></i> {{ item.rating || '4.8' }}
                  </div>
                </div>
                <div class="scenic-card-body">
                  <h3 class="scenic-card-name">{{ item.name }}</h3>
                  <p class="scenic-card-location">
                    <i class="fas fa-map-marker-alt"></i>
                    {{ item.location || '未知地区' }}
                  </p>
                  <p class="scenic-card-desc">{{ truncateText(item.description, 50) }}</p>
                </div>
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </section>

    <!-- 热门攻略 -->
    <section class="hot-guides-wrapper">
      <div class="content-container">
        <div class="section-header-row">
          <h2 class="section-main-title">
            <i class="fas fa-fire"></i>
            热门攻略
          </h2>
          <router-link to="/guide" class="view-all-link">
            查看全部 <i class="fas fa-arrow-right"></i>
          </router-link>
        </div>

        <el-skeleton :loading="guideLoading" animated :count="2" :throttle="500">
          <template #template>
            <div class="guide-cards-grid">
              <div v-for="i in 2" :key="i" class="skeleton-card">
                <el-skeleton-item variant="image" style="width: 100%; height: 200px" />
                <div style="padding: 16px;">
                  <el-skeleton-item variant="h3" style="width: 80%; margin-bottom: 8px" />
                  <el-skeleton-item variant="text" style="width: 60%; margin-bottom: 8px" />
                  <el-skeleton-item variant="text" style="width: 40%" />
                </div>
              </div>
            </div>
          </template>
          <template #default>
            <div class="guide-cards-grid">
              <div v-for="item in guideList.slice(0, 2)" :key="item.id"
                   class="guide-item-card"
                   @click="goToGuideDetail(item.id)">
                <img :src="getImageUrl(item.coverImage)" :alt="item.title" class="guide-card-image" />
                <div class="guide-card-body">
                  <h3 class="guide-card-title">{{ item.title }}</h3>
                  <div class="guide-card-meta">
                    <span><i class="fas fa-user"></i>{{ item.userNickname || '旅行达人' }}</span>
                    <span><i class="fas fa-eye"></i>{{ item.views || '1.2k' }}</span>
                    <span><i class="fas fa-heart"></i>{{ item.likes || '328' }}</span>
                  </div>
                  <p class="guide-card-excerpt">{{ truncateText(item.summary || item.content, 80) }}</p>
                </div>
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import SmartSearch from '@/components/common/SmartSearch.vue'
import CollaborativeFilteringRecommendation from '@/components/frontend/CollaborativeFilteringRecommendation.vue'
import {
  ArrowRight,
  Location,
  View,
  Calendar,
  Sunny,
  House,
  TakeawayBox,
  OfficeBuilding,
  Ship,
  Star
} from '@element-plus/icons-vue'

const router = useRouter()
const baseAPI = import.meta.env.VITE_API_BASE_URL || '/api'

// 景点数据
const scenicList = ref([])
const scenicLoading = ref(true)

// 攻略数据
const guideList = ref([])
const guideLoading = ref(true)

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : baseAPI + url
}

// 截断文本
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 格式化开放时间
const formatOpeningHours = (hours) => {
  if (!hours) return '暂无信息'
  return hours.split('-')[0].trim()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 跳转到景点详情
const goToScenicDetail = (scenicId) => {
  router.push(`/scenic/${scenicId}`)
}

// 跳转到攻略详情
const goToGuideDetail = (guideId) => {
  router.push(`/guide/detail/${guideId}`)
}

// 搜索功能
const handleSearch = (keyword) => {
  if (keyword && keyword.trim()) {
    router.push(`/scenic?search=${encodeURIComponent(keyword.trim())}`)
  }
}

// 获取热门景点
const fetchHotScenics = async () => {
  scenicLoading.value = true
  try {
    await request.get('/scenic/hot', {
      limit: 4
    }, {
      showDefaultMsg: false,
      onSuccess: (data) => {
        scenicList.value = data || []
      }
    })
  } catch (error) {
    console.error('获取热门景点失败:', error)
  } finally {
    scenicLoading.value = false
  }
}

// 获取精选攻略
const fetchHotGuides = async () => {
  guideLoading.value = true
  try {
    await request.get('/guide/hot', {
      limit: 3
    }, {
      showDefaultMsg: false,
      onSuccess: (data) => {
        guideList.value = data || []
      }
    })
  } catch (error) {
    console.error('获取精选攻略失败:', error)
  } finally {
    guideLoading.value = false
  }
}

// 滚动动画观察器
const observeElements = () => {
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animate-fade-in-up')
      }
    })
  }, {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  })

  // 观察需要动画的元素
  const animateElements = document.querySelectorAll('.animate-on-scroll')
  animateElements.forEach(el => observer.observe(el))
}

// 添加粒子效果
const createParticles = () => {
  const particlesContainer = document.querySelector('.hero-particles')
  if (!particlesContainer) return

  for (let i = 0; i < 20; i++) {
    const particle = document.createElement('div')
    particle.className = 'particle'
    particle.style.cssText = `
      position: absolute;
      width: ${Math.random() * 4 + 2}px;
      height: ${Math.random() * 4 + 2}px;
      background: rgba(255, 255, 255, ${Math.random() * 0.5 + 0.2});
      border-radius: 50%;
      left: ${Math.random() * 100}%;
      top: ${Math.random() * 100}%;
      animation: particleFloat ${Math.random() * 3 + 4}s ease-in-out infinite;
      animation-delay: ${Math.random() * 2}s;
    `
    particlesContainer.appendChild(particle)
  }
}

onMounted(() => {
  fetchHotScenics()
  fetchHotGuides()

  // 延迟执行动画相关功能，确保DOM已渲染
  nextTick(() => {
    observeElements()
    createParticles()
  })
})
</script>

<style lang="scss" scoped>
/* 全局容器样式 */
.home-container {
  width: 100%;
  background: #f8f9fa;
  min-height: 100vh;
}

/* Hero静态背景区域 */
.hero-section {
  position: relative;
  height: 500px;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  inset: 0;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(0, 0, 0, 0.5), transparent);
}

.hero-content {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  z-index: 2;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: white;
  margin: 0 0 16px 0;
  max-width: 800px;
  animation: fadeInUp 1s ease-out;
}

.hero-subtitle {
  font-size: 20px;
  color: white;
  margin: 0;
  opacity: 0.9;
  animation: fadeInUp 1.2s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 搜索栏区域 */
.search-bar-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 10;
  margin-top: -32px;
}

.search-bar-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
  padding: 24px;

  /* 覆盖 SmartSearch 组件样式 */
  :deep(.smart-search) {
    max-width: 100%;
  }

  :deep(.search-input) {
    .el-input__wrapper {
      border-radius: 12px !important;
      box-shadow: none !important;
      border: 2px solid #e5e7eb !important;
      background: white !important;
      padding: 12px 20px !important;
      transition: all 0.3s ease;

      &:hover {
        border-color: #cbd5e1 !important;
      }

      &.is-focus {
        border-color: #3b82f6 !important;
        box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1) !important;
      }

      .el-input__inner {
        font-size: 18px !important;
        color: #1f2937 !important;

        &::placeholder {
          color: #9ca3af !important;
        }
      }

      .el-input__prefix {
        color: #6b7280;
        font-size: 20px;
      }
    }

    .search-btn {
      border-radius: 8px !important;
      background: #3b82f6 !important;
      border: none !important;
      padding: 12px 32px !important;
      font-weight: 600 !important;
      font-size: 16px !important;
      box-shadow: none !important;
      transition: all 0.3s ease !important;

      &::before {
        display: none !important;
      }

      &:hover {
        background: #2563eb !important;
        transform: none !important;
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3) !important;
      }

      &:active {
        transform: scale(0.98) !important;
      }
    }
  }
}

/* 通用容器 */
.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;

}

/* 区块样式 */
.ai-recommendation-wrapper {
  padding: 32px 0;
  background: #f8f9fa;
}

.cf-recommendation-wrapper {
  padding: 0 0 32px 0;
  background: #f8f9fa;
}

.featured-scenic-wrapper {
  padding: 64px 0;

}

.hot-guides-wrapper {
  padding: 64px 0;
  background: #f8f9fa;
}

/* 标题样式 */
.section-header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-main-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    color: #3b82f6;
  }

  .fa-star {
    color: #fbbf24;
  }

  .fa-fire {
    color: #f97316;
  }
}

.view-all-link {
  color: #3b82f6;
  font-size: 16px;
  text-decoration: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;

  &:hover {
    color: #2563eb;

    i {
      transform: translateX(4px);
    }
  }

  i {
    font-size: 14px;
    transition: transform 0.3s ease;
  }
}

/* 景点卡片 */
.scenic-cards-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.scenic-item-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);

    .scenic-card-image img {
      transform: scale(1.1);
    }
  }
}

.scenic-card-image {
  height: 300px;
  overflow: hidden;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
}

.scenic-price-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  z-index: 2;

  &.price-tag {
    background: rgba(255, 255, 255, 0.95);
    color: #1f2937;
  }
}

.scenic-rating-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  z-index: 2;

  i {
    color: #fbbf24;
  }
}

.scenic-card-body {
  padding: 20px;
}

.scenic-card-name {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 12px 0;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.scenic-card-location {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 6px;

  i {
    color: #3b82f6;
  }
}

.scenic-card-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 攻略卡片 */
.guide-cards-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.guide-item-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: row;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);

    .guide-card-image {
      transform: scale(1.05);
    }
  }
}

.guide-card-image {
  width: 240px;
  height: 200px;
  object-fit: cover;
  flex-shrink: 0;
  transition: transform 0.5s ease;
}

.guide-card-body {
  flex: 1;
  padding: 24px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.guide-card-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.guide-card-meta {
  display: flex;
  gap: 20px;
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 12px;

  span {
    display: flex;
    align-items: center;
    gap: 6px;

    i {
      font-size: 14px;
    }
  }
}

.guide-card-excerpt {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 骨架屏样式 */
.skeleton-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 响应式样式 */
@media (max-width: 1024px) {
  .scenic-cards-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .guide-cards-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-section {
    height: 400px;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-subtitle {
    font-size: 18px;
  }

  .content-container {
    padding: 0 16px;
  }

  .ai-recommendation-wrapper,
  .featured-scenic-wrapper,
  .hot-guides-wrapper {
    padding: 48px 0;
  }

  .section-main-title {
    font-size: 28px;
  }

  .scenic-cards-grid {
    grid-template-columns: 1fr;
  }

  .guide-item-card {
    flex-direction: column;
  }

  .guide-card-image {
    width: 100%;
    height: 200px;
  }
}

@media (max-width: 480px) {
  .hero-section {
    height: 300px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .search-bar-wrapper {
    padding: 0 16px;
  }

  .section-main-title {
    font-size: 24px;
  }
}
</style>
