<template>
  <div class="my-guide-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-wrapper">
        <h1 class="page-title">
          <i class="fas fa-book-open"></i>
          我的攻略
        </h1>
        <p class="page-subtitle">管理您已发布的旅游攻略内容,分享您的旅行经验</p>
      </div>
    </div>

    <div class="content-wrapper">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="stats-info">
          找到 <span class="stats-number">{{ total }}</span> 篇攻略
        </div>
        <el-button
            type="primary"
            class="publish-btn"
            @click="goEdit"
        >
          <i class="fas fa-pen"></i>
          发布攻略
        </el-button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="tableData.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <h3 class="empty-title">您还没有发布任何攻略</h3>
        <p class="empty-desc">分享您的旅行经验,帮助更多人发现美好</p>
        <el-button type="primary" @click="goEdit" class="empty-action">
          <i class="fas fa-pen"></i>
          立即发布
        </el-button>
      </div>

      <!-- 攻略列表 -->
      <div v-else class="guide-list">
        <div
            v-for="guide in tableData"
            :key="guide.id"
            class="guide-card"
            @click="viewGuide(guide)"
        >
          <img
              :src="getImageUrl(guide.coverImage)"
              :alt="guide.title"
              class="guide-image"
          />
          <div class="guide-content">
            <h3 class="guide-title">{{ guide.title }}</h3>
            <div class="guide-meta">
              <span class="meta-item">
                <i class="fas fa-user"></i>
                {{ userStore.userInfo?.username || '匿名用户' }}
              </span>
              <span class="meta-item">
                <i class="fas fa-calendar"></i>
                {{ formatDate(guide.createTime) }}
              </span>
              <span class="meta-item">
                <i class="fas fa-eye"></i>
                {{ guide.views || 0 }}
              </span>
            </div>
            <p class="guide-excerpt" v-if="guide.content">
              {{ getExcerpt(guide.content) }}
            </p>
            <div class="guide-footer">
              <div class="guide-tags">
                <!-- 标签已移除 -->
              </div>
              <div class="guide-actions">
                <el-button
                    type="primary"
                    size="small"
                    @click.stop="viewGuide(guide)"
                >
                  阅读全文
                  <i class="fas fa-arrow-right"></i>
                </el-button>
                <el-button
                    size="small"
                    @click.stop="goEdit(guide)"
                >
                  <i class="fas fa-edit"></i>
                </el-button>
                <el-button
                    type="danger"
                    size="small"
                    @click.stop="deleteGuide(guide)"
                >
                  <i class="fas fa-trash"></i>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
            background
            layout="total, prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { formatDate } from '@/utils/dateUtils'
import { useUserStore } from '@/store/user'

const baseAPI = import.meta.env.VITE_APP_BASE_API || '/api'
const router = useRouter()
const userStore = useUserStore()
const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return '/default-guide-cover.jpg'
  return url.startsWith('http') ? url : baseAPI + url
}

// 判断是否是新发布的攻略（7天内）
const isNew = (dateString) => {
  if (!dateString) return false
  const publishDate = new Date(dateString)
  const now = new Date()
  const diffTime = now - publishDate
  const diffDays = diffTime / (1000 * 60 * 60 * 24)
  return diffDays < 7
}

// 获取内容摘要
const getExcerpt = (content) => {
  if (!content) return '暂无内容简介...'
  // 移除HTML标签
  const text = content.replace(/<[^>]+>/g, '')
  return text.length > 150 ? text.substring(0, 150) + '...' : text
}

