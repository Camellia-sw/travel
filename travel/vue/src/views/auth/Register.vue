<template>
  <Auth
      :formData="registerForm"
      :rules="accountRules"
      :loading="loading"
      submitText="注册"
      @submit="handleSubmit"
      ref="authFormRef"
  >
    <template #form-items>
      <el-form-item prop="username">
        <el-input
            v-model="registerForm.username"
            :prefix-icon="User"
            placeholder="用户名">
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input
            v-model="registerForm.email"
            :prefix-icon="Message"
            placeholder="邮箱地址">
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
            v-model="registerForm.password"
            :prefix-icon="Lock"
            type="password"
            placeholder="密码">
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
            v-model="registerForm.confirmPassword"
            :prefix-icon="Lock"
            type="password"
            placeholder="确认密码">
        </el-input>
      </el-form-item>
      <el-form-item prop="phone">
        <el-input
            v-model="registerForm.phone"
            :prefix-icon="Phone"
            placeholder="手机号（可选）">
        </el-input>
      </el-form-item>
    </template>

    <template #auth-links>
      已有账号？<router-link to="/login">立即登录</router-link>
    </template>
  </Auth>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import Auth from './Auth.vue'

const router = useRouter()
const authFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  role: 'USER', // 默认注册为普通用户
})

const validateAccountPass2 = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (!value) {
    callback(new Error('邮箱不能为空'))
    return
  }

  const emailRegex = /^[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)*@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  if (!emailRegex.test(value)) {
    callback(new Error('邮箱格式不正确'))
  } else {
    callback()
  }
}

const validatePhone = (rule, value, callback) => {
  if (value && !/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

// 账号密码注册的表单验证规则
const accountRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度必须在3到50个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度必须在6到100个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateAccountPass2, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { validator: validatePhone, trigger: 'blur' }
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
      const { confirmPassword, ...registerData } = registerForm
      await request.post("/user/add", registerData, {
        successMsg: "注册成功",
        showDefaultMsg: true,
        onSuccess: () => {
          router.push('/login')
        }
      })
    } catch (error) {
      console.error('注册失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
$primary-blue: #4A90E2;
</style>