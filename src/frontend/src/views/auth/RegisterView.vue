<template>
  <div class="register-container">
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
    <div class="register-form-wrapper">
      <!-- 系统Logo和标题 -->
      <div class="register-header">
        <div class="logo">
          <ElIcon :size="40" class="logo-icon">
            <ShieldIcon />
          </ElIcon>
        </div>
        <h1 class="title">
          创建账户
        </h1>
        <p class="subtitle">
          加入JavaWeb安全学习之旅
        </p>
      </div>

      <!-- 注册表单 -->
      <ElCard class="register-card" shadow="always">
        <ElForm
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
          label-position="top"
        >
          <!-- 用户名输入 -->
          <ElFormItem label="用户名" prop="username">
            <ElInput
              v-model="registerForm.username"
              placeholder="3-20字符，支持字母、数字、下划线"
              prefix-icon="User"
              size="large"
              clearable
              @blur="checkUsernameAvailability"
            />
            <div v-if="usernameStatus.checked" class="field-status">
              <ElIcon v-if="usernameStatus.available" color="#67c23a">
                <Check />
              </ElIcon>
              <ElIcon v-else color="#f56c6c">
                <Close />
              </ElIcon>
              <span :class="usernameStatus.available ? 'status-success' : 'status-error'">
                {{ usernameStatus.message }}
              </span>
            </div>
          </ElFormItem>

          <!-- 邮箱输入 -->
          <ElFormItem label="邮箱地址" prop="email">
            <ElInput
              v-model="registerForm.email"
              placeholder="请输入有效的邮箱地址"
              prefix-icon="Message"
              size="large"
              clearable
              @blur="checkEmailAvailability"
            />
            <div v-if="emailStatus.checked" class="field-status">
              <ElIcon v-if="emailStatus.available" color="#67c23a">
                <Check />
              </ElIcon>
              <ElIcon v-else color="#f56c6c">
                <Close />
              </ElIcon>
              <span :class="emailStatus.available ? 'status-success' : 'status-error'">
                {{ emailStatus.message }}
              </span>
            </div>
          </ElFormItem>

          <!-- 密码输入 -->
          <ElFormItem label="密码" prop="password">
            <ElInput
              v-model="registerForm.password"
              type="password"
              placeholder="8-32字符，需包含字母和数字"
              prefix-icon="Lock"
              size="large"
              show-password
              clearable
            />
            <div class="password-strength">
              <div class="strength-bar">
                <div
                  class="strength-fill"
                  :class="passwordStrength.level"
                  :style="{ width: `${passwordStrength.score}%` }"
                />
              </div>
              <span class="strength-text" :class="passwordStrength.level">
                {{ passwordStrength.text }}
              </span>
            </div>
          </ElFormItem>

          <!-- 确认密码输入 -->
          <ElFormItem label="确认密码" prop="confirmPassword">
            <ElInput
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              clearable
            />
          </ElFormItem>

          <!-- 真实姓名输入（可选） -->
          <ElFormItem label="真实姓名（可选）" prop="fullName">
            <ElInput
              v-model="registerForm.fullName"
              placeholder="请输入您的真实姓名"
              prefix-icon="UserFilled"
              size="large"
              clearable
            />
          </ElFormItem>

          <!-- 个人简介（可选） -->
          <ElFormItem label="个人简介（可选）" prop="bio">
            <ElInput
              v-model="registerForm.bio"
              type="textarea"
              placeholder="简单介绍一下自己吧..."
              :rows="3"
              maxlength="500"
              show-word-limit
            />
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
                v-model="registerForm.captchaAnswer"
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

          <!-- 用户协议 -->
          <ElFormItem prop="agreeToTerms">
            <ElCheckbox v-model="registerForm.agreeToTerms">
              我已阅读并同意
              <ElLink type="primary" @click="showTermsDialog = true">
                《用户服务协议》
              </ElLink>
              和
              <ElLink type="primary" @click="showPrivacyDialog = true">
                《隐私政策》
              </ElLink>
            </ElCheckbox>
          </ElFormItem>

          <!-- 错误提示 -->
          <div v-if="authStore.registerError" class="error-message">
            <ElAlert
              :title="authStore.registerError"
              type="error"
              :closable="false"
              show-icon
            />
          </div>

          <!-- 注册按钮 -->
          <ElFormItem>
            <ElButton
              type="primary"
              size="large"
              class="register-btn"
              :loading="authStore.isLoading"
              :disabled="!canRegister"
              @click="handleRegister"
            >
              {{ authStore.isLoading ? '注册中...' : '创建账户' }}
            </ElButton>
          </ElFormItem>

          <!-- 登录链接 -->
          <div class="login-link">
            <span>已有账户？</span>
            <RouterLink to="/login" class="login-text">
              立即登录
            </RouterLink>
          </div>
        </ElForm>
      </ElCard>
    </div>

    <!-- 用户服务协议对话框 -->
    <ElDialog
      v-model="showTermsDialog"
      title="用户服务协议"
      width="600px"
      class="terms-dialog"
    >
      <div class="terms-content">
        <h3>1. 服务条款的接受</h3>
        <p>欢迎使用JavaWeb安全教学系统。通过访问和使用本服务，您表示同意遵守本协议的所有条款。</p>

        <h3>2. 服务描述</h3>
        <p>本系统提供Web安全相关的教学内容，包括但不限于漏洞学习、安全测试、挑战练习等功能。</p>

        <h3>3. 用户责任</h3>
        <p>用户承诺仅将所学知识用于合法目的，不得利用系统学习的技术进行任何非法活动。</p>

        <h3>4. 隐私保护</h3>
        <p>我们承诺保护用户隐私，详细信息请参阅隐私政策。</p>

        <h3>5. 免责声明</h3>
        <p>本系统仅供学习使用，对用户的学习效果不承担任何保证责任。</p>
      </div>

      <template #footer>
        <ElButton @click="showTermsDialog = false">
          关闭
        </ElButton>
      </template>
    </ElDialog>

    <!-- 隐私政策对话框 -->
    <ElDialog
      v-model="showPrivacyDialog"
      title="隐私政策"
      width="600px"
      class="privacy-dialog"
    >
      <div class="privacy-content">
        <h3>1. 信息收集</h3>
        <p>我们仅收集必要的用户信息，包括用户名、邮箱和学习进度数据。</p>

        <h3>2. 信息使用</h3>
        <p>收集的信息仅用于提供教学服务和改善用户体验。</p>

        <h3>3. 信息共享</h3>
        <p>除法律要求外，我们不会与第三方共享用户个人信息。</p>

        <h3>4. 数据安全</h3>
        <p>我们采用业界标准的安全措施保护用户数据。</p>

        <h3>5. 权利保障</h3>
        <p>用户有权查看、修改或删除个人信息。</p>
      </div>

      <template #footer>
        <ElButton @click="showPrivacyDialog = false">
          关闭
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
// 修复图标导入问题 - 使用确定存在的图标
import {
  Lock as ShieldIcon, // 使用Lock图标替代Shield
  User,
  Message,
  Lock,
  UserFilled,
  Check,
  Close,
  Link
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
// 修复：现在添加了真实的API函数
import {
  checkUsernameAvailability as fetchUsernameAvailability,
  checkEmailAvailability as fetchEmailAvailability,
  getCaptcha,
  getNonce,
  getServerTime
} from '@/api/auth'
import type { RegisterForm } from '@/types/api'

// ================================
// 组件状态
// ================================

const router = useRouter()
const authStore = useAuthStore()

const registerFormRef = ref<FormInstance>()
const showTermsDialog = ref(false)
const showPrivacyDialog = ref(false)

// GitHub链接（从环境变量或默认值获取）
const githubUrl = (import.meta.env.VITE_APP_GITHUB_URL ||
  (typeof (window as any).__GITHUB_URL__ !== 'undefined' ? (window as any).__GITHUB_URL__ : 'https://github.com/javaweb-security/teaching-system')) as string

// 注册表单数据
const registerForm = reactive<RegisterForm>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  fullName: '',
  bio: '',
  agreeToTerms: false,
  captchaId: '',
  captchaAnswer: '',
  nonce: '',
  timestamp: ''
})

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

