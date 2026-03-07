<template>
  <div class="my-collection-container">
    <!-- 页面头部 - 红色渐变背景 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="fas fa-heart"></i>
          我的收藏
        </h1>
        <p class="page-subtitle">
          收藏您喜欢的景点和攻略
        </p>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="collection-section">
      <div class="section-container">
        <!-- 分类标签按钮 -->
        <div class="category-tabs">
          <button
              :class="['category-btn', { active: activeTab === 'scenic' }]"
              @click="handleTabChange('scenic')"
          >
            景点 ({{ scenicTotal }})
          </button>
          <button
              :class="['category-btn', { active: activeTab === 'guide' }]"
              @click="handleTabChange('guide')"
          >
            攻略 ({{ guideTotal }})
          </button>
        </div>

        <!-- 景点收藏内容 -->
        <div v-if="activeTab === 'scenic'">
          <!-- 加载状态 -->
          <div v-if="scenicLoading" class="loading-state">
            <el-skeleton :rows="8" animated />
          </div>

          <!-- 空状态 -->
          <div v-else-if="scenicCollections.length === 0" class="empty-state">
            <i class="fas fa-mountain empty-icon"></i>
            <h3 class="empty-title">暂无收藏</h3>
            <p class="empty-desc">您还没有收藏任何内容，快去发现喜欢的景点吧！</p>
            <button class="empty-action" @click="goToScenicList">
              去发现景点
            </button>
          </div>

          <!-- 景点收藏网格 -->
          <div v-else class="collection-grid">
            <div
                v-for="collection in scenicCollections"
                :key="collection.id"
                class="scenic-card"
            >
              <!-- 收藏按钮 -->
              <button
                  class="favorite-btn"
                  @click.stop="handleCancelScenicCollection(collection.scenicInfo.id)"
              >
                <i class="fas fa-heart"></i>
              </button>

              <div class="scenic-image">
                <img :src="getImageUrl(collection.scenicInfo.imageUrl)" :alt="collection.scenicInfo.name" />
                <div v-if="collection.scenicInfo.price === 0" class="scenic-badge free">免费</div>
                <div v-else-if="collection.scenicInfo.price > 0" class="scenic-badge price">
                  ¥{{ collection.scenicInfo.price }}
                </div>
                <div class="scenic-rating">
                  <i class="fas fa-star"></i> 4.8
                </div>
              </div>

              <div class="scenic-content" @click="goToScenicDetail(collection.scenicInfo.id)">
                <h3 class="scenic-name">{{ collection.scenicInfo.name }}</h3>
                <p class="scenic-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ collection.scenicInfo.location }}
                </p>
                <p class="scenic-desc" v-if="collection.scenicInfo.categoryInfo">
                  {{ collection.scenicInfo.categoryInfo.name }}
                </p>
                <div class="scenic-footer">
                  <span class="collection-time">
                    <i class="fas fa-clock"></i>
                    收藏于 {{ formatDate(collection.createTime) }}
                  </span>
                  <a href="javascript:void(0)" class="view-link" @click.stop="goToScenicDetail(collection.scenicInfo.id)">
                    查看 <i class="fas fa-arrow-right"></i>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- 景点收藏分页 -->
          <div class="pagination-wrapper" v-if="scenicTotal > 0 && activeTab === 'scenic'">
            <el-pagination
                background
                layout="total, prev, pager, next"
                :total="scenicTotal"
                :page-size="scenicPageSize"
                :current-page="scenicCurrentPage"
                @current-change="handleScenicPageChange"
            />
          </div>
        </div>

        <!-- 攻略收藏内容 -->
        <div v-if="activeTab === 'guide'">
          <!-- 加载状态 -->
          <div v-if="guideLoading" class="loading-state">
            <el-skeleton :rows="8" animated />
          </div>

          <!-- 空状态 -->
          <div v-else-if="guideCollections.length === 0" class="empty-state">
            <i class="fas fa-book empty-icon"></i>
            <h3 class="empty-title">暂无收藏攻略</h3>
            <p class="empty-desc">您还没有收藏任何攻略，快去发现精彩的旅游攻略吧！</p>
            <button class="empty-action" @click="goToGuideList">
              去发现攻略
            </button>
          </div>

          <!-- 攻略收藏网格 -->
          <div v-else class="collection-grid">
            <div
                v-for="collection in guideCollections"
                :key="collection.id"
                class="guide-card"
            >
              <!-- 收藏按钮 -->
              <button
                  class="favorite-btn"
                  @click.stop="handleCancelGuideCollection(collection.guideId)"
              >
                <i class="fas fa-heart"></i>
              </button>

              <img :src="getImageUrl(collection.guideCoverImage)" :alt="collection.guideTitle" class="guide-image" />

              <div class="guide-content" @click="goToGuideDetail(collection.guideId)">
                <div class="guide-tags">
                  <span class="tag tag-guide">攻略</span>
                </div>
                <h3 class="guide-title">{{ collection.guideTitle }}</h3>
                <div class="guide-meta">
                  <span class="meta-item">
                    <i class="fas fa-eye"></i>
                    {{ collection.guideViews || 0 }} 浏览
                  </span>
                  <span class="meta-item">
                    <i class="fas fa-user"></i>
                    {{ collection.username || collection.userNickname }}
                  </span>
                </div>
                <div class="guide-footer">
                  <span class="collection-time">
                    <i class="fas fa-clock"></i>
                    收藏于 {{ formatDate(collection.createTime) }}
                  </span>
                  <a href="javascript:void(0)" class="view-link" @click.stop="goToGuideDetail(collection.guideId)">
                    阅读 <i class="fas fa-arrow-right"></i>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- 攻略收藏分页 -->
          <div class="pagination-wrapper" v-if="guideTotal > 0 && activeTab === 'guide'">
            <el-pagination
                background
                layout="total, prev, pager, next"
                :total="guideTotal"
                :page-size="guidePageSize"
                :current-page="guideCurrentPage"
                @current-change="handleGuidePageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import {
  MapLocation,
  Document,
  Search,
  Clock,
  Location,
  Delete,
  View,
  User
} from '@element-plus/icons-vue'