const fetchGuides = async () => {
  loading.value = true
  try {
    await request.get('/guide/page', {
      userId: userStore.userInfo?.id,
      currentPage: currentPage.value,
      size: pageSize.value
    }, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        tableData.value = res.records || []
        total.value = res.total || 0
      }
    })
  } catch (error) {
    console.error('获取我的攻略列表失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(fetchGuides)

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchGuides()
}

const viewGuide = (row) => {
  router.push(`/guide/detail/${row.id}`)
}

const goEdit = (row) => {
  if (row) {
    router.push({ name: 'GuideEdit', query: { id: row.id } })
  } else {
    router.push({ name: 'GuideEdit' })
  }
}

const deleteGuide = (row) => {
  ElMessageBox.confirm('确定要删除该攻略吗？删除后无法恢复！', '删除确认', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning',
    closeOnClickModal: false
  }).then(async () => {
    try {
      await request.delete(`/guide/delete/${row.id}`, {
        successMsg: '删除成功',
        onSuccess: () => fetchGuides()
      })
    } catch (error) {
      console.error('删除攻略失败', error)
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.my-guide-container {
  min-height: 100vh;
  background: #f9fafb;

  // 页面头部
  .page-header {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    color: white;
    padding: 48px 0;

    .header-wrapper {
      max-width: 1280px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .page-title {
      font-size: 36px;
      font-weight: bold;
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

  // 内容包装器
  .content-wrapper {
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 24px;
  }

  // 操作栏
  .action-bar {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .stats-info {
      color: #6b7280;
      font-size: 14px;

      .stats-number {
        font-weight: 600;
        color: #111827;
        font-size: 16px;
      }
    }

    .publish-btn {
      padding: 12px 32px;
      border-radius: 12px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      font-weight: 600;

      &:hover {
        opacity: 0.9;
      }

      i {
        margin-right: 8px;
      }
    }
  }

  // 加载状态
  .loading-state {
    background: white;
    border-radius: 16px;
    padding: 40px;
  }

  // 空状态
  .empty-state {
    background: white;
    border-radius: 16px;
    padding: 80px 40px;
    text-align: center;

    .empty-icon {
      font-size: 64px;
      margin-bottom: 20px;
    }

    .empty-title {
      font-size: 24px;
      font-weight: 600;
      color: #111827;
      margin: 0 0 8px;
    }

    .empty-desc {
      font-size: 16px;
      color: #6b7280;
      margin: 0 0 24px;
    }

    .empty-action {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      border-radius: 12px;
      padding: 12px 32px;
      font-weight: 600;

      i {
        margin-right: 8px;
      }
    }
  }

  // 攻略列表
  .guide-list {
    display: flex;
    flex-direction: column;
    gap: 24px;

    .guide-card {
      background: white;
      border-radius: 16px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      overflow: hidden;
      display: flex;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      }

      .guide-image {
        width: 240px;
        height: 180px;
        object-fit: cover;
        flex-shrink: 0;
      }

      .guide-content {
        flex: 1;
        padding: 24px;
        display: flex;
        flex-direction: column;

        .guide-title {
          font-size: 20px;
          font-weight: 600;
          color: #111827;
          margin: 0 0 12px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .guide-meta {
          display: flex;
          gap: 24px;
          margin-bottom: 12px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            color: #6b7280;

            i {
              color: #2563eb;
            }
          }
        }

        .guide-excerpt {
          font-size: 14px;
          color: #6b7280;
          line-height: 1.6;
          margin: 0 0 16px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .guide-footer {
          margin-top: auto;
          display: flex;
          justify-content: space-between;
          align-items: center;

          .guide-tags {
            display: flex;
            gap: 8px;

            .tag {
              padding: 4px 12px;
              border-radius: 12px;
              font-size: 12px;
              font-weight: 600;

              &.tag-new {
                background: #fef3c7;
                color: #92400e;
              }

              &.tag-guide {
                background: #fce7f3;
                color: #9f1239;
              }
            }
          }

          .guide-actions {
            display: flex;
            gap: 8px;

            .el-button {
              border-radius: 8px;

              &.el-button--primary {
                background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
                border: none;

                i {
                  margin-left: 4px;
                }
              }

              &.el-button--small {
                padding: 8px 16px;
              }

              i {
                font-size: 14px;
              }
            }
          }
        }
      }
    }
  }

  // 分页
  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 32px;

    :deep(.el-pagination) {
      .el-pager li {
        border-radius: 8px;
        margin: 0 4px;
        transition: all 0.3s;

        &:hover {
          background: #2563eb;
          color: white;
        }

        &.is-active {
          background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
          color: white;
        }
      }

      .btn-prev, .btn-next {
        border-radius: 8px;
        transition: all 0.3s;

        &:hover {
          background: #2563eb;
          color: white;
        }
      }
    }
  }
}
</style>