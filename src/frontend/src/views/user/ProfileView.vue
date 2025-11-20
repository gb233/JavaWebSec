<template>
  <div class="profile-container">
    <!-- 页面头部 -->
    <div class="profile-header">
      <div class="header-content">
        <div class="user-avatar">
          <ElAvatar :size="80" :src="authStore.userAvatar" fit="cover">
            <ElIcon><UserFilled /></ElIcon>
          </ElAvatar>
          <ElButton
            class="avatar-upload-btn"
            type="primary"
            size="small"
            circle
            @click="showAvatarUpload = true"
          >
            <ElIcon><Camera /></ElIcon>
          </ElButton>
        </div>

        <div class="user-info">
          <h2 class="username">
            {{ authStore.userDisplayName }}
          </h2>
          <div class="user-meta">
            <ElTag :type="getUserStatusColor(authStore.user?.userStatus || '')">
              {{ authStore.userRoleDisplay }}
            </ElTag>
            <span class="join-date">
              加入时间: {{ formatDate(authStore.user?.createdAt) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="profile-content">
      <ElRow :gutter="24">
        <!-- 左侧：用户统计和成就 -->
        <ElCol :lg="8" :md="24">
          <!-- 学习统计卡片 -->
          <ElCard class="stats-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <ElIcon><DataLine /></ElIcon>
                <span>学习统计</span>
              </div>
            </template>

            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">
                  {{ userStats?.totalPoints || 0 }}
                </div>
                <div class="stat-label">
                  总积分
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-value">
                  {{ formatStudyTime(userStats?.totalStudyTime || 0) }}
                </div>
                <div class="stat-label">
                  学习时长
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-value">
                  {{ userStats?.completedVulnerabilities || 0 }}
                </div>
                <div class="stat-label">
                  完成漏洞
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-value">
                  {{ userStats?.passedTests || 0 }}
                </div>
                <div class="stat-label">
                  通过测试
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-value">
                  {{ userStats?.completedChallenges || 0 }}
                </div>
                <div class="stat-label">
                  完成挑战
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-value">
                  {{ userStats?.earnedBadges || 0 }}
                </div>
                <div class="stat-label">
                  获得徽章
                </div>
              </div>
            </div>
          </ElCard>

          <!-- 等级和进度卡片 -->
          <ElCard class="level-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <ElIcon><Medal /></ElIcon>
                <span>等级进度</span>
              </div>
            </template>

            <div class="level-info">
              <div class="current-level">
                <span class="level-text">等级 {{ userLevel.level }}</span>
                <ElProgress
                  :percentage="userLevel.progress"
                  :stroke-width="12"
                  :text-inside="true"
                  class="level-progress"
                />
              </div>
              <div class="level-details">
                <span>距离下一等级还需 {{ userLevel.nextLevelPoints - (userStats?.totalPoints || 0) }} 积分</span>
              </div>
            </div>
          </ElCard>

          <!-- 学习连续天数卡片 -->
          <ElCard class="streak-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <ElIcon><Calendar /></ElIcon>
                <span>学习连续性</span>
              </div>
            </template>

            <div class="streak-info">
              <div class="streak-current">
                <span class="streak-number">{{ userStats?.currentStreak || 0 }}</span>
                <span class="streak-label">连续天数</span>
              </div>
              <div class="streak-best">
                <span>最佳记录: {{ userStats?.longestStreak || 0 }} 天</span>
              </div>
            </div>
          </ElCard>
        </ElCol>

        <!-- 右侧：个人信息编辑 -->
        <ElCol :lg="16" :md="24">
          <ElCard class="profile-edit-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <ElIcon><Edit /></ElIcon>
                <span>个人信息</span>
              </div>
            </template>

            <ElForm
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="120px"
              class="profile-form"
            >
              <!-- 基本信息 -->
              <div class="form-section">
                <h3 class="section-title">
                  基本信息
                </h3>

                <ElFormItem label="用户名">
                  <ElInput v-model="profileForm.username" disabled />
                </ElFormItem>

                <ElFormItem label="邮箱地址" prop="email">
                  <ElInput v-model="profileForm.email" />
                </ElFormItem>

                <ElFormItem label="真实姓名" prop="fullName">
                  <ElInput v-model="profileForm.fullName" placeholder="请输入真实姓名" />
                </ElFormItem>

                <ElFormItem label="个人简介" prop="bio">
                  <ElInput
                    v-model="profileForm.bio"
                    type="textarea"
                    :rows="4"
                    placeholder="介绍一下自己..."
                    maxlength="500"
                    show-word-limit
                  />
                </ElFormItem>
              </div>

              <!-- 技能信息 -->
              <div class="form-section">
                <h3 class="section-title">
                  技能信息
                </h3>

                <ElFormItem label="技能水平" prop="skillLevel">
                  <ElSelect v-model="profileForm.skillLevel" placeholder="请选择技能水平">
                    <ElOption label="初学者" value="BEGINNER" />
                    <ElOption label="中级" value="INTERMEDIATE" />
                    <ElOption label="高级" value="ADVANCED" />
                    <ElOption label="专家" value="EXPERT" />
                  </ElSelect>
                </ElFormItem>

                <ElFormItem label="学习目标" prop="learningGoals">
                  <ElInput
                    v-model="profileForm.learningGoals"
                    type="textarea"
                    :rows="3"
                    placeholder="描述您的学习目标..."
                    maxlength="1000"
                    show-word-limit
                  />
                </ElFormItem>

                <ElFormItem label="职业背景" prop="professionalBackground">
                  <ElInput v-model="profileForm.professionalBackground" placeholder="如：软件开发、网络安全等" />
                </ElFormItem>

                <ElFormItem label="工作经验" prop="yearsOfExperience">
                  <ElInputNumber
                    v-model="profileForm.yearsOfExperience"
                    :min="0"
                    :max="50"
                    placeholder="年"
                  />
                </ElFormItem>
              </div>

              <!-- 地理信息 -->
              <div class="form-section">
                <h3 class="section-title">
                  地理信息
                </h3>

                <ElFormItem label="国家" prop="country">
                  <ElInput v-model="profileForm.country" placeholder="如：中国" />
                </ElFormItem>

                <ElFormItem label="城市" prop="city">
                  <ElInput v-model="profileForm.city" placeholder="如：北京" />
                </ElFormItem>

                <ElFormItem label="时区" prop="timezone">
                  <ElSelect v-model="profileForm.timezone" placeholder="请选择时区">
                    <ElOption label="北京时间 (UTC+8)" value="Asia/Shanghai" />
                    <ElOption label="UTC" value="UTC" />
                    <ElOption label="纽约时间 (UTC-5)" value="America/New_York" />
                    <ElOption label="伦敦时间 (UTC+0)" value="Europe/London" />
                  </ElSelect>
                </ElFormItem>

                <ElFormItem label="首选语言" prop="preferredLanguage">
                  <ElSelect v-model="profileForm.preferredLanguage" placeholder="请选择语言">
                    <ElOption label="简体中文" value="zh-CN" />
                    <ElOption label="English" value="en-US" />
                    <ElOption label="日本語" value="ja-JP" />
                  </ElSelect>
                </ElFormItem>
              </div>

              <!-- 通知设置 -->
              <div class="form-section">
                <h3 class="section-title">
                  通知设置
                </h3>

                <ElFormItem label="邮件通知">
                  <ElSwitch
                    v-model="profileForm.emailNotifications"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                </ElFormItem>

                <ElFormItem label="学习提醒">
                  <ElSwitch
                    v-model="profileForm.learningReminders"
                    active-text="开启"
                    inactive-text="关闭"
                  />
                </ElFormItem>
              </div>

              <!-- 操作按钮 -->
              <ElFormItem>
                <ElButton
                  type="primary"
                  :loading="userStore.isUpdating"
                  @click="handleUpdateProfile"
                >
                  保存更改
                </ElButton>
                <ElButton @click="resetForm">
                  重置
                </ElButton>
                <ElButton type="warning" @click="showPasswordChange = true">
                  修改密码
                </ElButton>
              </ElFormItem>
            </ElForm>
          </ElCard>
        </ElCol>
      </ElRow>
    </div>

    <!-- 头像上传对话框 -->
    <ElDialog
      v-model="showAvatarUpload"
      title="更换头像"
      width="400px"
    >
      <div class="avatar-upload">
        <ElUpload
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeAvatarUpload"
          :http-request="handleAvatarUpload"
        >
          <div class="upload-content">
            <ElIcon class="upload-icon">
              <Plus />
            </ElIcon>
            <div class="upload-text">
              点击上传头像
            </div>
            <div class="upload-hint">
              支持 JPG、PNG 格式，文件大小不超过 2MB
            </div>
          </div>
        </ElUpload>
      </div>
    </ElDialog>

    <!-- 修改密码对话框 -->
    <ElDialog
      v-model="showPasswordChange"
      title="修改密码"
      width="400px"
    >
      <ElForm
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
      >
        <ElFormItem label="当前密码" prop="oldPassword">
          <ElInput
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入当前密码"
            show-password
          />
        </ElFormItem>

        <ElFormItem label="新密码" prop="newPassword">
          <ElInput
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </ElFormItem>

        <ElFormItem label="确认密码" prop="confirmPassword">
          <ElInput
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <span class="dialog-footer">
          <ElButton @click="showPasswordChange = false">取消</ElButton>
          <ElButton
            type="primary"
            :loading="userStore.isUpdating"
            @click="handlePasswordChange"
          >
            确认修改
          </ElButton>
        </span>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadRequestHandler, UploadRequestOptions } from 'element-plus'
import {
  UserFilled,
  Camera,
  DataLine,
  Medal,
  Calendar,
  Edit,
  Plus
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
import { useUserStore } from '@/stores/modules/user'
import { getUserStatusColor, formatStudyTime, calculateUserLevel } from '@/api/user'
import type { UserUpdateData, PasswordChangeData } from '@/api/user'
import dayjs from 'dayjs'

// ================================
// 组件状态
// ================================

const authStore = useAuthStore()
const userStore = useUserStore()

const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const showAvatarUpload = ref(false)
const showPasswordChange = ref(false)

// 个人信息表单
const profileForm = reactive<UserUpdateData & { username: string }>({
  username: '',
  email: '',
  fullName: '',
  bio: '',
  skillLevel: 'BEGINNER',
  learningGoals: '',
  professionalBackground: '',
  yearsOfExperience: 0,
  country: '',
  city: '',
  timezone: 'Asia/Shanghai',
  preferredLanguage: 'zh-CN',
  emailNotifications: true,
  learningReminders: true
})

// 密码修改表单
const passwordForm = reactive<PasswordChangeData>({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// ================================
// 计算属性
// ================================

const userStats = computed(() => authStore.userStats)

const userLevel = computed(() => {
  const points = userStats.value?.totalPoints || 0
  return calculateUserLevel(points)
})

// ================================
// 表单验证规则
// ================================

const profileRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  fullName: [
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  bio: [
    { max: 500, message: '个人简介不能超过500个字符', trigger: 'blur' }
  ],
  learningGoals: [
    { max: 1000, message: '学习目标不能超过1000个字符', trigger: 'blur' }
  ],
  professionalBackground: [
    { max: 50, message: '职业背景不能超过50个字符', trigger: 'blur' }
  ],
  country: [
    { max: 50, message: '国家名称不能超过50个字符', trigger: 'blur' }
  ],
  city: [
    { max: 50, message: '城市名称不能超过50个字符', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 32, message: '密码长度为8-32个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// ================================
// 事件处理
// ================================

/**
 * 初始化个人信息表单
 */
const initializeForm = () => {
  // 优先使用userStore的用户信息，因为它是最新获取的
  const userData = userStore.userInfo || authStore.user
  if (!userData) return

  profileForm.username = userData.username || ''
  profileForm.email = userData.email || ''
  profileForm.fullName = userData.fullName || ''
  profileForm.bio = userData.bio || ''

  const { profile } = userData
  if (profile) {
    profileForm.skillLevel = (profile.skillLevel as any) || 'BEGINNER'
    profileForm.learningGoals = profile.learningGoals || ''
    profileForm.professionalBackground = profile.professionalBackground || ''
    profileForm.yearsOfExperience = profile.yearsOfExperience || 0
    profileForm.country = profile.country || ''
    profileForm.city = profile.city || ''
    profileForm.timezone = profile.timezone || 'Asia/Shanghai'
    profileForm.preferredLanguage = profile.preferredLanguage || 'zh-CN'
    profileForm.emailNotifications = profile.emailNotifications !== false
    profileForm.learningReminders = profile.learningReminders !== false
  } else {
    // 如果没有profile，设置默认值
    profileForm.skillLevel = 'BEGINNER'
    profileForm.timezone = 'Asia/Shanghai'
    profileForm.preferredLanguage = 'zh-CN'
    profileForm.emailNotifications = true
    profileForm.learningReminders = true
  }
}

/**
 * 更新个人信息
 */
const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return

  try {
    const valid = await profileFormRef.value.validate()
    if (!valid) return

    const { username, ...updateData } = profileForm
    const success = await userStore.updateUserProfile(updateData)

    if (success) {
      ElMessage.success('个人信息更新成功')
      // 更新成功后，重新获取最新的用户信息并刷新表单
      await userStore.fetchCurrentUserProfile()
      // 同时更新authStore的用户信息
      if (userStore.userInfo) {
        authStore.updateUser(userStore.userInfo)
      }
      // 重新初始化表单以显示最新数据
      initializeForm()
    } else {
      // 处理失败情况
      ElMessage.error('个人信息更新失败，请检查输入信息')
    }
  } catch (error: any) {
    console.error('更新个人信息失败:', error)
    ElMessage.error(error.message || '更新个人信息失败')
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  initializeForm()
  profileFormRef.value?.clearValidate()
}

/**
 * 修改密码
 */
const handlePasswordChange = async () => {
  if (!passwordFormRef.value) return

  try {
    const valid = await passwordFormRef.value.validate()
    if (!valid) return

    const success = await userStore.updatePassword(passwordForm)

    if (success) {
      ElMessage.success('密码修改成功')
      showPasswordChange.value = false
      Object.assign(passwordForm, {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      })
      passwordFormRef.value?.clearValidate()
    } else {
      ElMessage.error('密码修改失败，请检查输入信息')
    }
  } catch (error: any) {
    console.error('密码修改失败:', error)
    ElMessage.error(error.message || '密码修改失败')
  }
}

/**
 * 头像上传前检查
 */
const beforeAvatarUpload = (rawFile: File) => {
  const isValidType = ['image/jpeg', 'image/png'].includes(rawFile.type)
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isValidType) {
    ElMessage.error('头像图片只能是 JPG 或 PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

/**
 * 处理头像上传
 */
const handleAvatarUpload: UploadRequestHandler = async (options: UploadRequestOptions) => {
  try {
    const { uploadAvatar } = await import('@/api/user')
    const response = await uploadAvatar(options.file)

    if (response.code === 200 && response.data) {
      ElMessage.success('头像上传成功')
      showAvatarUpload.value = false

      // 更新用户信息
      await userStore.fetchCurrentUserProfile()
      if (userStore.userInfo) {
        authStore.updateUser(userStore.userInfo)
      }

      // 重新初始化表单
      initializeForm()

      options.onSuccess?.(response.data)
    } else {
      ElMessage.error(response.message || '头像上传失败')
      const error = new Error(response.message || '头像上传失败') as any
      error.status = 400
      error.method = 'POST'
      error.url = '/api/v1/users/avatar'
      options.onError?.(error)
    }
  } catch (error: any) {
    console.error('头像上传失败:', error)
    ElMessage.error(error.message || '头像上传失败')
    const uploadError = error as any
    if (!uploadError.status) {
      uploadError.status = 500
      uploadError.method = 'POST'
      uploadError.url = '/api/v1/users/avatar'
    }
    options.onError?.(uploadError)
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateString?: string) => {
  if (!dateString) return '未知'
  return dayjs(dateString).format('YYYY年MM月DD日')
}

// ================================
// 生命周期
// ================================

onMounted(async () => {
  // 获取最新的用户信息
  await userStore.fetchCurrentUserProfile()

  // 初始化表单
  initializeForm()
})
</script>

<style scoped lang="scss">
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 24px;
  color: white;

  .header-content {
    display: flex;
    align-items: center;
    gap: 24px;

    .user-avatar {
      position: relative;

      .avatar-upload-btn {
        position: absolute;
        bottom: -5px;
        right: -5px;
        background: #409eff;
        border: 2px solid white;
      }
    }

    .user-info {
      flex: 1;

      .username {
        margin: 0 0 8px 0;
        font-size: 28px;
        font-weight: 600;
      }

      .user-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 16px;

        .join-date {
          opacity: 0.9;
          font-size: 14px;
        }
      }
    }
  }
}

.profile-content {
  .el-col {
    margin-bottom: 24px;
  }
}

// 统计卡片
.stats-card {
  margin-bottom: 24px;

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    .stat-item {
      text-align: center;
      padding: 16px;
      background: #f8f9fa;
      border-radius: 8px;

      .stat-value {
        font-size: 24px;
        font-weight: 600;
        color: #409eff;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 12px;
        color: #666;
      }
    }
  }
}

// 等级卡片
.level-card {
  margin-bottom: 24px;

  .level-info {
    .current-level {
      margin-bottom: 16px;

      .level-text {
        display: block;
        font-size: 18px;
        font-weight: 600;
        color: #409eff;
        margin-bottom: 8px;
      }

      .level-progress {
        margin-bottom: 8px;
      }
    }

    .level-details {
      font-size: 14px;
      color: #666;
    }
  }
}

// 连续天数卡片
.streak-card {
  .streak-info {
    text-align: center;

    .streak-current {
      margin-bottom: 16px;

      .streak-number {
        display: block;
        font-size: 36px;
        font-weight: 600;
        color: #67c23a;
        line-height: 1;
      }

      .streak-label {
        font-size: 14px;
        color: #666;
      }
    }

    .streak-best {
      font-size: 14px;
      color: #409eff;
    }
  }
}

// 个人信息编辑卡片
.profile-edit-card {
  .profile-form {
    .form-section {
      margin-bottom: 32px;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 16px 0;
        padding-bottom: 8px;
        border-bottom: 2px solid #409eff;
      }
    }
  }
}

// 卡片头部样式
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

// 头像上传
.avatar-upload {
  text-align: center;

  .avatar-uploader {
    :deep(.el-upload) {
      border: 2px dashed #d9d9d9;
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }

    .upload-content {
      padding: 40px 20px;

      .upload-icon {
        font-size: 28px;
        color: #8c939d;
        margin-bottom: 16px;
      }

      .upload-text {
        color: #606266;
        font-size: 14px;
        margin-bottom: 8px;
      }

      .upload-hint {
        color: #8c939d;
        font-size: 12px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .profile-container {
    padding: 16px;
  }

  .profile-header {
    padding: 20px;

    .header-content {
      flex-direction: column;
      text-align: center;
      gap: 16px;
    }
  }

  .stats-grid {
    grid-template-columns: 1fr !important;
  }

  .profile-form {
    :deep(.el-form-item__label) {
      text-align: left !important;
    }
  }
}
</style>
