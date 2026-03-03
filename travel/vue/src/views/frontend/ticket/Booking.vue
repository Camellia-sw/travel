<template>
  <div class="booking-container">
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
        <span class="breadcrumb-item active">
          {{ ticket?.ticketName || '门票详情' }}
        </span>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-section">
      <div class="container">
        <el-skeleton :rows="20" animated />
      </div>
    </div>

    <!-- 预订内容区域 -->
    <div v-else class="booking-content">
      <div class="container">
        <div class="booking-grid">
          <!-- 左侧预订区域 -->
          <div class="left-section">
            <!-- 景点信息卡片 -->
            <div class="section-card scenic-info" v-if="ticket">
              <div class="scenic-info-wrapper">
                <img
                    :src="ticket.coverImage || 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=200&h=150&fit=crop'"
                    alt="景点图片"
                    class="scenic-image"
                />
                <div class="scenic-details">
                  <h1 class="scenic-title">{{ ticket.ticketName }}</h1>
                  <p class="scenic-location">
                    <i class="fa fa-map-marker-alt"></i>
                    {{ ticket.scenicLocation || '景点位置' }}
                  </p>
                  <div class="scenic-meta">
                    <span class="rating">
                      <i class="fa fa-star"></i>
                      <i class="fa fa-star"></i>
                      <i class="fa fa-star"></i>
                      <i class="fa fa-star"></i>
                      <i class="fa fa-star-half-alt"></i>
                      <span class="rating-text">4.8分</span>
                    </span>
                    <span class="sales-count">
                      <i class="fa fa-users"></i>
                      已售 {{ ticket.salesCount || '3.2k' }}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 选择门票类型 -->
            <div class="section-card ticket-type-section">
              <h2 class="section-title">
                <i class="fa fa-ticket-alt"></i>
                选择门票类型
              </h2>
              <div class="ticket-type-list">
                <label class="ticket-type-option selected">
                  <div class="option-left">
                    <input type="radio" name="ticket-type" checked class="ticket-radio" />
                    <div class="option-info">
                      <div class="option-name">{{ ticket?.ticketType || '成人票' }}</div>
                      <div class="option-desc">{{ ticket?.description || '适用于18-60周岁成人' }}</div>
                    </div>
                  </div>
                  <div class="option-right">
                    <div class="original-price" v-if="ticket?.discountPrice">¥{{ ticket.price }}</div>
                    <div class="current-price">¥{{ ticket?.discountPrice || ticket?.price }}</div>
                  </div>
                </label>
              </div>
            </div>

            <!-- 游玩日期和数量 -->
            <div class="section-card date-quantity-section">
              <h2 class="section-title">
                <i class="fa fa-calendar-alt"></i>
                选择日期和数量
              </h2>
              <el-form
                  :model="bookingForm"
                  :rules="rules"
                  ref="bookingFormRef"
                  class="booking-form"
              >
                <div class="form-grid">
                  <div class="form-group">
                    <label class="form-label">游玩日期</label>
                    <el-form-item prop="visitDate">
                      <el-date-picker
                          v-model="bookingForm.visitDate"
                          type="date"
                          placeholder="选择游玩日期"
                          :disabled-date="disabledDate"
                          class="form-input"
                      />
                    </el-form-item>
                    <p class="form-tip">
                      <i class="fa fa-info-circle"></i>
                      请至少提前1天预订
                    </p>
                  </div>
                  <div class="form-group">
                    <label class="form-label">购买数量</label>
                    <el-form-item prop="quantity">
                      <div class="quantity-control">
                        <button
                            type="button"
                            class="quantity-btn"
                            @click="bookingForm.quantity = Math.max(1, bookingForm.quantity - 1); calculateTotal()"
                        >
                          <i class="fa fa-minus"></i>
                        </button>
                        <input
                            type="number"
                            v-model.number="bookingForm.quantity"
                            @change="calculateTotal"
                            min="1"
                            :max="maxQuantity"
                            class="quantity-input"
                        />
                        <button
                            type="button"
                            class="quantity-btn"
                            @click="bookingForm.quantity = Math.min(maxQuantity, bookingForm.quantity + 1); calculateTotal()"
                        >
                          <i class="fa fa-plus"></i>
                        </button>
                      </div>
                    </el-form-item>
                    <p class="form-tip">
                      <i class="fa fa-info-circle"></i>
                      单次最多购买{{ maxQuantity }}张
                    </p>
                  </div>
                </div>
              </el-form>
            </div>

            <!-- 联系人信息 -->
            <div class="section-card contact-section">
              <h2 class="section-title">
                <i class="fa fa-user"></i>
                联系人信息
              </h2>
              <div class="contact-form-grid">
                <div class="form-group">
                  <label class="form-label">姓名 *</label>
                  <el-form-item prop="visitorName">
                    <el-input
                        v-model="bookingForm.visitorName"
                        placeholder="请输入真实姓名"
                        class="form-input"
                    />
                  </el-form-item>
                </div>
                <div class="form-group">
                  <label class="form-label">手机号 *</label>
                  <el-form-item prop="visitorPhone">
                    <el-input
                        v-model="bookingForm.visitorPhone"
                        placeholder="请输入手机号"
                        class="form-input"
                    />
                  </el-form-item>
                </div>
                <div class="form-group full-width">
                  <label class="form-label">身份证号 *</label>
                  <el-form-item prop="idCard">
                    <el-input
                        v-model="bookingForm.idCard"
                        placeholder="请输入身份证号"
                        class="form-input"
                    />
                  </el-form-item>
                  <p class="form-tip">
                    <i class="fa fa-lock"></i>
                    您的信息将被加密保护
                  </p>
                </div>
              </div>
            </div>

            <!-- 预订须知 -->
            <div class="section-card notice-section">
              <h2 class="section-title">
                <i class="fa fa-exclamation-circle"></i>
                预订须知
              </h2>
              <div class="notice-list">
                <div class="notice-item">
                  <i class="fa fa-check-circle"></i>
                  <div class="notice-content">
                    <div class="notice-title">入园时间</div>
                    <div class="notice-desc">请按照景区规定入园时间入园</div>
                  </div>
                </div>
                <div class="notice-item">
                  <i class="fa fa-check-circle"></i>
                  <div class="notice-content">
                    <div class="notice-title">取票方式</div>
                    <div class="notice-desc">预订身份证到景区售票处换票入园</div>
                  </div>
                </div>
                <div class="notice-item">
                  <i class="fa fa-check-circle"></i>
                  <div class="notice-content">
                    <div class="notice-title">退改政策</div>
                    <div class="notice-desc">票务一旦售出，概不退改，请谨慎下单</div>
                  </div>
                </div>
                <div class="notice-item">
                  <i class="fa fa-check-circle"></i>
                  <div class="notice-content">
                    <div class="notice-title">优惠政策</div>
                    <div class="notice-desc">1.2米以下儿童免票,需成人陪同</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧订单摘要 -->
          <div class="right-section">
            <div class="order-summary sticky">
              <h3 class="summary-title">订单摘要</h3>

              <div class="summary-items">
                <div class="summary-item">
                  <span class="item-label">门票类型</span>
                  <span class="item-value">{{ ticket?.ticketType || '成人票' }}</span>
                </div>
                <div class="summary-item">
                  <span class="item-label">单价</span>
                  <span class="item-value">¥{{ ticket?.discountPrice || ticket?.price }}</span>
                </div>
                <div class="summary-item">
                  <span class="item-label">数量</span>
                  <span class="item-value">× {{ bookingForm.quantity }}</span>
                </div>
                <div class="summary-item">
                  <span class="item-label">游玩日期</span>
                  <span class="item-value">{{ bookingForm.visitDate ? formatDate(bookingForm.visitDate) : '请选择' }}</span>
                </div>
              </div>

              <div class="summary-total">
                <div class="total-row">
                  <span class="total-label">合计</span>
                  <div class="total-price">
                    <div class="original-total" v-if="ticket?.discountPrice">¥{{ (ticket.price * bookingForm.quantity).toFixed(2) }}</div>
                    <div class="current-total">¥{{ totalAmount }}</div>
                  </div>
                </div>
                <div class="discount-info" v-if="ticket?.discountPrice">
                  <i class="fa fa-tag"></i>
                  已优惠 ¥{{ ((ticket.price - ticket.discountPrice) * bookingForm.quantity).toFixed(2) }}
                </div>
              </div>

              <button class="pay-button" @click="submitBooking" :disabled="loading">
                立即支付
              </button>

              <div class="payment-support">
                <i class="fa fa-shield-alt"></i>
                支持支付宝、微信支付
              </div>



              <!-- 安全保障 -->
              <div class="security-section">
                <h4 class="security-title">安全保障</h4>
                <div class="security-list">
                  <div class="security-item">
                    <i class="fa fa-check-circle"></i>
                    <span>官方授权,正品保证</span>
                  </div>
                  <div class="security-item">
                    <i class="fa fa-check-circle"></i>
                    <span>信息加密,隐私保护</span>
                  </div>
                  <div class="security-item">
                    <i class="fa fa-check-circle"></i>
                    <span>7×24小时客服支持</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付对话框 -->
    <el-dialog
        title="订单支付"
        v-model="payDialogVisible"
        width="500px"
        :close-on-click-modal="false"

        class="payment-dialog"
    >
      <div class="pay-dialog-content">
        <div class="order-info">
          <div class="order-header">
            <el-icon><Ticket /></el-icon>
            <span>订单详情</span>
          </div>

          <div class="order-details">
            <div class="order-item">
              <span class="order-label">订单号:</span>
              <span class="order-value">{{ createdOrder.orderNo }}</span>
            </div>
            <div class="order-item">
              <span class="order-label">门票名称:</span>
              <span class="order-value">{{ ticket?.ticketName }}</span>
            </div>
            <div class="order-item">
              <span class="order-label">游玩日期:</span>
              <span class="order-value">{{ formatDate(createdOrder.visitDate) }}</span>
            </div>
            <div class="order-item">
              <span class="order-label">数量:</span>
              <span class="order-value">{{ createdOrder.quantity }} 张</span>
            </div>
            <div class="order-item total-item">
              <span class="order-label">总金额:</span>
              <span class="order-value amount">¥{{ createdOrder.totalAmount }}</span>
            </div>
          </div>
        </div>

        <div class="payment-methods">
          <div class="payment-header">
            <el-icon><Wallet /></el-icon>
            <span>支付方式</span>
          </div>

          <el-radio-group v-model="paymentMethod" class="payment-options">
            <el-radio label="WECHAT">
              <div class="payment-option">
                <el-icon class="payment-icon wechat-icon"><Money /></el-icon>
                <span>微信支付</span>
              </div>
            </el-radio>
            <el-radio label="ALIPAY">
              <div class="payment-option">
                <el-icon class="payment-icon alipay-icon"><Money /></el-icon>
                <span>支付宝</span>
              </div>
            </el-radio>
            <el-radio label="BANK_CARD">
              <div class="payment-option">
                <el-icon class="payment-icon bank-icon"><CreditCard /></el-icon>
                <span>银行卡</span>
              </div>
            </el-radio>
          </el-radio-group>
        </div>

        <div class="payment-info" v-if="paymentMethod">
          <template v-if="paymentMethod === 'ALIPAY'">
            <div class="payment-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>点击下方"去支付"按钮，跳转到支付宝支付页面</span>
            </div>
          </template>
          <template v-else>
            <div class="qrcode-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>请扫描二维码完成支付</span>
            </div>
            <div class="qrcode-image">
              <el-image
                  style="width: 200px; height: 200px"
                  :src="qrcodePlaceholder"
                  fit="cover"
              ></el-image>
            </div>
          </template>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelOrder" :icon="Close">取消订单</el-button>
          <template v-if="paymentMethod === 'ALIPAY'">
            <el-button type="primary" @click="goToAlipay" :icon="Right">去支付</el-button>
          </template>
          <template v-else>
            <el-button type="primary" @click="confirmPayment" :icon="Check">确认已支付</el-button>
          </template>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import {
  Ticket,
  Calendar,
  Goods,
  Edit,
  User,
  Phone,
  CreditCard,
  Back,
  Check,
  Close,
  Right,
  Wallet,
  Money,
  InfoFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 门票ID
const ticketId = computed(() => route.params.id)

// 门票信息
const ticket = ref(null)
const loading = ref(false)

// 表单引用
const bookingFormRef = ref(null)

// 支付对话框
const payDialogVisible = ref(false)
const paymentMethod = ref('')
const createdOrder = ref({})

// 预订表单数据
const bookingForm = reactive({
  ticketId: null,
  visitDate: null,
  quantity: 1,
  visitorName: '',
  visitorPhone: '',
  idCard: ''
})

// 表单验证规则
const rules = {
  visitDate: [
    { required: true, message: '请选择游玩日期', trigger: 'change' }
  ],
  quantity: [
    { required: true, message: '请选择购买数量', trigger: 'change' },
    { type: 'number', min: 1, message: '购买数量至少为1', trigger: 'change' }
  ],
  visitorName: [
    { required: true, message: '请输入游客姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应在2-20个字符之间', trigger: 'blur' }
  ],
  visitorPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ]
}