// 字段验证状态
const usernameStatus = reactive({
  checked: false,
  available: false,
  message: ''
})

const emailStatus = reactive({
  checked: false,
  available: false,
  message: ''
})

// ================================
// 计算属性
// ================================

// 密码强度计算
const passwordStrength = computed(() => {
  const { password } = registerForm
  if (!password) {
    return { score: 0, level: 'weak', text: '请输入密码' }
  }

  let score = 0
  let level = 'weak'
  let text = '弱'

  // 长度检查
  if (password.length >= 8) score += 25
  if (password.length >= 12) score += 25

  // 字符类型检查
  if (/[a-z]/.test(password)) score += 10
  if (/[A-Z]/.test(password)) score += 10
  if (/[0-9]/.test(password)) score += 15
  if (/[^A-Za-z0-9]/.test(password)) score += 15

  // 确定强度等级
  if (score >= 80) {
    level = 'strong'
    text = '强'
  } else if (score >= 50) {
    level = 'medium'
    text = '中等'
  }

  return { score, level, text }
})

// 是否可以注册
const canRegister = computed(() => {
  return (
    registerForm.username &&
    registerForm.email &&
    registerForm.password &&
    registerForm.confirmPassword &&
    registerForm.agreeToTerms &&
    usernameStatus.available &&
    emailStatus.available &&
    passwordStrength.value.score >= 50
  )
})