const router = useRouter()
const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'
const userStore = useUserStore()

// 当前活跃标签页
const activeTab = ref('scenic')

// 景点收藏相关数据
const scenicLoading = ref(false)
const scenicCollections = ref([])
const scenicCurrentPage = ref(1)
const scenicPageSize = ref(12)
const scenicTotal = ref(0)

// 攻略收藏相关数据
const guideLoading = ref(false)
const guideCollections = ref([])
const guideCurrentPage = ref(1)
const guidePageSize = ref(12)
const guideTotal = ref(0)

// 获取用户收藏的景点
const fetchScenicCollections = async () => {
  scenicLoading.value = true
  try {
    await request.get('/scenic-collection/user', {
      currentPage: scenicCurrentPage.value,
      size: scenicPageSize.value,
      userId: userStore.userInfo.id
    }, {
      showDefaultMsg: false,
      onSuccess: (data) => {
        scenicCollections.value = data.records || []
        scenicTotal.value = data.total || 0
      }
    })
  } catch (error) {
    console.error('获取收藏景点失败:', error)
  } finally {
    scenicLoading.value = false
  }
}

// 获取用户收藏的攻略
const fetchGuideCollections = async () => {
  guideLoading.value = true
  try {
    await request.get('/collection/my', {
      currentPage: guideCurrentPage.value,
      size: guidePageSize.value
    }, {
      showDefaultMsg: false,
      onSuccess: (data) => {
        guideCollections.value = data.records || []
        guideTotal.value = data.total || 0
      }
    })
  } catch (error) {
    console.error('获取收藏攻略失败:', error)
  } finally {
    guideLoading.value = false
  }
}

// 标签页切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  if (tabName === 'scenic' && scenicCollections.value.length === 0) {
    fetchScenicCollections()
  }
  if (tabName === 'guide' && guideCollections.value.length === 0) {
    fetchGuideCollections()
  }
}

// 景点收藏分页
const handleScenicPageChange = (page) => {
  scenicCurrentPage.value = page
  fetchScenicCollections()
}

// 攻略收藏分页
const handleGuidePageChange = (page) => {
  guideCurrentPage.value = page
  fetchGuideCollections()
}

// 跳转到景点详情
const goToScenicDetail = (scenicId) => {
  router.push(`/scenic/${scenicId}`)
}

// 跳转到攻略详情
const goToGuideDetail = (guideId) => {
  router.push(`/guide/detail/${guideId}`)
}

// 跳转到景点列表
const goToScenicList = () => {
  router.push('/scenic')
}

// 跳转到攻略列表
const goToGuideList = () => {
  router.push('/guide')
}

// 取消景点收藏
const handleCancelScenicCollection = (scenicId) => {
  ElMessageBox.confirm('确认取消收藏该景点?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/scenic-collection/${scenicId}`, {
        successMsg: '取消收藏成功',
        onSuccess: () => {
          fetchScenicCollections()
        }
      })
    } catch (error) {
      console.error('取消景点收藏失败:', error)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 取消攻略收藏
const handleCancelGuideCollection = (guideId) => {
  ElMessageBox.confirm('确认取消收藏该攻略?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/collection/cancel?guideId=${guideId}`, {
        successMsg: '取消收藏成功',
        onSuccess: () => {
          fetchGuideCollections()
        }
      })
    } catch (error) {
      console.error('取消攻略收藏失败:', error)
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return '/default-image.jpg'
  return url.startsWith('http') ? url : baseAPI + url
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  // 默认加载所有收藏
  fetchScenicCollections()
  fetchGuideCollections()
})
</script>