// 计算属性：最大购买数量
const maxQuantity = computed(() => {
  return ticket.value ? ticket.value.stock : 1
})

// 计算属性：总金额
const totalAmount = ref(0)

// 计算总金额
const calculateTotal = () => {
  if (!ticket.value) return 0

  const price = ticket.value.discountPrice || ticket.value.price
  totalAmount.value = (price * bookingForm.quantity).toFixed(2)
}

// 获取门票详情
const fetchTicketDetail = async () => {
  loading.value = true
  try {
    await request.get(`/ticket/${ticketId.value}`, {}, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        ticket.value = res
        bookingForm.ticketId = res.id

        // 默认选择当前日期
        if (!bookingForm.visitDate) {
          bookingForm.visitDate = new Date()
        }

        // 如果已登录，预填用户信息
        if (userStore.isLoggedIn && userStore.userInfo) {
          bookingForm.visitorName = userStore.userInfo.name || userStore.userInfo.nickname || ''
          bookingForm.visitorPhone = userStore.userInfo.phone || ''
        }

        calculateTotal()
      }
    })
  } catch (error) {
    ElMessage.error('获取门票信息失败')
    console.error('获取门票详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 禁用的日期（今天之前的日期不可选）
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7 // 不能选择今天之前的日期
}

// 提交预订
const submitBooking = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login?redirect=' + encodeURIComponent(route.fullPath))
    return
  }

  bookingFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await request.post('/order', bookingForm, {
          successMsg: '订单创建成功',
          onSuccess: (res) => {
            createdOrder.value = res
            payDialogVisible.value = true
          }
        })
      } catch (error) {
        console.error('创建订单失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}

// 确认支付
const confirmPayment = async () => {
  if (!paymentMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  loading.value = true
  try {
    await request.post(`/order/${createdOrder.value.id}/pay`, null, {
      params: {
        paymentMethod: paymentMethod.value
      },
      successMsg: '支付成功',
      onSuccess: () => {
        payDialogVisible.value = false
        router.push('/orders')
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
  if (createdOrder.value && createdOrder.value.id) {
    router.push(`/payment/alipay/${createdOrder.value.id}`)
  } else {
    ElMessage.error('订单信息错误')
  }
}

// 取消订单
const cancelOrder = async () => {
  loading.value = true
  try {
    await request.post(`/order/${createdOrder.value.id}/cancel`, {}, {
      successMsg: '订单已取消',
      onSuccess: () => {
        payDialogVisible.value = false
        router.push('/tickets')
      }
    })
  } catch (error) {
    console.error('取消订单失败:', error)
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 二维码占位图（模拟支付二维码）
const qrcodePlaceholder = computed(() => {
  // 生成一个简单的二维码占位图URL
  return 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB4PSI1MCIgeT0iNTAiIHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiBzdHlsZT0iZmlsbDojZjBmMGYwO3N0cm9rZS13aWR0aDo0O3N0cm9rZTojZGRkIiAvPjxyZWN0IHg9IjcwIiB5PSI3MCIgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBzdHlsZT0iZmlsbDojZjhmOGY4O3N0cm9rZS13aWR0aDoyO3N0cm9rZTojY2NjIiAvPjx0ZXh0IHg9IjEwMCIgeT0iMTI3IiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBzdHlsZT0iZm9udC1mYW1pbHk6QXJpYWwsIHNhbnMtc2VyaWY7Zm9udC1zaXplOjEycHg7ZmlsbDojOTk5OTk5OyI+5pSv5LuY5LqM57u056CBPC90ZXh0Pjwvc3ZnPg=='
})

// 页面加载时获取门票详情
onMounted(() => {
  fetchTicketDetail()
})
</script>

<style lang="scss" scoped>
.booking-container {
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
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 24px;
  }

  // 加载状态
  .loading-section {
    padding: 40px 0;
  }

  // 预订内容
  .booking-content {
    .booking-grid {
      display: grid;
      grid-template-columns: 2fr 1fr;
      gap: 32px;
      align-items: start;
    }
  }

  // 左侧区域
  .left-section {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  // 卡片通用样式
  .section-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

    .section-title {
      font-size: 20px;
      font-weight: 700;
      color: #111827;
      margin: 0 0 24px;
      display: flex;
      align-items: center;
      gap: 12px;

      i {
        color: #2563eb;
        font-size: 18px;
      }
    }
  }

  // 景点信息卡片
  .scenic-info {
    .scenic-info-wrapper {
      display: flex;
      gap: 16px;

      .scenic-image {
        width: 192px;
        height: 144px;
        border-radius: 12px;
        object-fit: cover;
        flex-shrink: 0;
      }

      .scenic-details {
        flex: 1;

        .scenic-title {
          font-size: 24px;
          font-weight: 700;
          color: #111827;
          margin: 0 0 12px;
        }

        .scenic-location {
          color: #6b7280;
          margin: 0 0 12px;
          font-size: 14px;

          i {
            color: #2563eb;
            margin-right: 8px;
          }
        }

        .scenic-meta {
          display: flex;
          align-items: center;
          gap: 16px;
          font-size: 14px;

          .rating {
            color: #f59e0b;

            i {
              margin-right: 2px;
            }

            .rating-text {
              color: #6b7280;
              margin-left: 4px;
            }
          }

          .sales-count {
            color: #6b7280;

            i {
              margin-right: 4px;
            }
          }
        }
      }
    }
  }

  // 门票类型选择
  .ticket-type-section {
    .ticket-type-list {
      .ticket-type-option {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          border-color: #dbeafe;
        }

        &.selected {
          border-color: #2563eb;
          background: #eff6ff;
        }

        .option-left {
          display: flex;
          align-items: center;
          gap: 16px;

          .ticket-radio {
            width: 20px;
            height: 20px;
            accent-color: #2563eb;
          }

          .option-info {
            .option-name {
              font-weight: 600;
              color: #111827;
              margin-bottom: 4px;
            }

            .option-desc {
              font-size: 14px;
              color: #6b7280;
            }
          }
        }

        .option-right {
          text-align: right;

          .original-price {
            font-size: 14px;
            color: #9ca3af;
            text-decoration: line-through;
            margin-bottom: 4px;
          }

          .current-price {
            font-size: 24px;
            font-weight: 700;
            color: #2563eb;
          }
        }
      }
    }
  }

  // 日期和数量选择
  .date-quantity-section {
    .booking-form {
      :deep(.el-form-item) {
        margin-bottom: 0;
      }
    }

    .form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 24px;

      .form-group {
        .form-label {
          display: block;
          font-size: 14px;
          font-weight: 600;
          color: #111827;
          margin-bottom: 8px;
        }

        .form-input {
          width: 100%;

          :deep(.el-input__wrapper) {
            border: 2px solid #e5e7eb;
            border-radius: 12px;
            padding: 12px 16px;
            transition: all 0.3s;

            &:hover {
              border-color: #2563eb;
            }

            &.is-focus {
              border-color: #2563eb;
              box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
            }
          }
        }

        .quantity-control {
          display: flex;
          align-items: center;
          gap: 16px;

          .quantity-btn {
            width: 48px;
            height: 48px;
            border: 2px solid #d1d5db;
            border-radius: 12px;
            background: white;
            cursor: pointer;
            transition: all 0.3s;
            display: flex;
            align-items: center;
            justify-content: center;

            &:hover {
              border-color: #2563eb;
              color: #2563eb;
            }
          }

          .quantity-input {
            width: 80px;
            height: 48px;
            text-align: center;
            font-size: 20px;
            font-weight: 700;
            border: 2px solid #e5e7eb;
            border-radius: 12px;
            outline: none;
            transition: all 0.3s;

            &:focus {
              border-color: #2563eb;
              box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
            }
          }
        }

        .form-tip {
          font-size: 12px;
          color: #9ca3af;
          margin-top: 8px;

          i {
            margin-right: 4px;
          }
        }
      }
    }
  }

  // 联系人信息
  .contact-section {
    .contact-form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 24px;

      .form-group {
        &.full-width {
          grid-column: 1 / -1;
        }

        .form-label {
          display: block;
          font-size: 14px;
          font-weight: 600;
          color: #111827;
          margin-bottom: 8px;
        }

        .form-input {
          :deep(.el-input__wrapper) {
            border: 2px solid #e5e7eb;
            border-radius: 12px;
            padding: 12px 16px;
            transition: all 0.3s;

            &:hover {
              border-color: #ea580c;
            }

            &.is-focus {
              border-color: #ea580c;
              box-shadow: 0 0 0 3px rgba(234, 88, 12, 0.1);
            }
          }
        }

        .form-tip {
          font-size: 12px;
          color: #9ca3af;
          margin-top: 8px;

          i {
            margin-right: 4px;
          }
        }
      }
    }
  }

  // 预订须知
  .notice-section {
    .notice-list {
      .notice-item {
        display: flex;
        align-items: flex-start;
        gap: 12px;
        margin-bottom: 16px;

        &:last-child {
          margin-bottom: 0;
        }

        i {
          color: #10b981;
          font-size: 16px;
          margin-top: 2px;
          flex-shrink: 0;
        }

        .notice-content {
          flex: 1;

          .notice-title {
            font-weight: 600;
            color: #111827;
            margin-bottom: 4px;
          }

          .notice-desc {
            font-size: 14px;
            color: #6b7280;
            line-height: 1.5;
          }
        }
      }
    }
  }

  // 右侧订单摘要
  .right-section {
    .order-summary {
      background: white;
      border-radius: 16px;
      padding: 24px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

      &.sticky {
        position: sticky;
        top: 96px;
      }

      .summary-title {
        font-size: 20px;
        font-weight: 700;
        color: #111827;
        margin: 0 0 24px;
      }

      .summary-items {
        margin-bottom: 24px;

        .summary-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 0;
          color: #4b5563;

          .item-label {
            font-size: 14px;
          }

          .item-value {
            font-weight: 600;
            color: #111827;
          }
        }
      }

      .summary-total {
        border-top: 1px solid #e5e7eb;
        padding-top: 16px;
        margin-bottom: 24px;

        .total-row {
          display: flex;
          justify-content: space-between;
          align-items: center;

          .total-label {
            font-size: 16px;
            color: #4b5563;
          }

          .total-price {
            text-align: right;

            .original-total {
              font-size: 14px;
              color: #9ca3af;
              text-decoration: line-through;
              margin-bottom: 4px;
            }

            .current-total {
              font-size: 32px;
              font-weight: 700;
              color: #2563eb;
            }
          }
        }

        .discount-info {
          text-align: right;
          font-size: 14px;
          color: #10b981;
          margin-top: 8px;

          i {
            margin-right: 4px;
          }
        }
      }

      .pay-button {
        width: 100%;
        padding: 16px;
        background: linear-gradient(to right, #3b82f6, #2563eb);
        color: white;
        border: none;
        border-radius: 12px;
        font-size: 18px;
        font-weight: 700;
        cursor: pointer;
        transition: all 0.3s;
        box-shadow: 0 4px 15px rgba(37, 99, 235, 0.4);

        &:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(37, 99, 235, 0.6);
        }

        &:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }
      }

      .payment-support {
        text-align: center;
        font-size: 14px;
        color: #6b7280;
        margin-top: 16px;

        i {
          color: #10b981;
          margin-right: 4px;
        }
      }





      .security-section {
        margin-top: 24px;
        padding-top: 24px;
        border-top: 1px solid #e5e7eb;

        .security-title {
          font-size: 16px;
          font-weight: 600;
          color: #111827;
          margin: 0 0 12px;
        }

        .security-list {
          .security-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #6b7280;
            margin-bottom: 8px;

            &:last-child {
              margin-bottom: 0;
            }

            i {
              color: #10b981;
            }
          }
        }
      }
    }
  }

  // 支付对话框样式
  :deep(.payment-dialog) {
    .el-dialog__header {

      color: rgb(13, 0, 0);
      padding: 20px 24px;
      margin: 0;

      .el-dialog__title {
        color: rgb(13, 0, 0);
        font-weight: 600;
      }

      .el-dialog__headerbtn {
        .el-dialog__close {
          color: rgb(13, 0, 0);
          font-size: 18px;

          &:hover {
            color: rgba(255, 255, 255, 0.8);
          }
        }
      }
    }

    .el-dialog__body {
      padding: 24px;
    }

    .el-dialog__footer {
      padding: 16px 24px 24px;
      border-top: 1px solid #f1f5f9;
    }
  }

  .pay-dialog-content {
    .order-info {
      margin-bottom: 24px;

      .order-header {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #2d3748;
        margin-bottom: 16px;

        .el-icon {
          color: #667eea;
        }
      }

      .order-details {
        background: #f8fafc;
        border-radius: 8px;
        padding: 16px;

        .order-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #e2e8f0;

          &:last-child {
            border-bottom: none;
          }

          &.total-item {
            margin-top: 8px;
            padding-top: 16px;
            border-top: 2px solid #e2e8f0;
            border-bottom: none;

            .order-label {
              font-size: 16px;
              font-weight: 600;
            }

            .amount {
              font-size: 20px;
              font-weight: 700;
              color: #e53e3e;
            }
          }

          .order-label {
            font-size: 14px;
            color: #64748b;
          }

          .order-value {
            font-size: 14px;
            color: #2d3748;
            font-weight: 500;
          }
        }
      }
    }

    .payment-methods {
      margin-bottom: 24px;

      .payment-header {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;
        color: #2d3748;
        margin-bottom: 16px;

        .el-icon {
          color: #667eea;
        }
      }

      .payment-options {
        display: flex;
        flex-direction: column;
        gap: 12px;

        :deep(.el-radio) {
          margin: 0;
          padding: 12px;
          border: 2px solid #e2e8f0;
          border-radius: 8px;
          transition: all 0.3s ease;

          &:hover {
            border-color: #667eea;
            background: #f8fafc;
          }

          &.is-checked {
            border-color: #667eea;
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
          }

          .el-radio__input {
            margin-right: 12px;
          }
        }

        .payment-option {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 500;

          .payment-icon {
            font-size: 18px;

            &.wechat-icon {
              color: #07c160;
            }

            &.alipay-icon {
              color: #1677ff;
            }

            &.bank-icon {
              color: #667eea;
            }
          }
        }
      }
    }

    .payment-info {
      text-align: center;

      .payment-tip,
      .qrcode-tip {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        font-size: 14px;
        color: #64748b;
        margin-bottom: 16px;

        .el-icon {
          color: #667eea;
        }
      }

      .qrcode-image {
        display: flex;
        justify-content: center;
        padding: 20px;
        background: #f8fafc;
        border-radius: 8px;
        border: 2px dashed #e2e8f0;
      }
    }
  }

  // 响应式样式
  @media (max-width: 1200px) {
    .booking-grid {
      grid-template-columns: 1fr;
      gap: 30px;
    }

    .order-summary.sticky {
      position: static;
    }
  }

  @media (max-width: 768px) {
    .container {
      padding: 20px 16px;
    }

    .breadcrumb-content {
      padding: 12px 16px;
      font-size: 12px;
    }

    .section-card {
      padding: 16px;
      border-radius: 12px;

      .section-title {
        font-size: 18px;
        margin-bottom: 16px;
      }
    }

    .scenic-info {
      .scenic-info-wrapper {
        flex-direction: column;

        .scenic-image {
          width: 100%;
          height: 200px;
        }

        .scenic-title {
          font-size: 20px;
        }
      }
    }

    .form-grid,
    .contact-form-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .quantity-control {
      justify-content: center;
    }

    .order-summary {
      .summary-title {
        font-size: 18px;
      }

      .pay-button {
        font-size: 16px;
      }
    }
  }
}
</style>