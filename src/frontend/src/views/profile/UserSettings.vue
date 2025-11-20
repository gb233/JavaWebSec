<template>
  <div class="user-settings">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <ElIcon class="title-icon">
            <Setting />
          </ElIcon>
          个人设置
        </h1>
        <p class="page-description">
          管理您的账户设置和偏好
        </p>
      </div>
    </div>

    <ElRow :gutter="24">
      <!-- 左侧导航 -->
      <ElCol :span="6">
        <ElCard class="nav-card">
          <ElMenu
            :default-active="activeTab"
            class="settings-menu"
            @select="handleTabChange"
          >
            <ElMenuItem index="profile">
              <ElIcon><User /></ElIcon>
              <span>基本信息</span>
            </ElMenuItem>
            <ElMenuItem index="security">
              <ElIcon><Lock /></ElIcon>
              <span>安全设置</span>
            </ElMenuItem>
            <ElMenuItem index="preferences">
              <ElIcon><Setting /></ElIcon>
              <span>学习偏好</span>
            </ElMenuItem>
            <ElMenuItem index="notifications">
              <ElIcon><Bell /></ElIcon>
              <span>通知设置</span>
            </ElMenuItem>
            <ElMenuItem index="privacy">
              <ElIcon><View /></ElIcon>
              <span>隐私设置</span>
            </ElMenuItem>
          </ElMenu>
        </ElCard>
      </ElCol>

      <!-- 右侧内容 -->
      <ElCol :span="18">
        <!-- 基本信息 -->
        <ElCard v-if="activeTab === 'profile'" class="content-card">
          <template #header>
            <span>基本信息</span>
          </template>

          <ElForm
            ref="profileFormRef"
            :model="profileForm"
            :rules="profileRules"
            label-width="100px"
          >
            <ElFormItem label="头像">
              <div class="avatar-upload">
                <ElAvatar :size="80" :src="profileForm.avatarUrl">
                  {{ profileForm.username?.charAt(0).toUpperCase() }}
                </ElAvatar>
                <ElButton text @click="uploadAvatar">
                  更换头像
                </ElButton>
              </div>
            </ElFormItem>

            <ElFormItem label="用户名" prop="username">
              <ElInput v-model="profileForm.username" disabled />
            </ElFormItem>

            <ElFormItem label="邮箱" prop="email">
              <ElInput v-model="profileForm.email" />
            </ElFormItem>

            <ElFormItem label="真实姓名" prop="fullName">
              <ElInput v-model="profileForm.fullName" />
            </ElFormItem>

            <ElFormItem label="个人简介">
              <ElInput
                v-model="profileForm.bio"
                type="textarea"
                :rows="3"
                placeholder="介绍一下自己..."
              />
            </ElFormItem>

            <ElFormItem>
              <ElButton type="primary" :loading="updating" @click="updateProfile">
                保存修改
              </ElButton>
              <ElButton @click="resetProfile">
                重置
              </ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>

        <!-- 安全设置 -->
        <ElCard v-if="activeTab === 'security'" class="content-card">
          <template #header>
            <span>安全设置</span>
          </template>

          <div class="security-sections">
            <!-- 修改密码 -->
            <div class="security-section">
              <h3>修改密码</h3>
              <ElForm
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="120px"
              >
                <ElFormItem label="当前密码" prop="currentPassword">
                  <ElInput v-model="passwordForm.currentPassword" type="password" show-password />
                </ElFormItem>
                <ElFormItem label="新密码" prop="newPassword">
                  <ElInput v-model="passwordForm.newPassword" type="password" show-password />
                </ElFormItem>
                <ElFormItem label="确认密码" prop="confirmPassword">
                  <ElInput v-model="passwordForm.confirmPassword" type="password" show-password />
                </ElFormItem>
                <ElFormItem>
                  <ElButton type="primary" :loading="updatingPassword" @click="updatePassword">
                    修改密码
                  </ElButton>
                </ElFormItem>
              </ElForm>
            </div>

            <!-- 登录记录 -->
            <div class="security-section">
              <h3>登录记录</h3>
              <ElTable :data="loginRecords" style="width: 100%">
                <ElTableColumn prop="ipAddress" label="IP地址" />
                <ElTableColumn prop="location" label="登录地点" />
                <ElTableColumn prop="device" label="设备信息" />
                <ElTableColumn prop="loginTime" label="登录时间" />
                <ElTableColumn prop="status" label="状态">
                  <template #default="{ row }">
                    <ElTag :type="row.status === 'success' ? 'success' : 'danger'">
                      {{ row.status === 'success' ? '成功' : '失败' }}
                    </ElTag>
                  </template>
                </ElTableColumn>
              </ElTable>
            </div>
          </div>
        </ElCard>

        <!-- 学习偏好 -->
        <ElCard v-if="activeTab === 'preferences'" class="content-card">
          <template #header>
            <span>学习偏好</span>
          </template>

          <ElForm :model="preferencesForm" label-width="120px">
            <ElFormItem label="学习目标">
              <ElSelect v-model="preferencesForm.learningGoal" placeholder="选择学习目标">
                <ElOption label="基础安全知识" value="basic" />
                <ElOption label="Web安全专家" value="web_security" />
                <ElOption label="渗透测试工程师" value="penetration_testing" />
                <ElOption label="安全架构师" value="security_architect" />
              </ElSelect>
            </ElFormItem>

            <ElFormItem label="难度偏好">
              <ElRadioGroup v-model="preferencesForm.difficultyPreference">
                <ElRadio label="easy">
                  简单
                </ElRadio>
                <ElRadio label="medium">
                  中等
                </ElRadio>
                <ElRadio label="hard">
                  困难
                </ElRadio>
                <ElRadio label="mixed">
                  混合
                </ElRadio>
              </ElRadioGroup>
            </ElFormItem>

            <ElFormItem label="学习时间">
              <ElTimePicker
                v-model="preferencesForm.studyTime"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                placeholder="选择学习时间段"
              />
            </ElFormItem>

            <ElFormItem label="学习提醒">
              <ElSwitch v-model="preferencesForm.studyReminder" />
              <span class="form-tip">开启后会在学习时间提醒您</span>
            </ElFormItem>

            <ElFormItem>
              <ElButton type="primary" :loading="updating" @click="updatePreferences">
                保存设置
              </ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>

        <!-- 通知设置 -->
        <ElCard v-if="activeTab === 'notifications'" class="content-card">
          <template #header>
            <span>通知设置</span>
          </template>

          <ElForm :model="notificationForm" label-width="120px">
            <ElFormItem label="邮件通知">
              <ElSwitch v-model="notificationForm.emailEnabled" />
            </ElFormItem>

            <ElFormItem label="学习进度通知">
              <ElSwitch v-model="notificationForm.progressNotification" />
            </ElFormItem>

            <ElFormItem label="测试结果通知">
              <ElSwitch v-model="notificationForm.testResultNotification" />
            </ElFormItem>

            <ElFormItem label="挑战完成通知">
              <ElSwitch v-model="notificationForm.challengeNotification" />
            </ElFormItem>

            <ElFormItem label="系统公告">
              <ElSwitch v-model="notificationForm.systemAnnouncement" />
            </ElFormItem>

            <ElFormItem>
              <ElButton type="primary" :loading="updating" @click="updateNotifications">
                保存设置
              </ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>

        <!-- 隐私设置 -->
        <ElCard v-if="activeTab === 'privacy'" class="content-card">
          <template #header>
            <span>隐私设置</span>
          </template>

          <ElForm :model="privacyForm" label-width="120px">
            <ElFormItem label="公开学习进度">
              <ElSwitch v-model="privacyForm.publicProgress" />
              <span class="form-tip">允许其他用户查看您的学习进度</span>
            </ElFormItem>

            <ElFormItem label="公开测试成绩">
              <ElSwitch v-model="privacyForm.publicTestScores" />
              <span class="form-tip">允许其他用户查看您的测试成绩</span>
            </ElFormItem>

            <ElFormItem label="公开挑战记录">
              <ElSwitch v-model="privacyForm.publicChallenges" />
              <span class="form-tip">允许其他用户查看您的挑战记录</span>
            </ElFormItem>

            <ElFormItem label="数据收集">
              <ElSwitch v-model="privacyForm.dataCollection" />
              <span class="form-tip">允许系统收集匿名使用数据以改进服务</span>
            </ElFormItem>

            <ElFormItem>
              <ElButton type="primary" :loading="updating" @click="updatePrivacy">
                保存设置
              </ElButton>
            </ElFormItem>
          </ElForm>
        </ElCard>
      </ElCol>
    </ElRow>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Setting, User, Lock, Bell, View } from '@element-plus/icons-vue'

