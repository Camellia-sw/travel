<template>
  <div class="profile-container">
    <!-- 用户信息头部 -->
    <div class="user-header">
      <div class="header-inner">
        <div class="user-info-section">
          <div class="avatar-container">
            <el-avatar :size="128" :src="avatarUrl" class="user-avatar">
              <span class="avatar-fallback">{{ userForm.username?.charAt(0) || '用' }}</span>
            </el-avatar>
            <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="true"
                :show-file-list="false"
                :http-request="customUploadAvatar"
                :before-upload="beforeAvatarUpload"
            >
              <button class="upload-btn">
                <i class="fas fa-camera"></i>
              </button>
            </el-upload>
          </div>
          <div class="user-details">
            <h1 class="user-name">{{ userForm.username || '旅行达人' }}</h1>
            <p class="user-desc">探索世界,记录美好</p>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-value">{{ userStats.guides }}</div>
                <div class="stat-label">发布攻略</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.collections }}</div>
                <div class="stat-label">收藏景点</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ userStats.orders }}</div>
                <div class="stat-label">订单数量</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="content-wrapper">
        <!-- 左侧菜单 -->
        <aside class="sidebar">
          <div class="sidebar-inner">
            <nav class="sidebar-nav">
              <a href="#"
                 :class="['nav-item', { active: activeMenu === 'profile' }]"
                 @click.prevent="activeMenu = 'profile'">
                <i class="fas fa-user"></i>
                <span>个人信息</span>
              </a>
              <a href="#"
                 :class="['nav-item', { active: activeMenu === 'settings' }]"
                 @click.prevent="activeMenu = 'settings'">
                <i class="fas fa-cog"></i>
                <span>账户设置</span>
              </a>
              <a href="#" class="nav-item logout" @click.prevent="handleLogout">
                <i class="fas fa-sign-out-alt"></i>
                <span>退出登录</span>
              </a>
            </nav>
          </div>
        </aside>

        <!-- 右侧内容 -->
        <main class="content-area">
          <!-- 个人信息表单 -->
          <div class="content-card" v-show="activeMenu === 'profile'">
            <h2 class="card-title">
              <i class="fas fa-user-edit"></i>
              个人信息
            </h2>
            <el-form
                ref="userFormRef"
                :model="userForm"
                :rules="rules"
                label-width="100px"
                class="profile-form"
            >
              <div class="form-grid">
                <el-form-item label="用户名" prop="username">
                  <el-input
                      v-model="userForm.username"
                      disabled
                      placeholder="用户名"
                  />
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                  <el-select v-model="userForm.sex" placeholder="请选择性别" style="width: 100%">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                    <el-option label="保密" value="保密" />
                  </el-select>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input
                      v-model="userForm.phone"
                      placeholder="请输入手机号"
                  />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input
                      v-model="userForm.email"
                      placeholder="请输入邮箱地址"
                  />
                </el-form-item>
                <el-form-item label="个人简介" prop="bio" class="full-width">
                  <el-input
                      v-model="userForm.bio"
                      type="textarea"
                      :rows="4"
                      placeholder="介绍一下自己吧..."
                  />
                </el-form-item>
              </div>
              <div class="form-actions">
                <el-button class="cancel-btn">取消</el-button>
                <el-button type="primary" @click="submitUserInfo" class="save-btn">
                  保存修改
                </el-button>
              </div>
            </el-form>
          </div>

          <!-- 修改密码表单 -->
          <div class="content-card" v-show="activeMenu === 'settings'">
            <h2 class="card-title">
              <i class="fas fa-key"></i>
              修改密码
            </h2>
            <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="120px"
                class="password-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                    placeholder="请输入当前密码"
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                    placeholder="请输入新密码"
                />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                    placeholder="请再次输入新密码"
                />
              </el-form-item>
              <div class="form-actions">
                <el-button type="primary" @click="submitPassword" class="save-btn">
                  修改密码
                </el-button>
              </div>
            </el-form>
          </div>

          <!-- 账户统计卡片 -->
          <div class="stats-grid" v-show="activeMenu === 'profile'">
            <div class="stat-card blue">
              <div class="stat-card-content">
                <i class="fas fa-ticket-alt"></i>
                <span class="stat-number">{{ userStats.tickets }}</span>
              </div>
              <div class="stat-card-label">待使用门票</div>
            </div>
            <div class="stat-card orange">
              <div class="stat-card-content">
                <i class="fas fa-heart"></i>
                <span class="stat-number">{{ userStats.collections }}</span>
              </div>
              <div class="stat-card-label">收藏景点</div>
            </div>
            <div class="stat-card pink">
              <div class="stat-card-content">
                <i class="fas fa-book"></i>
                <span class="stat-number">{{ userStats.guides }}</span>
              </div>
              <div class="stat-card-label">发布攻略</div>
            </div>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/store/user";
