<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <!-- 系统Logo和标题 -->
      <div class="login-header">
        <div class="logo">
          <ElIcon :size="40" class="logo-icon">
            <SecurityIcon />
          </ElIcon>
        </div>
        <h1 class="title">
          JavaWeb安全教学系统
        </h1>
        <p class="subtitle">
          基于OWASP Top 10的Web安全教学平台
        </p>
      </div>

      <!-- 登录表单 -->
      <ElCard class="login-card" shadow="always">
        <template #header>
          <div class="card-header">
            <span class="card-title">用户登录</span>
          </div>
        </template>

        <ElForm
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <!-- 用户名/邮箱输入 -->
          <ElFormItem prop="loginIdentifier">
            <ElInput
              v-model="loginForm.loginIdentifier"
              placeholder="请输入用户名或邮箱"
              prefix-icon="User"
              size="large"
              clearable
              @keyup.enter="handleLogin"
            />
          </ElFormItem>

          <!-- 密码输入 -->
          <ElFormItem prop="password">
            <ElInput
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              clearable
              @keyup.enter="handleLogin"
            />
          </ElFormItem>

          <!-- 记住登录状态 -->
          <ElFormItem>
            <div class="login-options">
              <ElCheckbox v-model="loginForm.rememberMe">
                记住我
              </ElCheckbox>
              <!-- 忘记密码功能暂时注释掉 - 2025-01-15 -->
              <!-- <ElLink type="primary" @click="showForgotPassword = true">
                忘记密码？
              </ElLink> -->
            </div>
          </ElFormItem>

          <!-- 错误提示 -->
          <div v-if="authStore.loginError" class="error-message">
            <ElAlert
              :title="authStore.loginError"
              type="error"
              :closable="false"
              show-icon
            />
          </div>

          <!-- 登录按钮 -->
          <ElFormItem>
            <ElButton
              type="primary"
              size="large"
              class="login-btn"
              :loading="authStore.isLoading"
              @click="handleLogin"
            >
              {{ authStore.isLoading ? '登录中...' : '登录' }}
            </ElButton>
          </ElFormItem>

          <!-- 注册链接 -->
          <div class="register-link">
            <span>还没有账户？</span>
            <RouterLink to="/register" class="register-text">
              立即注册
            </RouterLink>
          </div>
        </ElForm>
      </ElCard>

      <!-- 系统特性介绍 -->
      <div class="features">
        <div class="feature-item">
          <ElIcon :size="24" color="#409eff">
            <Document />
          </ElIcon>
          <span>OWASP Top 10 学习</span>
        </div>
        <div class="feature-item">
          <ElIcon :size="24" color="#67c23a">
            <Trophy />
          </ElIcon>
          <span>实战漏洞演示</span>
        </div>
        <div class="feature-item">
          <ElIcon :size="24" color="#e6a23c">
            <Star />
          </ElIcon>
          <span>挑战模式练习</span>
        </div>
      </div>
    </div>

    <!-- 忘记密码功能暂时注释掉 - 2025-01-15 -->
    <!-- <ElDialog
      v-model="showForgotPassword"
      title="忘记密码"
      width="400px"
    >
      <ElForm
        ref="forgotPasswordFormRef"
        :model="forgotPasswordForm"
        :rules="forgotPasswordRules"
      >
        <ElFormItem label="邮箱地址" prop="email">
          <ElInput
            v-model="forgotPasswordForm.email"
            placeholder="请输入注册邮箱"
            prefix-icon="Message"
          />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <span class="dialog-footer">
          <ElButton @click="showForgotPassword = false">取消</ElButton>
          <ElButton
            type="primary"
            :loading="forgotPasswordLoading"
            @click="handleForgotPassword"
          >
            发送重置邮件
          </ElButton>
        </span>
      </template>
    </ElDialog> -->
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
// 修复图标导入问题 - 使用确定存在的图标
import { User, Lock, Document, Trophy, Star, Message } from '@element-plus/icons-vue'
// 使用确定存在的图标替代Shield
import { Lock as SecurityIcon } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
// 修复：resetPassword函数不存在，暂时注释掉
// import { resetPassword } from '@/api/auth'
import type { UserLoginData } from '@/api/auth'

// ================================
// 组件状态
// ================================

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref<FormInstance>()
const forgotPasswordFormRef = ref<FormInstance>()