<style lang="scss" scoped>
.my-collection-container {
  min-height: 100vh;
  background: #f3f4f6;
  font-family: "Source Han Sans CN", "Roboto", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;

  // 页面头部 - 红色渐变
  .page-header {
    background: linear-gradient(135deg, #ef4444 0%, #ec4899 100%);
    color: white;
    padding: 48px 0;

    .header-content {
      max-width: 1280px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .page-title {
      font-size: 36px;
      font-weight: 700;
      margin: 0 0 16px;
      display: flex;
      align-items: center;
      gap: 12px;

      i {
        font-size: 32px;
      }
    }

    .page-subtitle {
      font-size: 20px;
      opacity: 0.9;
      margin: 0;
    }
  }

  // 主内容区域
  .collection-section {
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 24px;
  }

  // 分类标签按钮
  .category-tabs {
    background: white;
    border-radius: 24px;
    padding: 24px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 16px;

    .category-btn {
      padding: 12px 24px;
      border-radius: 16px;
      font-weight: 600;
      font-size: 14px;
      border: 2px solid #e5e7eb;
      background: white;
      color: #4b5563;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        border-color: #ef4444;
        color: #ef4444;
      }

      &.active {
        background: #ef4444;
        border-color: #ef4444;
        color: white;
      }
    }
  }

  // 加载状态
  .loading-state {
    padding: 60px 20px;
  }

  // 空状态
  .empty-state {
    background: white;
    border-radius: 24px;
    padding: 80px 20px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    text-align: center;

    .empty-icon {
      font-size: 96px;
      color: #d1d5db;
      margin-bottom: 16px;
    }

    .empty-title {
      font-size: 24px;
      font-weight: 700;
      color: #111827;
      margin: 0 0 8px;
    }

    .empty-desc {
      font-size: 16px;
      color: #6b7280;
      margin: 0 0 24px;
    }

    .empty-action {
      display: inline-block;
      padding: 12px 32px;
      background: linear-gradient(135deg, #ef4444 0%, #ec4899 100%);
      color: white;
      border: none;
      border-radius: 16px;
      font-weight: 600;
      font-size: 14px;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 10px 15px -3px rgba(239, 68, 68, 0.3);
        transform: translateY(-2px);
      }
    }
  }

  // 收藏网格布局
  .collection-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
    margin-bottom: 32px;
  }

  // 景点卡片样式
  .scenic-card {
    background: white;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    position: relative;
    cursor: pointer;

    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);

      .scenic-image img {
        transform: scale(1.1);
      }
    }

    // 收藏按钮
    .favorite-btn {
      position: absolute;
      top: 16px;
      right: 16px;
      z-index: 10;
      width: 40px;
      height: 40px;
      background: #ef4444;
      color: white;
      border: none;
      border-radius: 50%;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;

      &:hover {
        background: #dc2626;
        transform: scale(1.1);
      }

      i {
        font-size: 16px;
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

      .scenic-rating {
        position: absolute;
        top: 16px;
        right: 60px;
        background: rgba(255, 255, 255, 0.95);
        padding: 8px 16px;
        border-radius: 9999px;
        font-weight: 600;
        color: #f59e0b;
        backdrop-filter: blur(10px);

        i {
          margin-right: 4px;
        }
      }
    }

    .scenic-content {
      padding: 20px;

      .scenic-name {
        font-size: 18px;
        font-weight: 700;
        color: #111827;
        margin: 0 0 8px;
      }

      .scenic-location {
        color: #6b7280;
        font-size: 14px;
        margin: 0 0 8px;

        i {
          margin-right: 8px;
        }
      }

      .scenic-desc {
        color: #4b5563;
        font-size: 15px;
        margin: 0 0 16px;
      }

      .scenic-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-top: 16px;

        .collection-time {
          font-size: 14px;
          color: #9ca3af;

          i {
            margin-right: 4px;
          }
        }

        .view-link {
          color: #ef4444;
          font-weight: 600;
          text-decoration: none;
          transition: all 0.3s ease;

          &:hover {
            color: #dc2626;

            i {
              transform: translateX(4px);
            }
          }

          i {
            margin-left: 4px;
            transition: transform 0.3s ease;
          }
        }
      }
    }
  }

  // 攻略卡片样式
  .guide-card {
    background: white;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    position: relative;
    cursor: pointer;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
    }

    // 收藏按钮
    .favorite-btn {
      position: absolute;
      top: 16px;
      right: 16px;
      z-index: 10;
      width: 40px;
      height: 40px;
      background: #ef4444;
      color: white;
      border: none;
      border-radius: 50%;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;

      &:hover {
        background: #dc2626;
        transform: scale(1.1);
      }

      i {
        font-size: 16px;
      }
    }

    .guide-image {
      width: 100%;
      height: 192px;
      object-fit: cover;
    }

    .guide-content {
      padding: 20px;

      .guide-tags {
        margin-bottom: 8px;

        .tag {
          display: inline-block;
          padding: 4px 8px;
          border-radius: 9999px;
          font-size: 12px;
          font-weight: 600;

          &.tag-guide {
            background: #fef3c7;
            color: #d97706;
          }
        }
      }

      .guide-title {
        font-size: 18px;
        font-weight: 700;
        color: #111827;
        margin: 0 0 12px;
      }

      .guide-meta {
        display: flex;
        gap: 24px;
        color: #6b7280;
        font-size: 14px;
        margin-bottom: 16px;

        .meta-item {
          i {
            margin-right: 4px;
          }
        }
      }

      .guide-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .collection-time {
          font-size: 14px;
          color: #9ca3af;

          i {
            margin-right: 4px;
          }
        }

        .view-link {
          color: #ef4444;
          font-weight: 600;
          text-decoration: none;
          transition: all 0.3s ease;

          &:hover {
            color: #dc2626;

            i {
              transform: translateX(4px);
            }
          }

          i {
            margin-left: 4px;
            transition: transform 0.3s ease;
          }
        }
      }
    }
  }

  .image-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, transparent 0%, rgba(0, 0, 0, 0.7) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
    display: flex;
    align-items: flex-end;
    padding: 20px;
  }

  .overlay-content {
    color: white;

    .collection-time,
    .guide-views {
      display: flex;
      align-items: center;
      font-size: 14px;
      font-weight: 600;
      gap: 4px;

      .el-icon {
        color: #ffd700;
      }
    }
  }

  .card-badges {
    position: absolute;
    top: 12px;
    left: 12px;
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .badge {
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 600;
    backdrop-filter: blur(10px);

    &.category {
      background: linear-gradient(45deg, #667eea, #764ba2);
      color: white;
    }

    &.free {
      background: linear-gradient(45deg, #10b981, #059669);
      color: white;
    }

    &.price {
      background: rgba(255, 255, 255, 0.9);
      color: #333;
    }

    &.guide {
      background: linear-gradient(45deg, #f59e0b, #d97706);
      color: white;
    }
  }

  .card-content {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .item-name {
    margin: 0 0 12px;
    font-size: 18px;
    font-weight: 700;
    color: #2d3748;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    line-height: 1.3;
  }

  .item-location {
    display: flex;
    align-items: center;
    font-size: 14px;
    color: #64748b;
    margin-bottom: 12px;
    gap: 4px;

    .el-icon {
      color: #667eea;
    }
  }

  .guide-meta {
    display: flex;
    justify-content: space-between;
    margin-bottom: 12px;

    .meta-item {
      display: flex;
      align-items: center;
      font-size: 12px;
      color: #64748b;
      gap: 4px;

      .el-icon {
        color: #667eea;
      }
    }
  }

  .card-footer {
    margin-top: auto;
    padding-top: 16px;

    .collection-date {
      font-size: 12px;
      color: #94a3b8;
      margin-bottom: 12px;
    }

    .card-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 8px;

      .detail-btn {
        border-radius: 20px;
        background: linear-gradient(45deg, #667eea, #764ba2);
        border: none;
        font-weight: 600;
        padding: 8px 16px;
        flex: 1;

        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
      }

      .cancel-btn {
        border-radius: 50%;
        width: 36px;
        height: 36px;
        padding: 0;
        background: #f56565;
        border: none;
        color: white;

        &:hover {
          background: #e53e3e;
          transform: scale(1.1);
        }
      }
    }
  }

  // 分页样式
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 40px;
  }

  .modern-pagination {
    :deep(.el-pagination) {
      .el-pager li {
        border-radius: 8px;
        margin: 0 4px;
        transition: all 0.3s ease;

        &:hover {
          background: #667eea;
          color: white;
        }

        &.is-active {
          background: linear-gradient(45deg, #667eea, #764ba2);
          color: white;
        }
      }

      .btn-prev,
      .btn-next {
        border-radius: 8px;
        transition: all 0.3s ease;

        &:hover {
          background: #667eea;
          color: white;
        }
      }
    }
  }
}
</style>