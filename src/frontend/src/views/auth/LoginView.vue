<template>
  <div class="login-container">
    <!-- 右上角GitHub链接 -->
    <div class="github-top-right">
      <ElLink
        :href="githubUrl"
        target="_blank"
        :underline="false"
        class="github-link-icon"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="currentColor"
        >
          <path
            d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"
          />
        </svg>
      </ElLink>
    </div>
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

          <!-- 验证码 -->
          <ElFormItem label="验证码" prop="captchaAnswer">
            <div class="captcha-container">
              <div 
                class="captcha-question"
                @click="() => refreshCaptcha(false)"
                title="点击刷新验证码"
              >
                <span class="question-text">{{ captchaQuestion }}</span>
              </div>
              <ElInput
                v-model="loginForm.captchaAnswer"
                placeholder="请输入答案"
                prefix-icon="Lock"
                size="large"
                clearable
                class="captcha-input"
                @focus="checkCaptchaExpiry"
                @input="checkCaptchaExpiry"
              />
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
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
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
import { getCaptcha, getNonce, getServerTime } from '@/api/auth'
import type { UserLoginData } from '@/api/auth'

// ================================
// 组件状态
// ================================

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref<FormInstance>()
const forgotPasswordFormRef = ref<FormInstance>()

// GitHub链接（从环境变量或默认值获取）
const githubUrl = (import.meta.env.VITE_APP_GITHUB_URL ||
  (typeof (window as any).__GITHUB_URL__ !== 'undefined' ? (window as any).__GITHUB_URL__ : 'https://github.com/javaweb-security/teaching-system')) as string

// 忘记密码功能暂时注释掉 - 2025-01-15
// const showForgotPassword = ref(false)
// const forgotPasswordLoading = ref(false)

