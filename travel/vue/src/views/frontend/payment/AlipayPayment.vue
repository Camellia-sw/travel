<template>
  <div class="payment-container">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-content">
        <div class="logo">
          <img src="https://img.alicdn.com/tfs/TB1Zv8_lxSYBuNjSspjXXX73VXa-390-63.png" alt="支付宝" />
        </div>
        <div class="nav-links">
          <span>你好，欢迎使用支付宝付款！</span>
          <a href="#">常见问题</a>
        </div>
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
      <!-- 订单信息栏 -->
      <div class="order-bar">
        <div class="order-bar-content">
          <div class="order-info-left">
            <span class="label">正在使用即时到账交易</span>
            <span class="order-detail">
              <span class="order-name">{{ order?.ticketName || '门票订单' }}</span>
              <span class="seller">收款方：旅游推荐系统</span>
            </span>
          </div>
          <div class="order-amount">
            <span class="amount">{{ order?.totalAmount }}</span>
            <span class="currency">元</span>
          </div>
        </div>
      </div>

      <!-- 主支付区域 -->
      <div class="main-payment">
        <div class="payment-wrapper">
          <!-- 左侧：扫码支付 -->
          <div class="qr-section">
            <div class="section-title">扫码支付</div>
            <div class="qr-code">
              <div class="qr-placeholder">
                <img src="https://tfs.alipayobjects.com/images/partner/T1QttgXf4cXXXXXXXX.png" alt="支付宝二维码" />
              </div>
              <div class="qr-tip">
                <i class="fa fa-mobile"></i>
                <span>使用手机支付宝扫码完成付款</span>
              </div>
              <div class="qr-links">
                <a href="#">手机支付宝下载</a>
                <span>|</span>
                <a href="#">如何使用？</a>
              </div>
            </div>
          </div>

          <!-- 分隔线 -->
          <div class="divider"></div>

          <!-- 右侧：登录支付 -->
          <div class="login-section">
            <div class="section-header">
              <span class="section-title active">登录支付宝账户付款</span>
              <a href="#" class="register-link">新用户注册</a>
            </div>

            <div class="login-form">
              <div class="form-item">
                <label>账户名：</label>
                <div class="input-wrapper">
                  <input type="text" placeholder="手机号/邮箱" />
                  <a href="#" class="forgot-link">忘记账户名？</a>
                </div>
              </div>

              <div class="form-item">
                <label>支付密码：</label>
                <div class="input-wrapper">
                  <input type="password" placeholder="请输入账户的支付密码，不是登录密码" />
                  <a href="#" class="forgot-link">忘记密码？</a>
                </div>
              </div>

              <div class="password-tip">
                请输入账户的 <span class="highlight">支付密码</span>，不是登录密码。
              </div>

              <button class="pay-btn" @click="handleSimulatePay">下一步</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部 -->
      <div class="footer">
        <div class="footer-content">
          <a href="#" @click.prevent="goBack">返回订单列表</a>
          <span class="copyright">ICP证：浙B2-20190046</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

// 订单ID
const orderId = ref(route.params.orderId)

// 加载状态
const loading = ref(true)

// 订单信息
const order = ref({})

