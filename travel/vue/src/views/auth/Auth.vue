<template>
  <div class="auth-container">
    <div class="auth-box">
      <div class="auth-header" v-if="showHeader">
        <div class="logo"><el-icon><Place /></el-icon></div>
        <h1 class="title">旅游推荐系统</h1>
        <div class="subtitle">TRAVEL RECOMMENDATION SYSTEM</div>
      </div>

      <el-form :model="formData" :rules="rules" ref="formRef" class="auth-form">
        <slot name="form-items"></slot>

        <el-form-item class="submit-item">
          <el-button type="primary" :loading="loading" @click="handleSubmit" class="auth-button">
            {{ submitText }}
          </el-button>
        </el-form-item>

        <div class="auth-links">
          <slot name="auth-links"></slot>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Place } from '@element-plus/icons-vue'

const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  rules: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  submitText: {
    type: String,
    default: '提交'
  },
  showHeader: {
    type: Boolean,
    default: true
  }
})

const formRef = ref(null)

const emit = defineEmits(['submit'])

const handleSubmit = () => {
  formRef.value.validate(valid => {
    if (valid) {
      emit('submit', formRef)
    }
  })
}

defineExpose({
  formRef
})
</script>

<style lang="scss" scoped>
// 颜色定义
$primary-blue: #4A90E2;
$warm-orange: #FF9F43;
$natural-green: #27AE60;
$cloud-white: #F8F9FA;
$text-primary: #2C3E50;
$text-secondary: #7F8C8D;
$border-light: #E8EAED;

.auth-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/bg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.2);
    z-index: 1;
  }
}

.auth-box {
  width: 420px;
  padding: 40px;
  background-color: rgba(255, 255, 255, 0.92);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  z-index: 2;
  position: relative;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;

  .logo {
    font-size: 48px;
    color: $primary-blue;
    margin-bottom: 16px;

    .el-icon {
      font-size: 48px;
    }
  }

  .title {
    font-size: 24px;
    font-weight: 600;
    color: $text-primary;
    margin: 0 0 8px;
  }

  .subtitle {
    font-size: 13px;
    color: $text-secondary;
    letter-spacing: 0.5px;
  }
}

.auth-form {
  .el-form-item {
    margin-bottom: 16px;

    &.submit-item {
      margin-top: 24px;
      margin-bottom: 16px;
    }
  }

  :deep(.el-input__wrapper) {
    background-color: $cloud-white;
    border: 1px solid $border-light;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      border-color: #D0D7E0;
    }

    &.is-focus {
      border-color: $primary-blue;
      box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.1);
    }
  }

  :deep(.el-input__inner) {
    height: 40px;
    font-size: 14px;
    color: $text-primary;

    &::placeholder {
      color: #B0B8C1;
    }
  }

  :deep(.el-input__prefix) {
    display: flex;
    align-items: center;
    color: $primary-blue;
    font-size: 16px;
  }

  :deep(.el-tabs) {
    .el-tabs__header {
      margin-bottom: 20px;
      border-bottom: 1px solid $border-light;
    }

    .el-tabs__nav-wrap {
      &::after {
        display: none;
      }
    }

    .el-tabs__item {
      color: $text-secondary;
      font-size: 14px;
      font-weight: 500;

      &.is-active {
        color: $primary-blue;
      }

      &:hover {
        color: $primary-blue;
      }
    }

    .el-tabs__active-bar {
      background-color: $primary-blue;
      height: 2px;
    }
  }
}

.auth-button {
  width: 100%;
  height: 40px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  background-color: $primary-blue;
  border-color: $primary-blue;
  transition: all 0.3s ease;

  &:hover {
    background-color: #3A7FD8;
    border-color: #3A7FD8;
  }

  &:active {
    background-color: #2E6BC0;
    border-color: #2E6BC0;
  }
}

.auth-links {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
  color: $text-secondary;

  a {
    color: $primary-blue;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s ease;

    &:hover {
      color: #3A7FD8;
    }
  }
}

@media (max-width: 576px) {
  .auth-box {
    width: 90%;
    max-width: 360px;
    padding: 32px 24px;
  }

  .auth-header {
    margin-bottom: 24px;

    .logo {
      font-size: 40px;
      margin-bottom: 12px;
    }

    .title {
      font-size: 20px;
    }
  }
}
</style>