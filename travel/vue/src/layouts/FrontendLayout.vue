<template>
  <div class="frontend-layout">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-content">
          <!-- Logo区域 -->
          <div class="navbar-left">
            <router-link to="/" class="navbar-logo">
              <i class="fas fa-plane-departure"></i>
              <span>旅游推荐系统</span>
            </router-link>

            <!-- 导航菜单 -->
            <div class="navbar-menu">
              <router-link to="/" class="nav-link" active-class="active">首页</router-link>
              <router-link to="/scenic" class="nav-link" active-class="active">景点</router-link>
              <router-link to="/tickets" class="nav-link" active-class="active">门票</router-link>
              <router-link to="/accommodation" class="nav-link" active-class="active">住宿</router-link>
              <router-link to="/guide" class="nav-link" active-class="active">攻略</router-link>
            </div>
          </div>

          <!-- 用户操作区域 -->
          <div class="navbar-right">
            <!-- 已登录：显示用户下拉菜单 -->
            <template v-if="isLoggedIn">
              <el-dropdown trigger="click" @command="handleCommand">
                <div class="user-avatar-btn">
                  <!-- 显示真实头像或默认图标 -->
                  <img
                      v-if="userStore.userInfo?.avatar"
                      :src="getImageUrl(userStore.userInfo.avatar)"
                      alt="用户头像"
                      class="user-avatar-img"
                      @error="handleImageError"
                  />
                  <i v-else class="fas fa-user-circle"></i>
                  <span class="username">{{ userStore.userInfo?.username || '用户' }}</span>
                </div>

                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile" icon="User">个人中心</el-dropdown-item>
                    <el-dropdown-item command="guide" icon="Notebook">我的攻略</el-dropdown-item>
                    <el-dropdown-item command="collection" icon="Star">我的收藏</el-dropdown-item>
                    <el-dropdown-item command="orders" icon="Ticket">我的订单</el-dropdown-item>
                    <el-dropdown-item divided command="logout" icon="SwitchButton">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>

            <!-- 未登录：显示登录/注册按钮 -->
            <template v-else>
              <button @click="goToLogin" class="btn-outline">登录</button>
              <button @click="goToRegister" class="btn-primary">注册</button>
            </template>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <router-view />
    </main>
    <!-- 底部,仅保留版权信息   -->
    <footer class="footer" style="text-align: center;">
      <div class="footer-content">
        <p>半夜撕代码 © 2025 旅游推荐系统. 保留所有权利.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
// import { computed } from 'vue'
// import { useUserStore } from '@/store/user'
// import { useRouter } from 'vue-router'
//
// const userStore = useUserStore()
// const router = useRouter()
//
// const isLoggedIn = computed(() => !!userStore.token)
//
// // 获取后端API基础地址
// const baseAPI = process.env.VUE_APP_BASE_API || '/api'
//
// // 获取图片完整URL
// const getImageUrl = (url) => {
//   if (!url) return ''
//   return url.startsWith('http') ? url : baseAPI + url
// }
//
// const handleCommand = (command) => {
//   switch (command) {
//     case 'profile':
//       router.push('/profile')
//       break
//     case 'guide':
//       router.push('/my-guide')
//       break
//     case 'collection':
//       router.push('/collection')
//       break
//     case 'orders':
//       router.push('/orders')
//       break
//     case 'logout':
//       handleLogout()
//       break
//   }
// }

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const handleLogout = () => {
  userStore.clearUserInfo()
  router.push('/login')
}

// 图片加载失败处理
const handleImageError = (e) => {
  // 图片加载失败时隐藏img标签，显示默认图标
  e.target.style.display = 'none'
}
</script>

<style lang="scss" scoped>
.frontend-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f9fafb;
}

// 导航栏样式 (转换自Tailwind: bg-white shadow-md sticky top-0 z-50)
.navbar {
  background-color: #ffffff;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 50;
}

// 容器 (转换自Tailwind: max-w-7xl mx-auto px-6 py-4)
.navbar-container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0.5rem 1.5rem;
}

// 内容区域 (转换自Tailwind: flex justify-between items-center)
.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

// 左侧区域 (转换自Tailwind: flex items-center space-x-8)
.navbar-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

// Logo样式 (转换自Tailwind: text-2xl font-bold text-blue-600)
.navbar-logo {
  display: flex;
  align-items: center;
  font-size: 1.5rem;
  font-weight: 700;
  color: #2563eb;
  text-decoration: none;
  transition: color 0.3s;

  i {
    margin-right: 0.5rem;
  }

  &:hover {
    color: #1d4ed8;
  }
}

// 导航菜单 (转换自Tailwind: hidden md:flex space-x-6)
.navbar-menu {
  display: none;
  gap: 1.5rem;

  @media (min-width: 768px) {
    display: flex;
  }
}

// 导航链接样式 (转换自Tailwind: nav-link)
.nav-link {
  color: #4b5563;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  transition: all 0.3s;
  font-weight: 500;

  &:hover {
    color: #2563eb;
    background-color: #eff6ff;
  }

  &.active {
    color: #2563eb;
    background-color: #dbeafe;
  }
}

// 右侧用户操作区域 (转换自Tailwind: flex items-center space-x-4)
.navbar-right {
  display: flex;
  align-items: center;
  gap: 1rem;
}

// 用户头像按钮 (转换自Tailwind: text-gray-600 hover:text-blue-600)
.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #4b5563;
  cursor: pointer;
  transition: color 0.3s;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;

  i {
    font-size: 1.5rem;
  }

  // 用户头像图片样式
  .user-avatar-img {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #e5e7eb;
    transition: border-color 0.3s;
  }

  .username {
    font-weight: 500;
    font-size: 0.875rem;
  }

  &:hover {
    color: #2563eb;
    background-color: #eff6ff;

    .user-avatar-img {
      border-color: #2563eb;
    }
  }
}

// 主要按钮样式 (转换自Tailwind: btn-primary)
.btn-primary {
  background-color: #2563eb;
  color: #ffffff;
  padding: 0.5rem 1.5rem;
  border-radius: 0.375rem;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background-color: #1d4ed8;
    transform: translateY(-1px);
    box-shadow: 0 4px 6px rgba(37, 99, 235, 0.3);
  }
}

// 次要按钮样式（描边按钮）
.btn-outline {
  background-color: transparent;
  color: #2563eb;
  padding: 0.5rem 1.5rem;
  border-radius: 0.375rem;
  border: 2px solid #2563eb;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background-color: #2563eb;
    color: #ffffff;
    transform: translateY(-1px);
    box-shadow: 0 4px 6px rgba(37, 99, 235, 0.3);
  }
}

// 主内容区域
.main-content {
  flex: 1;
  width: 100%;
  padding: 0;
}

// 响应式样式
@media (max-width: 768px) {
  .navbar-container {
    padding: 1rem;
  }

  .navbar-content {
    flex-direction: column;
    gap: 1rem;
  }

  .navbar-left {
    flex-direction: column;
    width: 100%;
    gap: 1rem;
  }

  .navbar-menu {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    width: 100%;
  }

  .navbar-right {
    width: 100%;
    justify-content: center;
  }
}
</style>