// 获取订单信息
const fetchPaymentInfo = async () => {
  try {
    // 获取订单信息
    await request.get(`/order/${orderId.value}`, {}, {
      showDefaultMsg: false,
      onSuccess: (res) => {
        order.value = res
      }
    })
  } catch (error) {
    ElMessage.error('获取订单信息失败')
    console.error('获取订单信息失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理模拟支付
const handleSimulatePay = async () => {
  try {
    // 调用后端接口更新订单状态
    await request.post(`/order/${orderId.value}/pay`, null, {
      params: { paymentMethod: '支付宝' },
      showDefaultMsg: false
    })
    ElMessage.success('支付成功')
    router.push('/orders')
  } catch (error) {
    ElMessage.error('支付失败：' + error.message)
  }
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
})
</script>

<style lang="scss" scoped>
.payment-container {
  min-height: 100vh;
  background: #f5f5f5;
  font-family: "Microsoft YaHei", "PingFang SC", -apple-system, BlinkMacSystemFont, sans-serif;

  // 顶部导航栏
  .top-nav {
    background: #fff;
    border-bottom: 1px solid #e0e0e0;

    .nav-content {
      max-width: 1200px;
      margin: 0 auto;
      padding: 15px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .logo {
        img {
          height: 35px;
        }
      }

      .nav-links {
        font-size: 12px;
        color: #666;

        a {
          color: #08c;
          text-decoration: none;
          margin-left: 20px;

          &:hover {
            text-decoration: underline;
          }
        }
      }
    }
  }

  // 加载状态
  .loading-section {
    padding: 40px 0;
    max-width: 990px;
    margin: 0 auto;
  }

  // 订单信息栏
  .order-bar {
    background: #e8f4fc;
    border-bottom: 1px solid #d9e8f4;
    padding: 15px 0;

    .order-bar-content {
      max-width: 990px;
      margin: 0 auto;
      padding: 0 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .order-info-left {
        display: flex;
        flex-direction: column;
        gap: 5px;

        .label {
          font-size: 12px;
          color: #666;
        }

        .order-detail {
          display: flex;
          align-items: center;
          gap: 15px;

          .order-name {
            font-size: 14px;
            color: #333;
            font-weight: 500;
          }

          .seller {
            font-size: 12px;
            color: #999;
          }
        }
      }

      .order-amount {
        display: flex;
        align-items: baseline;
        gap: 3px;

        .amount {
          font-size: 28px;
          color: #ff6600;
          font-weight: bold;
        }

        .currency {
          font-size: 14px;
          color: #ff6600;
        }
      }
    }
  }

  // 主支付区域
  .main-payment {
    max-width: 990px;
    margin: 20px auto;
    padding: 0 20px;

    .payment-wrapper {
      background: #fff;
      border: 1px solid #ddd;
      border-radius: 4px;
      display: flex;
      min-height: 400px;

      // 左侧扫码支付
      .qr-section {
        flex: 1;
        padding: 40px;
        text-align: center;

        .section-title {
          font-size: 16px;
          color: #333;
          margin-bottom: 30px;
          font-weight: normal;
        }

        .qr-code {
          .qr-placeholder {
            width: 200px;
            height: 200px;
            margin: 0 auto 20px;
            border: 1px solid #ddd;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f9f9f9;

            img {
              width: 180px;
              height: 180px;
            }
          }

          .qr-tip {
            font-size: 14px;
            color: #666;
            margin-bottom: 10px;

            i {
              color: #08c;
              margin-right: 5px;
            }
          }

          .qr-links {
            font-size: 12px;

            a {
              color: #08c;
              text-decoration: none;

              &:hover {
                text-decoration: underline;
              }
            }

            span {
              color: #ccc;
              margin: 0 8px;
            }
          }
        }
      }

      // 分隔线
      .divider {
        width: 1px;
        background: #eee;
        margin: 40px 0;
      }

      // 右侧登录支付
      .login-section {
        flex: 1;
        padding: 40px;

        .section-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 30px;
          border-bottom: 2px solid #08c;
          padding-bottom: 10px;

          .section-title {
            font-size: 16px;
            color: #333;
            font-weight: normal;

            &.active {
              color: #08c;
              font-weight: 500;
            }
          }

          .register-link {
            font-size: 12px;
            color: #08c;
            text-decoration: none;

            &:hover {
              text-decoration: underline;
            }
          }
        }

        .login-form {
          .form-item {
            margin-bottom: 20px;

            label {
              display: block;
              font-size: 12px;
              color: #666;
              margin-bottom: 8px;
            }

            .input-wrapper {
              position: relative;

              input {
                width: 100%;
                height: 36px;
                padding: 0 10px;
                border: 1px solid #ccc;
                border-radius: 3px;
                font-size: 14px;
                box-sizing: border-box;

                &:focus {
                  outline: none;
                  border-color: #08c;
                }

                &::placeholder {
                  color: #999;
                }
              }

              .forgot-link {
                position: absolute;
                right: 10px;
                top: 50%;
                transform: translateY(-50%);
                font-size: 12px;
                color: #08c;
                text-decoration: none;

                &:hover {
                  text-decoration: underline;
                }
              }
            }
          }

          .password-tip {
            font-size: 12px;
            color: #999;
            margin-bottom: 20px;

            .highlight {
              color: #ff6600;
            }
          }

          .pay-btn {
            width: 100%;
            height: 40px;
            background: #00a3ee;
            border: none;
            border-radius: 3px;
            color: #fff;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;

            &:hover {
              background: #0088cc;
            }
          }
        }
      }
    }
  }

  // 底部
  .footer {
    max-width: 990px;
    margin: 0 auto;
    padding: 20px;
    text-align: center;

    .footer-content {
      a {
        color: #666;
        text-decoration: none;
        font-size: 12px;

        &:hover {
          color: #08c;
        }
      }

      .copyright {
        display: block;
        margin-top: 10px;
        font-size: 12px;
        color: #999;
      }
    }
  }
}
</style>