// ================================
// 表单验证规则
// ================================

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' },
    { max: 100, message: '邮箱长度不能超过100个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 32, message: '密码长度为8-32个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  fullName: [
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  bio: [
    { max: 500, message: '个人简介不能超过500个字符', trigger: 'blur' }
  ],
  agreeToTerms: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请同意用户服务协议'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// ================================
// 事件处理
// ================================

/**
 * 检查用户名可用性
 */
const checkUsernameAvailability = async () => {
  if (!registerForm.username || registerForm.username.length < 3) {
    usernameStatus.checked = false
    return
  }

  try {
    const response = await fetchUsernameAvailability(registerForm.username)
    const available = response.data?.available === true

    usernameStatus.checked = true
    usernameStatus.available = available
    usernameStatus.message = available ? '用户名可用' : '用户名已被占用'
  } catch (error) {
    console.error('检查用户名失败:', error)
    usernameStatus.checked = true
    usernameStatus.available = false
    usernameStatus.message = '暂时无法验证用户名，请稍后重试'
  }
}

/**
 * 检查邮箱可用性
 */
const checkEmailAvailability = async () => {
  if (!registerForm.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(registerForm.email)) {
    emailStatus.checked = false
    return
  }

  try {
    const response = await fetchEmailAvailability(registerForm.email)
    const available = response.data?.available === true

    emailStatus.checked = true
    emailStatus.available = available
    emailStatus.message = available ? '邮箱可用' : '邮箱已被占用'
  } catch (error) {
    console.error('检查邮箱失败:', error)
    emailStatus.checked = true
    emailStatus.available = false
    emailStatus.message = '暂时无法验证邮箱，请稍后重试'
  }
}

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
      registerForm.captchaId = captchaId.value
      
      // 使用服务器时间设置过期时间（统一使用绝对时间戳）
      // 确保无论服务器是否返回expiryTime，都使用一致的绝对时间戳格式
      if (captchaResponse.data.expiryTime) {
        // 服务器返回的过期时间（绝对时间戳）
        const serverExpiryTime = parseInt(captchaResponse.data.expiryTime)
        // 验证时间戳有效性（应该是未来的时间戳）
        const currentTime = getCurrentServerTime()
        if (serverExpiryTime > currentTime) {
          captchaExpiryTime.value = serverExpiryTime
        } else {
          // 如果服务器返回的时间戳已过期，使用当前时间+120秒
          captchaExpiryTime.value = currentTime + 120 * 1000
        }
      } else {
        // 服务器未返回过期时间，使用当前服务器时间+120秒（绝对时间戳）
        captchaExpiryTime.value = getCurrentServerTime() + 120 * 1000
      }
      
      // 清空答案
      registerForm.captchaAnswer = ''
      
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
      registerForm.nonce = newNonce
      registerForm.timestamp = newTimestamp
      
      // 使用与验证码相同的过期时间（统一使用绝对时间戳，确保时间一致）
      // 优先使用验证码的过期时间，确保两者时间完全一致
      if (captchaResponse.data && captchaResponse.data.expiryTime) {
        // 使用验证码的过期时间（绝对时间戳）
        const serverExpiryTime = parseInt(captchaResponse.data.expiryTime)
        const currentTime = getCurrentServerTime()
        if (serverExpiryTime > currentTime) {
          nonceExpiryTime.value = serverExpiryTime
        } else {
          // 如果服务器返回的时间戳已过期，使用当前时间+120秒
          nonceExpiryTime.value = currentTime + 120 * 1000
        }
      } else if (nonceResponse.data && nonceResponse.data.expiryTime) {
        // 如果验证码没有过期时间，使用nonce的过期时间（绝对时间戳）
        const serverExpiryTime = parseInt(nonceResponse.data.expiryTime)
        const currentTime = getCurrentServerTime()
        if (serverExpiryTime > currentTime) {
          nonceExpiryTime.value = serverExpiryTime
        } else {
          // 如果服务器返回的时间戳已过期，使用当前时间+120秒
          nonceExpiryTime.value = currentTime + 120 * 1000
        }
      } else {
        // 如果都没有过期时间，使用当前服务器时间 + 120秒（绝对时间戳，与验证码一致）
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
      registerForm.nonce = newNonce
      registerForm.timestamp = newTimestamp
      
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
 * 处理注册
 */
const handleRegister = async () => {
  if (!registerFormRef.value) {
    return
  }

  try {
    // 验证表单
    const valid = await registerFormRef.value.validate()
    if (!valid) {
      return
    }

    // 检查必要条件
    if (!canRegister.value) {
      ElMessage.warning('请完善注册信息')
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
    if (!registerForm.captchaAnswer || !registerForm.captchaId) {
      ElMessage.warning('请输入验证码')
      return
    }

    // 检查nonce token是否存在
    if (!registerForm.nonce || !registerForm.timestamp) {
      ElMessage.warning('安全令牌不存在，正在自动刷新...')
      await refreshCaptcha(true) // 同步刷新验证码和nonce token
      ElMessage.info('请重新提交注册')
      return
    }
    
    // 确保表单中的nonce token和时间戳都是最新的（防御性编程）
    registerForm.nonce = nonceToken.value
    registerForm.timestamp = nonceTimestamp.value
    
    console.log('[注册提交] 验证码和nonce token状态:', {
      captchaRemaining: captchaRemaining + '秒',
      nonceRemaining: nonceRemaining + '秒',
      nonce: registerForm.nonce?.substring(0, 20) + '...',
      timestamp: registerForm.timestamp
    })

    // 清除之前的错误信息
    authStore.clearErrors()

    // 修复：auth store中的方法名是registerUser，不是register
    const success = await authStore.registerUser(registerForm)

    if (success) {
      // 注册成功，清理定时器
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
      // 注册成功，跳转到登录页（成功消息已在store中显示）
      await router.push('/login')
    } else {
      // 注册失败，同步刷新验证码和nonce token
      await refreshCaptcha()
    }
  } catch (error) {
    console.error('注册处理失败:', error)
    // 注册失败，同步刷新验证码和nonce token
    await refreshCaptcha()
  }
}

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

  // 启动服务器时间同步（每30秒同步一次）
  startServerTimeSync()

  // 页面加载时获取验证码和nonce token（同步刷新）
  await refreshCaptcha()
})

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
})