import request from "@/utils/request";

const baseAPI = import.meta.env.VUE_APP_BASE_API || "/api";
const userStore = useUserStore();
const activeMenu = ref("profile");

// 用户统计数据
const userStats = reactive({
  guides: 12,
  collections: 28,
  orders: 5,
  tickets: 5
});

// 表单引用
const userFormRef = ref(null);
const passwordFormRef = ref(null);

// 用户表单数据
const userForm = reactive({
  id: "",
  username: "",
  email: "",
  phone: "",
  sex: "",
  avatar: "",
  bio: "热爱旅行,喜欢摄影,走遍祖国大好河山是我的梦想。"
});

// 头像地址
const avatarUrl = computed(() => {
  // return baseAPI + userForm.avatar;
  return userForm.avatar ? baseAPI + userForm.avatar : '';
});

// 密码表单数据
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 表单校验规则
const rules = {
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    {
      type: "email",
      message: "请输入正确的邮箱地址",
      trigger: ["blur", "change"],
    },
  ],
  phone: [
    { required: false, trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号码",
      trigger: ["blur", "change"],
    },
  ],
};

// 密码表单校验规则
const passwordRules = {
  oldPassword: [
    { required: true, message: "请输入旧密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6个字符", trigger: "blur" },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能小于6个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: ["blur", "change"],
    },
  ],
};

// 获取用户信息
const getUserInfo = async () => {
  try {
    // 如果用户已登录，从 store 中获取用户信息
    if (userStore.isLoggedIn) {
      // 从后端重新获取最新的用户信息
      const response = await request.get("/user/current", null, {
        showDefaultMsg: false,
      });

      // 确保返回数据存在
      if (response) {
        // 更新store中的用户信息
        userStore.updateUserInfo(response);

        // 直接更新表单数据
        userForm.id = response.id || "";
        userForm.username = response.username || "";
        userForm.email = response.email || "";
        userForm.phone = response.phone || "";
        userForm.sex = response.sex || "男";
        userForm.avatar = response.avatar || "";

        console.log("用户信息加载成功:", userForm);
      }
    }
  } catch (error) {
    console.error("获取用户信息失败", error);
    ElMessage.error("获取用户信息失败");
  }
};

// 上传头像前的校验
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === "image/jpeg";
  const isPNG = file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG && !isPNG) {
    ElMessage.error("头像只能是 JPG 或 PNG 格式!");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("头像大小不能超过 2MB!");
    return false;
  }
  return true;
};

// 自定义头像上传方法
const customUploadAvatar = async (options) => {
  try {
    const { file } = options;

    // 创建 FormData 对象
    const formData = new FormData();
    formData.append("file", file);

    // 设置自定义上传选项
    const uploadOptions = {
      headers: {
        token: localStorage.getItem("token") || "",
      },
      // 不进行JSON处理
      transformRequest: [(data) => data],
      // 自定义成功消息
      successMsg: "头像上传成功",
      // 自定义错误消息
      errorMsg: "头像上传失败",
      // 成功回调
      onSuccess: async (data) => {
        // 更新用户头像
        userForm.avatar = data;

        // 保存到后端
        await updateUserAvatar(data);

        // 通知上传成功
        options.onSuccess({ data });
      },
      // 错误回调
      onError: (error) => {
        console.error("头像上传错误:", error);
        options.onError(new Error(error.message || "上传失败"));
      },
    };

    // 发送上传请求
    await request.post("/file/upload/img", formData, uploadOptions);
  } catch (error) {
    options.onError(error);
    console.error("头像上传过程发生错误:", error);
  }
};

// 更新用户头像信息
const updateUserAvatar = async (avatarPath) => {
  try {
    await request.put(
        `/user/${userForm.id}`,
        { avatar: avatarPath },
        {
          showDefaultMsg: false,
          onSuccess: (data) => {
            // 更新本地用户信息
            const updatedUserInfo = { ...userStore.userInfo, avatar: avatarPath };
            userStore.updateUserInfo(updatedUserInfo);
          },
          onError: (error) => {
            console.error("头像信息保存失败", error);
            ElMessage.error("头像信息保存失败");
          },
        }
    );
  } catch (error) {
    console.error("头像信息保存失败", error);
    ElMessage.error("头像信息保存失败");
    throw error;
  }
};

