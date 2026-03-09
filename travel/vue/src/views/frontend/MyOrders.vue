<template>
  <div class="orders-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="fas fa-shopping-bag"></i>
          我的订单
        </h1>
        <p class="page-subtitle">管理您的所有订单</p>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 订单状态标签 -->
      <div class="status-tabs">
        <button
            v-for="tab in statusTabs"
            :key="tab.value"
            :class="['tab-btn', { active: activeTab === tab.value }]"
            @click="handleTabChange(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="6" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="orderList.length === 0" class="empty-state">
        <i class="fas fa-shopping-bag empty-icon"></i>
        <h3 class="empty-title">暂无订单</h3>
        <p class="empty-desc">您还没有任何订单,快去预订门票吧!</p>
        <button class="empty-btn" @click="goToTicketList">
          去预订门票
        </button>
      </div>

      <!-- 订单列表 -->
      <div v-else class="orders-list">
        <div
            v-for="order in orderList"
            :key="order.id"
            class="order-card"
        >
          <!-- 订单头部 -->
          <div class="order-header" :class="getHeaderClass(order.status)">
            <div class="order-meta">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span class="order-time">{{ formatTime(order.createTime) }}</span>
            </div>
            <span class="order-status" :class="getStatusClass(order.status)">
              <i :class="getStatusIcon(order.status)"></i>
              {{ getOrderStatusText(order.status) }}
            </span>
          </div>

          <!-- 订单内容 -->
          <div class="order-body">
            <div class="order-main">
              <!-- 景点图片 -->
              <div class="scenic-image">
                <img :src="order.scenicImage || 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=150&h=150&fit=crop'"
                     :alt="order.scenicName">
              </div>

              <!-- 订单信息 -->
              <div class="order-info">
                <h3 class="ticket-title">{{ order.ticketName }}</h3>
                <div class="info-item">
                  <i class="fas fa-calendar"></i>
                  <span>游玩日期: {{ formatDate(order.visitDate) }}</span>
                </div>
                <div class="info-item">
                  <i class="fas fa-user"></i>
                  <span>{{ order.ticketName }} × {{ order.quantity }}</span>
                </div>
                <div class="info-item" v-if="order.visitorName">
                  <i class="fas fa-phone"></i>
                  <span>联系人: {{ order.visitorName }} {{ order.visitorPhone }}</span>
                </div>
                <!-- 待支付倒计时 -->
                <div class="info-item warning" v-if="order.status === 0">
                  <i class="fas fa-exclamation-circle"></i>
                  <span>请尽快完成支付</span>
                </div>
              </div>
            </div>

            <!-- 价格和操作 -->
            <div class="order-actions">
              <div class="price-section">
                <div class="price-amount">
                  ¥{{ order.totalAmount }}
                </div>
              </div>
              <div class="action-buttons">
                <!-- 待支付状态 -->
                <template v-if="order.status === 0">
                  <button class="btn btn-primary" @click="payOrder(order)">
                    立即支付
                  </button>
                  <button class="btn btn-secondary" @click="cancelOrder(order.id)">
                    取消订单
                  </button>
                </template>
                <!-- 已支付状态 -->
                <template v-else-if="order.status === 1">
                  <button class="btn btn-primary" @click="viewOrderDetail(order.id)">
                    查看详情
                  </button>
                  <button class="btn btn-warning" @click="refundOrder(order)">
                    申请退款
                  </button>
                </template>
                <!-- 其他状态 -->
                <template v-else>
                  <button class="btn btn-primary" @click="viewOrderDetail(order.id)">
                    查看详情
                  </button>
                </template>
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

    <!-- 支付对话框 -->
    <el-dialog
        title="订单支付"
        v-model="payDialogVisible"
        width="500px"
        :close-on-click-modal="false"
    >
      <div class="pay-dialog-content" v-if="currentOrder">
        <div class="order-info">
          <p><strong>订单号:</strong> {{ currentOrder.orderNo }}</p>
          <p><strong>门票名称:</strong> {{ currentOrder.ticketName }}</p>
          <p><strong>游玩日期:</strong> {{ formatDate(currentOrder.visitDate) }}</p>
          <p><strong>数量:</strong> {{ currentOrder.quantity }}</p>
          <p><strong>总金额:</strong> <span class="amount">¥{{ currentOrder.totalAmount }}</span></p>
        </div>

      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="payDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="goToAlipay">去支付</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
        title="订单详情"
        v-model="detailDialogVisible"
        width="600px"
    >
      <div class="order-detail" v-if="currentOrder">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="门票名称">{{ currentOrder.ticketName }}</el-descriptions-item>
          <el-descriptions-item label="景点名称">{{ currentOrder.scenicName }}</el-descriptions-item>
          <el-descriptions-item label="游玩日期">{{ formatDate(currentOrder.visitDate) }}</el-descriptions-item>
          <el-descriptions-item label="购买数量">{{ currentOrder.quantity }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="游客姓名">{{ currentOrder.visitorName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentOrder.visitorPhone }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">
            {{ currentOrder.idCard || '未提供' }}
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            {{ getOrderStatusText(currentOrder.status) }}
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">
            {{ formatTime(currentOrder.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="支付时间" v-if="currentOrder.paymentTime">
            {{ formatTime(currentOrder.paymentTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'


const router = useRouter()

// 分页参数
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 订单列表数据
const orderList = ref([])
const loading = ref(false)
const activeTab = ref('all')

// 对话框控制
const payDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentOrder = ref(null)


// 状态标签配置
const statusTabs = [
  { label: '全部订单', value: 'all' },
  { label: '待支付', value: '0' },
  { label: '已支付', value: '1' },
  { label: '已取消', value: '2' },
  { label: '已退款', value: '3' }
]

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    0: 'status-pending',
    1: 'status-paid',
    2: 'status-cancelled',
    3: 'status-refunded'
  }
  return statusMap[status] || 'status-default'
}

// 获取头部样式类
const getHeaderClass = (status) => {
  const headerMap = {
    0: 'header-pending',
    1: 'header-paid',
    2: 'header-cancelled',
    3: 'header-refunded'
  }
  return headerMap[status] || ''
}

// 获取状态图标
const getStatusIcon = (status) => {
  const iconMap = {
    0: 'fas fa-clock',
    1: 'fas fa-check-circle',
    2: 'fas fa-times-circle',
    3: 'fas fa-undo'
  }
  return iconMap[status] || 'fas fa-info-circle'
}

// 跳转到门票列表
const goToTicketList = () => {
  router.push('/ticket')
}

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const status = activeTab.value === 'all' ? '' : activeTab.value

    await request.get('/order/my', {
      status,
      currentPage: currentPage.value,
      size: pageSize.value
    }, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        orderList.value = res?.records || []
        total.value = res?.total || 0
      }
    })
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orderList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 分页变化事件
const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchOrders()
}

// 标签页切换事件
const handleTabChange = (tab) => {
  activeTab.value = tab
  currentPage.value = 1
  fetchOrders()
}

// 获取订单状态文本
const getOrderStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退款',
  }
  return statusMap[status] || '未知状态'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 支付订单
const payOrder = (order) => {
  if (!order || !order.id) {
    ElMessage.error('订单数据无效')
    return
  }
  currentOrder.value = order
  payDialogVisible.value = true
}

// 确认支付
const confirmPayment = async () => {
  if (!currentOrder.value || !currentOrder.value.id) {
    ElMessage.error('订单数据无效')
    payDialogVisible.value = false
    return
  }

  loading.value = true
  try {
    await request.post(`/order/${currentOrder.value.id}/pay`, null, {
      successMsg: '支付成功',
      onSuccess: () => {
        payDialogVisible.value = false
        fetchOrders()  // 重新加载订单列表
      }
    })
  } catch (error) {
    console.error('支付订单失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到支付宝支付页面
const goToAlipay = () => {
  if (currentOrder.value && currentOrder.value.id) {
    payDialogVisible.value = false;
    // 直接跳转到后端支付接口，后端会返回支付宝支付表单
    window.open(`http://localhost:8080/pay/alipay/${currentOrder.value.id}`);
  } else {
    ElMessage.error('订单信息错误')
  }
}

// 取消订单
const cancelOrder = async (orderId) => {
  ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await request.post(`/order/${orderId}/cancel`, {}, {
        successMsg: '订单已取消',
        onSuccess: () => {
          fetchOrders()  // 重新加载订单列表
        }
      })
    } catch (error) {
      console.error('取消订单失败:', error)
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

// 退款订单
const refundOrder = async (order) => {
  if (!order || !order.id) {
    ElMessage.error('订单数据无效')
    return
  }

  ElMessageBox.confirm(
      `确定要申请退款吗？\n订单金额: ¥${order.totalAmount}`,
      '申请退款',
      {
        confirmButtonText: '确定退款',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    loading.value = true
    try {
      await request.post(`/pay/refund/${order.id}`, null, {
        params: {
          refundAmount: order.totalAmount
        },
        successMsg: '退款申请已提交',
        onSuccess: () => {
          fetchOrders()  // 重新加载订单列表
        }
      })
    } catch (error) {
      console.error('退款申请失败:', error)
      ElMessage.error('退款申请失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }).catch(() => {})
}

// 查看订单详情
const viewOrderDetail = async (orderId) => {
  loading.value = true
  try {
    await request.get(`/order/${orderId}`, {}, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        if (res) {
          currentOrder.value = res
          detailDialogVisible.value = true
        } else {
          ElMessage.error('获取订单详情失败')
        }
      }
    })
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

// 页面加载时获取订单列表
onMounted(() => {
  fetchOrders()
})
</script>

<style lang="scss" scoped>
.orders-container {
  min-height: 100vh;
  background: #f9fafb;

  // 页面头部
  .page-header {
    background: #667eea;
    color: white;
    padding: 48px 0;

    .header-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .page-title {
      font-size: 40px;
      font-weight: 700;
      margin: 0 0 16px;
      display: flex;
      align-items: center;
      gap: 12px;

      i {
        font-size: 36px;
      }
    }

    .page-subtitle {
      font-size: 20px;
      margin: 0;
      opacity: 0.9;
    }
  }

  // 主内容区域
  .main-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 32px 24px;
  }

  // 状态标签
  .status-tabs {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    overflow-x: auto;

    .tab-btn {
      padding: 12px 24px;
      border: 2px solid #e5e7eb;
      background: white;
      color: #6b7280;
      border-radius: 12px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      white-space: nowrap;

      &:hover {
        border-color: #667eea;
        color: #667eea;
      }

      &.active {
        background: #667eea;
        border-color: #667eea;
        color: white;
      }
    }
  }

  // 加载状态
  .loading-state {
    padding: 40px 20px;
  }

  // 空状态
  .empty-state {
    background: white;
    border-radius: 16px;
    padding: 80px 32px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    text-align: center;

    .empty-icon {
      font-size: 96px;
      color: #d1d5db;
      margin-bottom: 16px;
    }

    .empty-title {
      font-size: 20px;
      font-weight: 700;
      color: #111827;
      margin: 0 0 8px;
    }

    .empty-desc {
      font-size: 16px;
      color: #6b7280;
      margin: 0 0 24px;
    }

    .empty-btn {
      display: inline-block;
      padding: 12px 32px;
      background: #667eea;
      color: white;
      border: none;
      border-radius: 12px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;

      &:hover {
        background: #5568d3;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        transform: translateY(-2px);
      }
    }
  }

  // 订单列表
  .orders-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .order-card {
    background: white;
    border-radius: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }
  }

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    border-bottom: 1px solid #e5e7eb;

    &.header-pending {
      background: #fef3c7;
    }

    &.header-paid {
      background: #d1fae5;
    }

    &.header-cancelled {
      background: #f3f4f6;
    }

    &.header-completed {
      background: #f3f4f6;
    }

    .order-meta {
      display: flex;
      align-items: center;
      gap: 16px;

      .order-no {
        font-size: 14px;
        color: #6b7280;
      }

      .order-time {
        font-size: 14px;
        color: #6b7280;
      }
    }

    .order-status {
      padding: 8px 16px;
      border-radius: 9999px;
      font-size: 14px;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 4px;

      &.status-pending {
        background: #f97316;
        color: white;
      }

      &.status-paid {
        background: #10b981;
        color: white;
      }

      &.status-cancelled {
        background: #6b7280;
        color: white;
      }

      &.status-completed {
        background: #6b7280;
        color: white;
      }
    }
  }

  .order-body {
    padding: 24px;

    .order-main {
      display: flex;
      align-items: flex-start;
      gap: 24px;
      margin-bottom: 16px;

      .scenic-image {
        width: 128px;
        height: 128px;
        border-radius: 12px;
        overflow: hidden;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .order-info {
        flex: 1;

        .ticket-title {
          font-size: 20px;
          font-weight: 700;
          color: #111827;
          margin: 0 0 8px;
        }

        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          font-size: 16px;
          color: #6b7280;

          i {
            color: #6b7280;
          }

          &.warning {
            color: #f97316;
            font-weight: 600;

            i {
              color: #f97316;
            }
          }
        }
      }
    }

    .order-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .price-section {
        .price-amount {
          font-size: 30px;
          font-weight: 700;
          color: #667eea;

          &.price-gray {
            color: #6b7280;
          }
        }
      }

      .action-buttons {
        display: flex;
        gap: 8px;

        .btn {
          padding: 8px 24px;
          border-radius: 8px;
          font-size: 16px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.3s ease;
          border: none;

          &.btn-primary {
            background: #667eea;
            color: white;

            &:hover {
              background: #5568d3;
            }
          }

          &.btn-secondary {
            background: white;
            color: #6b7280;
            border: 2px solid #d1d5db;

            &:hover {
              border-color: #667eea;
              color: #667eea;
            }
          }
        }
      }
      &.btn-warning {
        background: #f59e0b;
        color: white;
        border: none;

        &:hover {
          background: #d97706;
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

}

</style>