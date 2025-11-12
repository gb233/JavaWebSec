<template>
  <div class="user-settings">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon"><Setting /></el-icon>
          个人设置
        </h1>
        <p class="page-description">管理您的账户设置和偏好</p>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 左侧导航 -->
      <el-col :span="6">
        <el-card class="nav-card">
          <el-menu
            :default-active="activeTab"
            @select="handleTabChange"
            class="settings-menu"
          >
            <el-menu-item index="profile">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
            <el-menu-item index="preferences">
              <el-icon><Setting /></el-icon>
              <span>学习偏好</span>
            </el-menu-item>
            <el-menu-item index="notifications">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </el-menu-item>
            <el-menu-item index="privacy">
              <el-icon><View /></el-icon>
              <span>隐私设置</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容 -->
      <el-col :span="18">
        <!-- 基本信息 -->
        <el-card v-if="activeTab === 'profile'" class="content-card">
          <template #header>
            <span>基本信息</span>
          </template>
          
          <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-width="100px">
            <el-form-item label="头像">
              <div class="avatar-upload">
                <el-avatar :size="80" :src="profileForm.avatarUrl">
                  {{ profileForm.username?.charAt(0).toUpperCase() }}
                </el-avatar>
                <el-button text @click="uploadAvatar">更换头像</el-button>
              </div>
            </el-form-item>
            
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
            
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" />
            </el-form-item>
            
            <el-form-item label="真实姓名" prop="fullName">
              <el-input v-model="profileForm.fullName" />
            </el-form-item>
            
            <el-form-item label="个人简介">
              <el-input
                v-model="profileForm.bio"
                type="textarea"
                :rows="3"
                placeholder="介绍一下自己..."
              />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="updateProfile" :loading="updating">
                保存修改
              </el-button>
              <el-button @click="resetProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 安全设置 -->
        <el-card v-if="activeTab === 'security'" class="content-card">
          <template #header>
            <span>安全设置</span>
          </template>
          
          <div class="security-sections">
            <!-- 修改密码 -->
            <div class="security-section">
              <h3>修改密码</h3>
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px">
                <el-form-item label="当前密码" prop="currentPassword">
                  <el-input v-model="passwordForm.currentPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updatePassword" :loading="updatingPassword">
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>

            <!-- 登录记录 -->
            <div class="security-section">
              <h3>登录记录</h3>
              <el-table :data="loginRecords" style="width: 100%">
                <el-table-column prop="ipAddress" label="IP地址" />
                <el-table-column prop="location" label="登录地点" />
                <el-table-column prop="device" label="设备信息" />
                <el-table-column prop="loginTime" label="登录时间" />
                <el-table-column prop="status" label="状态">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
                      {{ row.status === 'success' ? '成功' : '失败' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-card>

        <!-- 学习偏好 -->
        <el-card v-if="activeTab === 'preferences'" class="content-card">
          <template #header>
            <span>学习偏好</span>
          </template>
          
          <el-form :model="preferencesForm" label-width="120px">
            <el-form-item label="学习目标">
              <el-select v-model="preferencesForm.learningGoal" placeholder="选择学习目标">
                <el-option label="基础安全知识" value="basic" />
                <el-option label="Web安全专家" value="web_security" />
                <el-option label="渗透测试工程师" value="penetration_testing" />
                <el-option label="安全架构师" value="security_architect" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="难度偏好">
              <el-radio-group v-model="preferencesForm.difficultyPreference">
                <el-radio label="easy">简单</el-radio>
                <el-radio label="medium">中等</el-radio>
                <el-radio label="hard">困难</el-radio>
                <el-radio label="mixed">混合</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="学习时间">
              <el-time-picker
                v-model="preferencesForm.studyTime"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                placeholder="选择学习时间段"
              />
            </el-form-item>
            
            <el-form-item label="学习提醒">
              <el-switch v-model="preferencesForm.studyReminder" />
              <span class="form-tip">开启后会在学习时间提醒您</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="updatePreferences" :loading="updating">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 通知设置 -->
        <el-card v-if="activeTab === 'notifications'" class="content-card">
          <template #header>
            <span>通知设置</span>
          </template>
          
          <el-form :model="notificationForm" label-width="120px">
            <el-form-item label="邮件通知">
              <el-switch v-model="notificationForm.emailEnabled" />
            </el-form-item>
            
            <el-form-item label="学习进度通知">
              <el-switch v-model="notificationForm.progressNotification" />
            </el-form-item>
            
            <el-form-item label="测试结果通知">
              <el-switch v-model="notificationForm.testResultNotification" />
            </el-form-item>
            
            <el-form-item label="挑战完成通知">
              <el-switch v-model="notificationForm.challengeNotification" />
            </el-form-item>
            
            <el-form-item label="系统公告">
              <el-switch v-model="notificationForm.systemAnnouncement" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="updateNotifications" :loading="updating">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 隐私设置 -->
        <el-card v-if="activeTab === 'privacy'" class="content-card">
          <template #header>
            <span>隐私设置</span>
          </template>
          
          <el-form :model="privacyForm" label-width="120px">
            <el-form-item label="公开学习进度">
              <el-switch v-model="privacyForm.publicProgress" />
              <span class="form-tip">允许其他用户查看您的学习进度</span>
            </el-form-item>
            
            <el-form-item label="公开测试成绩">
              <el-switch v-model="privacyForm.publicTestScores" />
              <span class="form-tip">允许其他用户查看您的测试成绩</span>
            </el-form-item>
            
            <el-form-item label="公开挑战记录">
              <el-switch v-model="privacyForm.publicChallenges" />
              <span class="form-tip">允许其他用户查看您的挑战记录</span>
            </el-form-item>
            
            <el-form-item label="数据收集">
              <el-switch v-model="privacyForm.dataCollection" />
              <span class="form-tip">允许系统收集匿名使用数据以改进服务</span>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="updatePrivacy" :loading="updating">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
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