// 提交用户信息更新
const submitUserInfo = async () => {
  if (!userFormRef.value) return;

  try {
    // 表单验证
    await userFormRef.value.validate();

    await request.put(
        `/user/${userForm.id}`,
        {
          name: userForm.name,
          email: userForm.email,
          phone: userForm.phone,
          sex: userForm.sex,
        },
        {
          showDefaultMsg: false,
          successMsg: "个人信息更新成功!",
          onSuccess: (data) => {
            // 更新本地用户信息
            const updatedUserInfo = {
              ...userStore.userInfo,
              name: userForm.name,
              email: userForm.email,
              phone: userForm.phone,
              sex: userForm.sex,
            };
            userStore.updateUserInfo(updatedUserInfo);
          },
          onError: (error) => {
            console.error("用户信息更新失败", error);
            ElMessage.error("用户信息更新失败");
          },
        }
    );

  } catch (error) {
    console.error("保存个人信息失败", error);
    ElMessage.error("保存个人信息失败");
  }
};

// 提交密码修改
const submitPassword = async () => {
  if (!passwordFormRef.value) return;

  try {
    // 表单验证
    await passwordFormRef.value.validate();

    await request.put(
        `/user/password/${userForm.id}`,
        {
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword,
        },
        {
          showDefaultMsg: false,

          onSuccess: (data) => {
            // 清空表单
            passwordForm.oldPassword = "";
            passwordForm.newPassword = "";
            passwordForm.confirmPassword = "";

            // 提示用户重新登录
            ElMessageBox.confirm("密码已修改，需要重新登录", "提示", {
              confirmButtonText: "重新登录",
              cancelButtonText: "取消",
              type: "warning",
            }).then(() => {
              // 清除用户信息并跳转到登录页
              userStore.clearUserInfo();
              window.location.href = "/login";
            });
          },
          onError: (error) => {
            console.error("密码信息保存失败", error);
            ElMessage.error("密码信息保存失败");
          },
        }
    );
  } catch (error) {
    console.error("密码修改失败", error);
    ElMessage.error(error.message || "密码修改失败");
  }
};

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.clearUserInfo();
    window.location.href = '/login';
  }).catch(() => {
    // 取消操作
  });
};

// 监听用户表单数据变化
watch(
    userForm,
    (newVal) => {
      console.log("用户表单数据变化:", newVal);
    },
    { deep: true }
);

// 组件挂载时获取用户信息
onMounted(() => {
  getUserInfo();
});
</script>

