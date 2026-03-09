<template>
  <div class="payment-container">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-nav">
      <div class="breadcrumb-content">
        <span class="breadcrumb-item" @click="router.push('/')">
          <i class="fa fa-home"></i> 首页
        </span>
        <i class="fa fa-chevron-right breadcrumb-separator"></i>
        <span class="breadcrumb-item" @click="router.push('/tickets')">
          门票预订
        </span>
        <i class="fa fa-chevron-right breadcrumb-separator"></i>
        <span class="breadcrumb-item" @click="router.push('/orders')">
          我的订单
        </span>
        <i class="fa fa-chevron-right breadcrumb-separator"></i>
        <span class="breadcrumb-item active">
          微信支付
        </span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-section">
      <div class="container">
        <el-skeleton :rows="10" animated />
      </div>
    </div>

    <!-- 支付内容 -->
    <div v-else class="payment-content">
      <div class="container">
        <div class="payment-card">
          <div class="payment-header">
            <h2 class="payment-title">
              <i class="fa fa-weixin"></i>
              微信支付
            </h2>
          </div>

          <div class="payment-body">
            <!-- 订单信息 -->
            <div class="order-info">
              <div class="order-item">
                <span class="order-label">订单号:</span>
                <span class="order-value">{{ order?.orderNo }}</span>
              </div>
              <div class="order-item">
                <span class="order-label">门票名称:</span>
                <span class="order-value">{{ order?.ticketName }}</span>
              </div>
              <div class="order-item">
                <span class="order-label">游玩日期:</span>
                <span class="order-value">{{ formatDate(order?.visitDate) }}</span>
              </div>
              <div class="order-item">
                <span class="order-label">数量:</span>
                <span class="order-value">{{ order?.quantity }} 张</span>
              </div>
              <div class="order-item total-item">
                <span class="order-label">支付金额:</span>
                <span class="order-value amount">¥{{ order?.totalAmount }}</span>
              </div>
            </div>

            <!-- 支付二维码 -->
            <div class="qrcode-section">
              <div class="qrcode-tip">
                <el-icon class="tip-icon"><InfoFilled /></el-icon>
                <span>请使用微信扫描二维码完成支付</span>
              </div>
              <div class="qrcode-container">
                <el-image
                    :src="qrcodeUrl"
                    fit="cover"
                    class="qrcode-image"
                />
              </div>
              <div class="qrcode-hint">
                <span>支付完成后，系统会自动跳转到订单页面</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="payment-actions">
              <el-button @click="goBack">
                <el-icon><ArrowLeft /></el-icon>
                返回订单
              </el-button>
              <el-button type="primary" @click="checkPaymentStatus">
                <el-icon><Check /></el-icon>
                确认已支付
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { InfoFilled, ArrowLeft, Check } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 订单ID
const orderId = ref(route.params.orderId)

// 加载状态
const loading = ref(true)

// 订单信息
const order = ref(null)

// 二维码URL
const qrcodeUrl = ref('')

// 轮询定时器
let pollingTimer = null

// 获取订单信息和微信支付参数
const fetchPaymentInfo = async () => {
  try {
    // 获取订单信息
    await request.get(`/order/${orderId.value}`, {}, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        order.value = res
      }
    })

    // 获取微信支付参数
    await request.post(`/pay/wechat/${orderId.value}`, {}, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        // 生成二维码
        // 实际项目中，这里应该使用后端返回的prepayId生成二维码
        // 这里简化处理，使用模拟的二维码
        qrcodeUrl.value = generateQrcodeUrl(res.prepayId)
      }
    })
  } catch (error) {
    ElMessage.error('获取支付信息失败')
    console.error('获取支付信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 生成二维码URL
const generateQrcodeUrl = (prepayId) => {
  // 实际项目中，这里应该使用后端返回的prepayId生成二维码
  // 这里使用一个模拟的二维码生成服务
  const orderInfo = `订单号: ${order.value?.orderNo}\n金额: ¥${order.value?.totalAmount}`
  return `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(orderInfo)}`
}

// 检查支付状态
const checkPaymentStatus = async () => {
  try {
    const res = await request.get(`/order/${orderId.value}`)
    if (res.status === 1) {
      ElMessage.success('支付成功')
      router.push('/orders')
    } else {
      ElMessage.warning('支付尚未完成，请稍后再试')
    }
  } catch (error) {
    console.error('检查支付状态失败:', error)
  }
}

// 轮询检查支付状态
const startPolling = () => {
  pollingTimer = setInterval(async () => {
    try {
      const res = await request.get(`/order/${orderId.value}`)
      if (res.status === 1) {
        ElMessage.success('支付成功')
        clearInterval(pollingTimer)
        router.push('/orders')
      }
    } catch (error) {
      console.error('轮询检查支付状态失败:', error)
    }
  }, 3000) // 每3秒检查一次
}

// 返回订单页面
const goBack = () => {
  router.push('/orders')
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 页面加载时获取支付信息
onMounted(() => {
  fetchPaymentInfo()
  // 开始轮询检查支付状态
  startPolling()
})

// 页面卸载时清除定时器
onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
  }
})
</script>

