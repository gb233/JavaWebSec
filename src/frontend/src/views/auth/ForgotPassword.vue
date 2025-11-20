<!-- 忘记密码功能暂时注释掉 - 2025-01-15 -->
<!-- <template>
  <div class="forgot-password-container">
    <div class="forgot-password-form-wrapper">
      <!-- 页面头部 -->
      <div class="form-header">
        <div class="logo">
          <ElIcon :size="40" class="logo-icon">
            <Lock />
          </ElIcon>
        </div>

        <h1 class="title">
          找回密码
        </h1>

        <p class="subtitle">
          请输入您的邮箱地址，我们将发送重置密码的链接给您
        </p>
      </div>

      <!-- 表单区域 -->
      <ElCard class="form-card" shadow="always">
        <ElForm
          ref="formRef"
          :model="form"
          :rules="rules"
          class="forgot-password-form"
        >
          <ElFormItem prop="email">
            <ElInput
              v-model="form.email"
              placeholder="请输入注册邮箱"
              prefix-icon="Message"
              size="large"
              clearable
              @keyup.enter="handleSubmit"
            />
          </ElFormItem>

          <ElFormItem>
            <ElButton
              type="primary"
              size="large"
              class="submit-btn"
              :loading="isLoading"
              @click="handleSubmit"
            >
              {{ isLoading ? '发送中...' : '发送重置邮件' }}
            </ElButton>
          </ElFormItem>

          <div class="form-links">
            <RouterLink to="/login" class="back-link">
              <ElIcon><ArrowLeft /></ElIcon>
              返回登录
            </RouterLink>
          </div>
        </ElForm>
      </ElCard>
    </div>
  </div>
</template> -->

<!-- <script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Lock, Message, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref<FormInstance>()
const isLoading = ref(false)

const form = reactive({
  email: ''
})

const rules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    isLoading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))

    ElMessage.success('重置密码邮件已发送，请查收邮箱')
    router.push('/login')
  } catch (error) {
    ElMessage.error('发送失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.forgot-password-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.forgot-password-form-wrapper {
  width: 100%;
  max-width: 400px;
}

.form-header {
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
    line-height: 1.5;
  }
}

.form-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.forgot-password-form {
  padding: 20px 0;

  .submit-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 8px;
  }

  .form-links {
    text-align: center;
    margin-top: 16px;

    .back-link {
      color: #409eff;
      text-decoration: none;
      font-weight: 500;
      display: inline-flex;
      align-items: center;
      gap: 4px;

      &:hover {
        color: #36a3f7;
      }
    }
  }
}

// 响应式设计
@media (max-width: 480px) {
  .forgot-password-container {
    padding: 16px;
  }

  .form-header {
    .title {
      font-size: 24px;
    }

    .subtitle {
      font-size: 12px;
    }
  }
}
</style> -->