// ================================
// 监听器
// ================================

// 监听用户名变化，重置验证状态
watch(() => registerForm.username, () => {
  if (usernameStatus.checked) {
    usernameStatus.checked = false
  }
})

// 监听邮箱变化，重置验证状态
watch(() => registerForm.email, () => {
  if (emailStatus.checked) {
    emailStatus.checked = false
  }
})
</script>

<style scoped lang="scss">
.register-container {
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

.register-form-wrapper {
  width: 100%;
  max-width: 500px;
}

.register-header {
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

.register-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.register-form {
  padding: 30px;

  .field-status {
    display: flex;
    align-items: center;
    margin-top: 4px;
    font-size: 12px;

    .el-icon {
      margin-right: 4px;
    }

    .status-success {
      color: #67c23a;
    }

    .status-error {
      color: #f56c6c;
    }
  }

  .password-strength {
    margin-top: 4px;

    .strength-bar {
      height: 4px;
      background: #e4e7ed;
      border-radius: 2px;
      overflow: hidden;
      margin-bottom: 4px;

      .strength-fill {
        height: 100%;
        transition: all 0.3s ease;

        &.weak {
          background: #f56c6c;
        }

        &.medium {
          background: #e6a23c;
        }

        &.strong {
          background: #67c23a;
        }
      }
    }

    .strength-text {
      font-size: 12px;

      &.weak {
        color: #f56c6c;
      }

      &.medium {
        color: #e6a23c;
      }

      &.strong {
        color: #67c23a;
      }
    }
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

  .register-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 8px;
  }

  .login-link {
    text-align: center;
    margin-top: 16px;
    color: #666;

    .login-text {
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

.terms-dialog,
.privacy-dialog {
  :deep(.el-dialog__body) {
    max-height: 400px;
    overflow-y: auto;
  }

  .terms-content,
  .privacy-content {
    h3 {
      color: #303133;
      font-size: 16px;
      margin: 16px 0 8px 0;

      &:first-child {
        margin-top: 0;
      }
    }

    p {
      color: #606266;
      line-height: 1.6;
      margin: 0 0 12px 0;
    }
  }
}

// 响应式设计
@media (max-width: 480px) {
  .register-container {
    padding: 16px;
  }

  .register-form-wrapper {
    max-width: 100%;
  }

  .register-header {
    .title {
      font-size: 24px;
    }

    .subtitle {
      font-size: 12px;
    }
  }

  .register-form {
    padding: 20px;
  }
}
</style>