<style lang="scss" scoped>
.payment-container {
  min-height: 100vh;
  background: #f3f4f6;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;

  // 面包屑导航
  .breadcrumb-nav {
    background: white;
    border-bottom: 1px solid #e5e7eb;

    .breadcrumb-content {
      max-width: 1280px;
      margin: 0 auto;
      padding: 16px 24px;
      font-size: 14px;

      .breadcrumb-item {
        color: #6b7280;
        cursor: pointer;
        transition: color 0.2s;

        &:hover {
          color: #2563eb;
        }

        &.active {
          color: #111827;
          font-weight: 600;
          cursor: default;
        }

        i {
          margin-right: 4px;
        }
      }

      .breadcrumb-separator {
        font-size: 10px;
        color: #9ca3af;
        margin: 0 8px;
      }
    }
  }

  // 容器
  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 32px 24px;
  }

  // 加载状态
  .loading-section {
    padding: 40px 0;
  }

  // 支付卡片
  .payment-card {
    background: white;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

    // 支付头部
    .payment-header {
      padding: 24px 32px;
      border-bottom: 1px solid #e5e7eb;

      .payment-title {
        font-size: 24px;
        font-weight: 700;
        color: #111827;
        margin: 0;
        display: flex;
        align-items: center;
        gap: 12px;

        i {
          color: #07c160;
        }
      }
    }

    // 支付内容
    .payment-body {
      padding: 32px;

      // 订单信息
      .order-info {
        margin-bottom: 32px;

        .order-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #f3f4f6;

          &:last-child {
            border-bottom: none;
          }

          &.total-item {
            margin-top: 8px;
            padding-top: 16px;
            border-top: 2px solid #e5e7eb;

            .order-label {
              font-size: 16px;
              font-weight: 600;
            }

            .amount {
              font-size: 24px;
              font-weight: 700;
              color: #e53e3e;
            }
          }

          .order-label {
            font-size: 14px;
            color: #6b7280;
          }

          .order-value {
            font-size: 14px;
            color: #111827;
            font-weight: 500;
          }
        }
      }

      // 二维码区域
      .qrcode-section {
        text-align: center;
        margin-bottom: 32px;

        // 二维码提示
        .qrcode-tip {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 12px;
          padding: 16px;
          background: #eff6ff;
          border-radius: 12px;
          margin-bottom: 24px;

          .tip-icon {
            color: #2563eb;
            font-size: 20px;
          }

          span {
            color: #4b5563;
            font-size: 14px;
          }
        }

        // 二维码容器
        .qrcode-container {
          display: flex;
          justify-content: center;
          padding: 24px;
          background: #f8fafc;
          border-radius: 12px;
          margin-bottom: 16px;

          .qrcode-image {
            width: 200px;
            height: 200px;
          }
        }

        // 二维码提示
        .qrcode-hint {
          font-size: 14px;
          color: #6b7280;
        }
      }

      // 操作按钮
      .payment-actions {
        display: flex;
        gap: 16px;
        justify-content: flex-end;

        .el-button {
          padding: 12px 24px;
          font-size: 16px;
        }
      }
    }
  }

  // 响应式样式
  @media (max-width: 768px) {
    .container {
      padding: 20px 16px;
    }

    .breadcrumb-content {
      padding: 12px 16px;
      font-size: 12px;
    }

    .payment-card {
      border-radius: 12px;

      .payment-header {
        padding: 16px 24px;

        .payment-title {
          font-size: 20px;
        }
      }

      .payment-body {
        padding: 24px;

        .order-info {
          margin-bottom: 24px;
        }

        .qrcode-section {
          margin-bottom: 24px;

          .qrcode-container {
            padding: 20px;

            .qrcode-image {
              width: 180px;
              height: 180px;
            }
          }
        }

        .payment-actions {
          flex-direction: column;

          .el-button {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>