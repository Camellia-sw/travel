<template>
  <Auth
      :formData="loginForm"
      :rules="accountRules"
      :loading="loading"
      submitText="登录"
      @submit="handleSubmit"
      ref="authFormRef"
  >
    <template #form-items>
      <el-form-item prop="username">
        <el-input
            v-model="loginForm.username"
            :prefix-icon="User"
            placeholder="用户名">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
            v-model="loginForm.password"
            :prefix-icon="Lock"
            type="password"
            placeholder="密码">
        </el-input>
      </el-form-item>
    </template>

    <template #auth-links>
      还没有账号？<router-link to="/register">立即注册</router-link>
    </template>
  </Auth>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Auth from './Auth.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const authFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const accountRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleSubmit = () => {
  if (!authFormRef.value || !authFormRef.value.formRef) {
    ElMessage.error('表单引用错误')
    return
  }

  const formRef = authFormRef.value.formRef

  formRef.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      // 账号密码登录
      await request.post("/user/login", loginForm, {
        successMsg: "登录成功",
        showDefaultMsg: true,
        onSuccess: handleLoginSuccess
      })
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}

const handleLoginSuccess = async (data) => {
  userStore.setUserInfo(data)

  // 根据返回的角色决定跳转路径
  if (data.role !== 'USER') {
    // 管理员登录，跳转到后台
    await router.isReady()
    router.push(route.query.redirect || '/back/manager')
  } else {
    // 普通用户登录，跳转到前台
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  }
}
</script>

<style lang="scss" scoped>
$primary-blue: #4A90E2;
</style>