<style lang="scss" scoped>
.profile-container {
  min-height: 100vh;
  background: #f3f4f6;

  // 用户信息头部
  .user-header {
    background: #3b82f6;
    color: white;
    padding: 48px 0;

    .header-inner {
      max-width: 1280px;
      margin: 0 auto;
      padding: 0 24px;
    }

    .user-info-section {
      display: flex;
      align-items: center;
      gap: 24px;
    }

    .avatar-container {
      position: relative;

      .user-avatar {
        width: 128px;
        height: 128px;
        border: 4px solid white;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
        background: #667eea;
        color: white;
        font-size: 48px;
        font-weight: 700;
      }

      .avatar-uploader {
        position: absolute;
        bottom: 0;
        right: 0;

        .upload-btn {
          width: 40px;
          height: 40px;
          background: white;
          color: #3b82f6;
          border: none;
          border-radius: 50%;
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all 0.3s ease;

          &:hover {
            background: #f3f4f6;
            transform: scale(1.1);
          }

          i {
            font-size: 16px;
          }
        }
      }
    }

    .user-details {
      flex: 1;

      .user-name {
        font-size: 30px;
        font-weight: 700;
        margin: 0 0 8px;
      }

      .user-desc {
        font-size: 18px;
        opacity: 0.9;
        margin: 0 0 16px;
      }

      .user-stats {
        display: flex;
        gap: 24px;

        .stat-item {
          text-align: center;

          .stat-value {
            font-size: 24px;
            font-weight: 700;
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 14px;
            opacity: 0.8;
          }
        }
      }
    }
  }

  // 主内容区域
  .main-content {
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 24px;

    .content-wrapper {
      display: grid;
      grid-template-columns: 1fr 3fr;
      gap: 24px;

      @media (max-width: 1024px) {
        grid-template-columns: 1fr;
      }
    }
  }

  // 左侧菜单
  .sidebar {
    .sidebar-inner {
      background: white;
      border-radius: 16px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      overflow: hidden;
    }

    .sidebar-nav {
      padding: 8px;

      .nav-item {
        display: flex;
        align-items: center;
        padding: 12px 16px;
        color: #4b5563;
        text-decoration: none;
        border-radius: 12px;
        margin-bottom: 4px;
        transition: all 0.3s ease;
        font-weight: 500;

        i {
          width: 24px;
          margin-right: 8px;
          font-size: 16px;
        }

        &:hover {
          background: #f3f4f6;
        }

        &.active {
          background: #eff6ff;
          color: #3b82f6;
          font-weight: 600;
        }

        &.logout {
          color: #ef4444;

          &:hover {
            background: #fef2f2;
          }
        }
      }
    }
  }

  // 右侧内容区域
  .content-area {
    .content-card {
      background: white;
      border-radius: 16px;
      padding: 32px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      margin-bottom: 24px;

      .card-title {
        font-size: 24px;
        font-weight: 700;
        color: #111827;
        margin: 0 0 24px;
        display: flex;
        align-items: center;
        gap: 12px;

        i {
          color: #3b82f6;
        }
      }
    }

    // 个人信息表单
    .profile-form {
      .form-grid {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 24px;
        margin-bottom: 24px;

        @media (max-width: 768px) {
          grid-template-columns: 1fr;
        }

        .full-width {
          grid-column: 1 / -1;
        }
      }

      :deep(.el-form-item__label) {
        font-weight: 600;
        color: #374151;
        font-size: 14px;
      }

      :deep(.el-input__wrapper) {
        padding: 12px 16px;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        box-shadow: none;
        transition: all 0.3s ease;

        &:hover {
          border-color: #3b82f6;
        }

        &.is-focus {
          border-color: #3b82f6;
          box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }
      }

      :deep(.el-textarea__inner) {
        padding: 12px 16px;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        transition: all 0.3s ease;

        &:hover {
          border-color: #3b82f6;
        }

        &:focus {
          border-color: #3b82f6;
          box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }
      }

      :deep(.el-select) {
        width: 100%;
      }

      .form-actions {
        display: flex;
        justify-content: flex-end;
        gap: 12px;
        margin-top: 32px;

        .cancel-btn {
          padding: 12px 24px;
          border: 2px solid #d1d5db;
          color: #4b5563;
          border-radius: 12px;
          font-weight: 600;
          transition: all 0.3s ease;

          &:hover {
            background: #f3f4f6;
          }
        }

        .save-btn {
          padding: 12px 24px;
          background: #3b82f6;
          border: none;
          border-radius: 12px;
          font-weight: 600;
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
          transition: all 0.3s ease;

          &:hover {
            background: #2563eb;
            box-shadow: 0 6px 16px rgba(59, 130, 246, 0.6);
            transform: translateY(-2px);
          }
        }
      }
    }

    // 修改密码表单
    .password-form {
      max-width: 600px;

      :deep(.el-form-item__label) {
        font-weight: 600;
        color: #374151;
        font-size: 14px;
      }

      :deep(.el-input__wrapper) {
        padding: 12px 16px;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        box-shadow: none;
        transition: all 0.3s ease;

        &:hover {
          border-color: #3b82f6;
        }

        &.is-focus {
          border-color: #3b82f6;
          box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }
      }

      .form-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 32px;

        .save-btn {
          padding: 12px 24px;
          background: #3b82f6;
          border: none;
          border-radius: 12px;
          font-weight: 600;
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
          transition: all 0.3s ease;

          &:hover {
            background: #2563eb;
            box-shadow: 0 6px 16px rgba(59, 130, 246, 0.6);
            transform: translateY(-2px);
          }
        }
      }
    }

    // 统计卡片网格
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 24px;

      @media (max-width: 768px) {
        grid-template-columns: 1fr;
      }

      .stat-card {
        border-radius: 16px;
        padding: 24px;
        color: white;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
        }

        &.blue {
          background: #3b82f6;
        }

        &.orange {
          background: #f97316;
        }

        &.pink {
          background: #ec4899;
        }

        .stat-card-content {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 16px;

          i {
            font-size: 30px;
          }

          .stat-number {
            font-size: 24px;
            font-weight: 700;
          }
        }

        .stat-card-label {
          font-size: 16px;
          font-weight: 600;
        }
      }
    }

    // 占位内容
    .placeholder-content {
      text-align: center;
      padding: 60px 20px;
      color: #9ca3af;

      .placeholder-icon {
        font-size: 64px;
        margin-bottom: 16px;
        opacity: 0.5;
      }

      p {
        font-size: 16px;
        margin: 0;
      }
    }
  }
}
</style>