// 登录表单数据
const loginForm = reactive<UserLoginData>({
  loginIdentifier: '',
  password: '',
  rememberMe: false,
  captchaId: '',
  captchaAnswer: '',
  nonce: '',
  timestamp: ''
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

// 验证码相关
const captchaQuestion = ref('')
const captchaId = ref('')
const nonceToken = ref('')
const nonceTimestamp = ref('')
const captchaExpiryTime = ref(0) // 验证码过期时间戳（服务器时间）
const nonceExpiryTime = ref(0) // nonce token过期时间戳（服务器时间）
let captchaTimer: ReturnType<typeof setInterval> | null = null
let nonceTimer: ReturnType<typeof setInterval> | null = null
let serverTimeSyncTimer: ReturnType<typeof setInterval> | null = null
let serverTimeOffset = ref(0) // 服务器时间与客户端时间的差值（毫秒）

/**
 * 同步服务器时间
 */
const syncServerTime = async () => {
  try {
    const response = await getServerTime()
    if (response.code === 200 && response.data) {
      const serverTime = response.data.serverTime || response.data.timestamp
      const clientTime = Date.now()
      serverTimeOffset.value = serverTime - clientTime
      console.log(`[时间同步] 服务器时间: ${serverTime}, 客户端时间: ${clientTime}, 时间差: ${serverTimeOffset.value}ms`)
    }
  } catch (error) {
    console.warn('同步服务器时间失败:', error)
    // 同步失败不影响功能，使用客户端时间
    serverTimeOffset.value = 0
  }
}

/**
 * 获取当前服务器时间（客户端时间 + 时间差）
 */
const getCurrentServerTime = () => {
  return Date.now() + serverTimeOffset.value
}

/**
 * 获取验证码（同时刷新nonce token，确保时间一致）
 */
const refreshCaptcha = async (silent = false) => {
  try {
    // 先同步服务器时间，确保时间准确
    await syncServerTime()
    
    // 同时刷新验证码和nonce token，确保它们的时间戳一致
    const [captchaResponse, nonceResponse] = await Promise.all([
      getCaptcha(),
      getNonce()
    ])
    
    // 处理验证码响应
    if (captchaResponse.code === 200 && captchaResponse.data) {
      captchaId.value = captchaResponse.data.captchaId
      captchaQuestion.value = captchaResponse.data.captchaQuestion || ''
      loginForm.captchaId = captchaId.value
      
      // 使用服务器时间设置过期时间（实时同步）
      if (captchaResponse.data.expiryTime) {
        const serverExpiryTime = parseInt(captchaResponse.data.expiryTime)
        captchaExpiryTime.value = serverExpiryTime
      } else {
        captchaExpiryTime.value = getCurrentServerTime() + 120 * 1000
      }
      
      // 清空答案
      loginForm.captchaAnswer = ''
      
      // 启动过期检测定时器
      startCaptchaTimer()
    } else {
      ElMessage.error('获取验证码失败，请稍后重试')
      return
    }
    
    // 处理nonce token响应（与验证码同步刷新）
    if (nonceResponse.code === 200 && nonceResponse.data) {
      const newNonce = nonceResponse.data.nonce
      const newTimestamp = nonceResponse.data.timestamp
      
      // 立即更新表单数据
      nonceToken.value = newNonce
      nonceTimestamp.value = newTimestamp
      loginForm.nonce = newNonce
      loginForm.timestamp = newTimestamp
      
      // 使用与验证码相同的过期时间（确保时间一致）
      if (captchaResponse.data.expiryTime) {
        // 使用验证码的过期时间，确保两者时间一致
        nonceExpiryTime.value = parseInt(captchaResponse.data.expiryTime)
      } else if (nonceResponse.data.expiryTime) {
        nonceExpiryTime.value = parseInt(nonceResponse.data.expiryTime)
      } else {
        // 使用当前服务器时间 + 120秒（与验证码一致）
        nonceExpiryTime.value = getCurrentServerTime() + 120 * 1000
      }
      
      // 启动过期检测定时器
      startNonceTimer()
      
      console.log(`[同步刷新] 验证码和nonce token已同步刷新，过期时间: ${new Date(nonceExpiryTime.value).toLocaleTimeString()}`)
    } else {
      console.warn('获取nonce token失败，但验证码已刷新')
    }
    
    if (!silent) {
      ElMessage.success('验证码已刷新')
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
    ElMessage.error('获取验证码失败，请稍后重试')
  }
}

/**
 * 启动验证码过期检测定时器（基于服务器时间）
 */
const startCaptchaTimer = () => {
  // 清除之前的定时器
  if (captchaTimer) {
    clearInterval(captchaTimer)
  }
  
  captchaTimer = setInterval(() => {
    // 使用服务器时间判断是否过期（实时同步）
    const currentServerTime = getCurrentServerTime()
    const remaining = Math.max(0, Math.floor((captchaExpiryTime.value - currentServerTime) / 1000))
    
    // 如果验证码过期，自动刷新
    if (remaining <= 0) {
      clearInterval(captchaTimer!)
      captchaTimer = null
      // 自动刷新验证码（静默刷新，但必须清空答案）
      refreshCaptcha(true)
      // 提示用户验证码已自动刷新
      ElMessage.info('验证码已自动刷新，请重新输入')
    }
  }, 1000)
}

/**
 * 检查验证码是否过期（在用户输入时检查，基于服务器时间）
 */
const checkCaptchaExpiry = () => {
  if (captchaExpiryTime.value === 0) {
    return // 验证码未初始化
  }
  
  // 使用服务器时间判断（实时同步）
  const currentServerTime = getCurrentServerTime()
  const remaining = Math.max(0, Math.floor((captchaExpiryTime.value - currentServerTime) / 1000))
  if (remaining <= 0) {
    // 验证码已过期，自动刷新
    ElMessage.warning('验证码已过期，正在自动刷新...')
    refreshCaptcha(true)
    ElMessage.info('请重新输入验证码')
  } else if (remaining <= 10) {
    // 剩余时间少于10秒时，提示用户（但不自动刷新，避免打断输入）
    // 实际刷新会在提交时进行
  }
}

/**
 * 获取nonce token（独立刷新，使用与验证码相同的过期时间）
 */
const refreshNonce = async (silent = false) => {
  try {
    // 先同步服务器时间，确保时间准确
    await syncServerTime()
    
    const response = await getNonce()
    if (response.code === 200 && response.data) {
      // 获取最新的nonce token和时间戳
      const newNonce = response.data.nonce
      const newTimestamp = response.data.timestamp
      
      // 立即更新表单数据
      nonceToken.value = newNonce
      nonceTimestamp.value = newTimestamp
      loginForm.nonce = newNonce
      loginForm.timestamp = newTimestamp
      
      // 使用与验证码相同的过期时间（120秒），确保时间一致
      if (captchaExpiryTime.value > 0) {
        // 如果验证码已存在，使用验证码的过期时间
        nonceExpiryTime.value = captchaExpiryTime.value
      } else if (response.data.expiryTime) {
        // 否则使用后端返回的过期时间（但会被调整为120秒）
        const serverExpiryTime = parseInt(response.data.expiryTime)
        // 计算剩余时间，但限制为120秒
        const currentServerTime = getCurrentServerTime()
        const remaining = Math.min(120 * 1000, serverExpiryTime - currentServerTime)
        nonceExpiryTime.value = currentServerTime + remaining
      } else {
        // 使用当前服务器时间 + 120秒（与验证码一致）
        nonceExpiryTime.value = getCurrentServerTime() + 120 * 1000
      }
      
      // 启动过期检测定时器
      startNonceTimer()
      
      if (!silent) {
        console.log(`[Nonce刷新] 新的nonce已刷新，过期时间: ${new Date(nonceExpiryTime.value).toLocaleTimeString()}`)
      }
    } else {
      ElMessage.error('获取安全令牌失败，请稍后重试')
    }
  } catch (error) {
    console.error('获取nonce token失败:', error)
    ElMessage.error('获取安全令牌失败，请稍后重试')
  }
}

/**
 * 启动nonce token过期检测定时器（基于服务器时间）
 * nonce token过期时，同时刷新验证码，确保时间一致
 */
const startNonceTimer = () => {
  // 清除之前的定时器
  if (nonceTimer) {
    clearInterval(nonceTimer)
  }
  
  nonceTimer = setInterval(() => {
    // 使用服务器时间判断是否过期（实时同步）
    const currentServerTime = getCurrentServerTime()
    const remaining = Math.max(0, Math.floor((nonceExpiryTime.value - currentServerTime) / 1000))
    
    // 如果nonce token过期，自动刷新验证码和nonce token（同步刷新，确保时间一致）
    if (remaining <= 0) {
      clearInterval(nonceTimer!)
      nonceTimer = null
      // 自动刷新验证码和nonce token（同步刷新）
      refreshCaptcha(true)
    }
  }, 1000)
}

/**
 * 启动服务器时间同步定时器（每30秒同步一次）
 */
const startServerTimeSync = () => {
  // 清除之前的定时器
  if (serverTimeSyncTimer) {
    clearInterval(serverTimeSyncTimer)
  }
  
  // 立即同步一次
  syncServerTime()
  
  // 每30秒同步一次服务器时间
  serverTimeSyncTimer = setInterval(() => {
    syncServerTime()
  }, 30000) // 30秒
}

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

    // 提交前先同步服务器时间，确保时间准确
    await syncServerTime()
    
    // 检查验证码和nonce token是否过期（基于服务器时间）
    const currentServerTime = getCurrentServerTime()
    const captchaRemaining = Math.max(0, Math.floor((captchaExpiryTime.value - currentServerTime) / 1000))
    const nonceRemaining = Math.max(0, Math.floor((nonceExpiryTime.value - currentServerTime) / 1000))
    
    // 如果验证码或nonce token过期，同步刷新（确保时间一致）
    if (captchaRemaining <= 0 || nonceRemaining <= 0) {
      ElMessage.warning('验证码或安全令牌已过期，正在自动刷新...')
      await refreshCaptcha(true) // 同步刷新验证码和nonce token
      ElMessage.info('请重新输入验证码')
      return
    }
    
    // 如果剩余时间少于10秒，提前刷新（保守策略）
    if (captchaRemaining < 10 || nonceRemaining < 10) {
      ElMessage.warning('验证码或安全令牌即将过期，正在自动刷新...')
      await refreshCaptcha(true) // 同步刷新验证码和nonce token
      ElMessage.info('请重新输入验证码')
      return
    }

    // 检查验证码
    if (!loginForm.captchaAnswer || !loginForm.captchaId) {
      ElMessage.warning('请输入验证码')
      return
    }

    // 检查nonce token是否存在
    if (!loginForm.nonce || !loginForm.timestamp) {
      ElMessage.warning('安全令牌不存在，正在自动刷新...')
      await refreshCaptcha(true) // 同步刷新验证码和nonce token
      ElMessage.info('请重新提交登录')
      return
    }
    
    // 确保表单中的nonce token和时间戳都是最新的（防御性编程）
    loginForm.nonce = nonceToken.value
    loginForm.timestamp = nonceTimestamp.value
    
    console.log('[登录提交] 验证码和nonce token状态:', {
      captchaRemaining: captchaRemaining + '秒',
      nonceRemaining: nonceRemaining + '秒',
      nonce: loginForm.nonce?.substring(0, 20) + '...',
      timestamp: loginForm.timestamp
    })

    // 清除之前的错误信息
    authStore.clearErrors()

    // 执行登录
    const success = await authStore.loginUser(loginForm)

    if (success) {
      // 登录成功，清理定时器
      if (captchaTimer) {
        clearInterval(captchaTimer)
        captchaTimer = null
      }
      if (nonceTimer) {
        clearInterval(nonceTimer)
        nonceTimer = null
      }
      // 登录成功，跳转到首页或之前页面
      const redirect = router.currentRoute.value.query.redirect as string
      await router.push(redirect || '/')
    } else {
      // 登录失败，同步刷新验证码和nonce token
      await refreshCaptcha()
    }
  } catch (error) {
    console.error('登录处理失败:', error)
    // 登录失败，同步刷新验证码和nonce token
    await refreshCaptcha()
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

onMounted(async () => {
  // 如果已经登录，直接跳转到首页
  if (authStore.isLoggedIn) {
    router.push('/')
    return
  }

  // 清除之前的错误信息
  authStore.clearErrors()

  // 启动服务器时间同步定时器（实时同步）
  startServerTimeSync()
  
  // 页面加载时获取验证码和nonce token（同步刷新，确保时间一致）
  await refreshCaptcha()
  
  // 监听页面可见性变化，当页面重新可见时检查并刷新过期的token
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

/**
 * 处理页面可见性变化
 */
const handleVisibilityChange = async () => {
  // 当页面从隐藏变为可见时，先同步服务器时间，然后检查token是否过期
  if (!document.hidden) {
    // 先同步服务器时间
    await syncServerTime()
    
    // 检查验证码和nonce token是否过期（基于服务器时间）
    const currentServerTime = getCurrentServerTime()
    const captchaRemaining = Math.max(0, Math.floor((captchaExpiryTime.value - currentServerTime) / 1000))
    const nonceRemaining = Math.max(0, Math.floor((nonceExpiryTime.value - currentServerTime) / 1000))
    
    // 如果任一过期，同步刷新（确保时间一致）
    if (captchaRemaining <= 0 || nonceRemaining <= 0) {
      // 静默刷新验证码和nonce token（同步刷新）
      await refreshCaptcha(true)
    }
  }
}

onBeforeUnmount(() => {
  // 清理定时器
  if (captchaTimer) {
    clearInterval(captchaTimer)
    captchaTimer = null
  }
  if (nonceTimer) {
    clearInterval(nonceTimer)
    nonceTimer = null
  }
  if (serverTimeSyncTimer) {
    clearInterval(serverTimeSyncTimer)
    serverTimeSyncTimer = null
  }
  
  // 移除页面可见性变化监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange)
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
  position: relative;
  overflow: hidden;

  .github-top-right {
    position: absolute;
    top: 20px;
    right: 20px;
    z-index: 100;

    .github-link-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      backdrop-filter: blur(10px);
      transition: all 0.3s;
      color: white;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: scale(1.1);
      }

      svg {
        width: 24px;
        height: 24px;
      }
    }
  }
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

  .captcha-container {
    display: flex;
    align-items: center;
    gap: 12px;
    width: 100%;

    .captcha-question {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 12px;
      height: 40px;
      background: #f5f7fa;
      border: 1px solid #dcdfe6;
      border-radius: 4px;
      width: 120px; // 固定宽度，对应"验证码"三个字的长度
      flex-shrink: 0;
      cursor: pointer;
      transition: all 0.3s;
      user-select: none;

      &:hover {
        background: #ecf5ff;
        border-color: #409eff;
      }

      &:active {
        background: #d9ecff;
      }

      .question-text {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        white-space: nowrap;
      }
    }

    .captcha-input {
      flex: 1;
      min-width: 0;
    }
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