// 忘记密码功能暂时注释掉 - 2025-01-15
// const showForgotPassword = ref(false)
// const forgotPasswordLoading = ref(false)

// 登录表单数据
const loginForm = reactive<UserLoginData>({
  loginIdentifier: '',
  password: '',
  rememberMe: false
})

// 忘记密码功能暂时注释掉 - 2025-01-15
// 忘记密码表单数据
// const forgotPasswordForm = reactive({
//   email: ''
// })

// ================================
// 表单验证规则
// ================================

const loginRules: FormRules = {
  loginIdentifier: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码至少8个字符', trigger: 'blur' }
  ]
}

// 忘记密码功能暂时注释掉 - 2025-01-15
// const forgotPasswordRules: FormRules = {
//   email: [
//     { required: true, message: '请输入邮箱地址', trigger: 'blur' },
//     { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
//   ]
// }

// ================================
// 事件处理
// ================================

/**
 * 处理登录
 */
const handleLogin = async () => {
  if (!loginFormRef.value) {
    return
  }

  try {
    // 验证表单
    const valid = await loginFormRef.value.validate()
    if (!valid) {
      return
    }

    // 清除之前的错误信息
    authStore.clearErrors()

    // 执行登录
    const success = await authStore.loginUser(loginForm)

    if (success) {
      // 登录成功，跳转到首页或之前页面
      const redirect = router.currentRoute.value.query.redirect as string
      await router.push(redirect || '/')
    }
  } catch (error) {
    console.error('登录处理失败:', error)
  }
}

// 忘记密码功能暂时注释掉 - 2025-01-15
/**
 * 处理忘记密码
 */
// const handleForgotPassword = async () => {
//   if (!forgotPasswordFormRef.value) return

//   try {
//     // 验证表单
//     const valid = await forgotPasswordFormRef.value.validate()
//     if (!valid) return

//     forgotPasswordLoading.value = true

//     // 修复：resetPassword函数不存在，暂时注释掉相关功能
//     // await resetPassword(forgotPasswordForm.email)

//     // 临时提示：功能待实现
//     ElMessage.info('密码重置功能待实现，请联系管理员')
//     showForgotPassword.value = false
//     forgotPasswordForm.email = ''
//   } catch (error: any) {
//     console.error('发送重置邮件失败:', error)
//     ElMessage.error(error.response?.data?.message || '发送失败，请稍后重试')
//   } finally {
//     forgotPasswordLoading.value = false
//   }
// }

// ================================
// 生命周期
// ================================

onMounted(() => {
  // 如果已经登录，直接跳转到首页
  if (authStore.isLoggedIn) {
    router.push('/')
  }

  // 清除之前的错误信息
  authStore.clearErrors()
})
</script>

<style scoped lang="scss">
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;

  .logo {
    margin-bottom: 16px;

    .logo-icon {
      color: white;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      padding: 12px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }

  .title {
    font-size: 28px;
    font-weight: 600;
    margin: 0 0 8px 0;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }

  .subtitle {
    font-size: 14px;
    opacity: 0.9;
    margin: 0;
    font-weight: 300;
  }
}

.login-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);

  :deep(.el-card__header) {
    background: linear-gradient(135deg, #409eff, #36a3f7);
    border-bottom: none;
    border-radius: 12px 12px 0 0;
  }

  .card-header {
    text-align: center;

    .card-title {
      color: white;
      font-size: 18px;
      font-weight: 600;
    }
  }
}

.login-form {
  padding: 20px 0;

  .login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }

  .error-message {
    margin-bottom: 16px;
  }

  .login-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 8px;
  }

  .register-link {
    text-align: center;
    margin-top: 16px;
    color: #666;

    .register-text {
      color: #409eff;
      text-decoration: none;
      font-weight: 500;
      margin-left: 4px;

      &:hover {
        color: #36a3f7;
      }
    }
  }
}

.features {
  display: flex;
  justify-content: space-around;
  margin-top: 30px;

  .feature-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    color: white;
    opacity: 0.9;

    span {
      margin-top: 8px;
      font-size: 12px;
      text-align: center;
    }
  }
}

// 响应式设计
@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }

  .login-header {
    .title {
      font-size: 24px;
    }

    .subtitle {
      font-size: 12px;
    }
  }

  .features {
    margin-top: 20px;

    .feature-item {
      span {
        font-size: 11px;
      }
    }
  }
}
</style>