// 响应式数据
const activeTab = ref('profile')
const updating = ref(false)
const updatingPassword = ref(false)

// 表单数据
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const profileForm = reactive({
  username: 'security_learner',
  email: 'learner@example.com',
  fullName: '安全学习者',
  bio: '专注于Web安全学习',
  avatarUrl: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const preferencesForm = reactive({
  learningGoal: 'web_security',
  difficultyPreference: 'medium',
  studyTime: null,
  studyReminder: true
})

const notificationForm = reactive({
  emailEnabled: true,
  progressNotification: true,
  testResultNotification: true,
  challengeNotification: true,
  systemAnnouncement: false
})

const privacyForm = reactive({
  publicProgress: true,
  publicTestScores: false,
  publicChallenges: true,
  dataCollection: true
})

// 登录记录
const loginRecords = ref([
  {
    ipAddress: '192.168.1.100',
    location: '北京市',
    device: 'Chrome/Windows',
    loginTime: '2024-01-15 14:30:25',
    status: 'success'
  },
  {
    ipAddress: '192.168.1.101',
    location: '上海市',
    device: 'Safari/macOS',
    loginTime: '2024-01-14 09:15:10',
    status: 'success'
  }
])

// 表单验证规则
const profileRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  fullName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 生命周期
onMounted(() => {
  loadUserSettings()
})

// 加载用户设置
const loadUserSettings = async () => {
  try {
    // 这里应该调用API获取用户设置
    // 暂时使用模拟数据
  } catch (error) {
    console.error('加载用户设置失败:', error)
    ElMessage.error('加载用户设置失败')
  }
}

// 切换标签页
const handleTabChange = (tab: string) => {
  activeTab.value = tab
}

// 上传头像
const uploadAvatar = () => {
  ElMessage.info('头像上传功能待开发')
}

// 更新个人信息
const updateProfile = async () => {
  try {
    updating.value = true
    // 这里应该调用API更新个人信息
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('更新个人信息失败')
  } finally {
    updating.value = false
  }
}

// 重置个人信息
const resetProfile = () => {
  // 重置表单数据
  ElMessage.info('表单已重置')
}

// 更新密码
const updatePassword = async () => {
  try {
    updatingPassword.value = true
    // 这里应该调用API更新密码
    ElMessage.success('密码修改成功')
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败')
  } finally {
    updatingPassword.value = false
  }
}

// 更新学习偏好
const updatePreferences = async () => {
  try {
    updating.value = true
    // 这里应该调用API更新学习偏好
    ElMessage.success('学习偏好更新成功')
  } catch (error) {
    console.error('更新学习偏好失败:', error)
    ElMessage.error('更新学习偏好失败')
  } finally {
    updating.value = false
  }
}

// 更新通知设置
const updateNotifications = async () => {
  try {
    updating.value = true
    // 这里应该调用API更新通知设置
    ElMessage.success('通知设置更新成功')
  } catch (error) {
    console.error('更新通知设置失败:', error)
    ElMessage.error('更新通知设置失败')
  } finally {
    updating.value = false
  }
}

// 更新隐私设置
const updatePrivacy = async () => {
  try {
    updating.value = true
    // 这里应该调用API更新隐私设置
    ElMessage.success('隐私设置更新成功')
  } catch (error) {
    console.error('更新隐私设置失败:', error)
    ElMessage.error('更新隐私设置失败')
  } finally {
    updating.value = false
  }
}
</script>

<style lang="scss" scoped>
.user-settings {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .page-header {
    margin-bottom: 32px;

    .header-content {
      text-align: center;

      .page-title {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 32px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 8px;

        .title-icon {
          margin-right: 12px;
          color: #3b82f6;
        }
      }

      .page-description {
        font-size: 16px;
        color: #6b7280;
        margin: 0;
      }
    }
  }

  .nav-card {
    .settings-menu {
      border: none;
    }
  }

  .content-card {
    .avatar-upload {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .security-sections {
      .security-section {
        margin-bottom: 32px;

        h3 {
          font-size: 16px;
          font-weight: 600;
          color: #1f2937;
          margin-bottom: 16px;
        }
      }
    }

    .form-tip {
      font-size: 12px;
      color: #6b7280;
      margin-left: 8px;
    }
  }
}
</style>
