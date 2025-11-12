<template>
  <div class="home-container">
    <!-- 主要内容区域 -->
    <div class="welcome-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="title">
            JavaWeb安全教学系统
          </h1>
          <p class="subtitle">
            专业的Web安全学习平台，基于OWASP Top 10漏洞清单
          </p>
          <p class="description">
            通过互动式学习、实际演示和挑战练习，掌握Web应用安全核心知识
          </p>

          <div class="action-buttons">
            <template v-if="!authStore.isLoggedIn">
              <ElButton type="primary" size="large" @click="$router.push('/register')">
                立即开始学习
              </ElButton>
              <ElButton size="large" @click="$router.push('/login')">
                已有账户，登录
              </ElButton>
            </template>
            <template v-else>
              <ElButton type="primary" size="large" @click="$router.push('/dashboard')">
                进入控制台
              </ElButton>
              <ElButton size="large" @click="$router.push('/learning/vulnerabilities')">
                开始学习
              </ElButton>
            </template>
          </div>
        </div>

        <div class="hero-image">
          <ElIcon :size="200" color="#409eff">
            <Lock />
          </ElIcon>
        </div>
      </div>
    </div>

    <!-- 功能特色介绍 -->
    <div class="features-section">
      <div class="section-header">
        <h2>核心功能特色</h2>
        <p>全面的Web安全学习体验</p>
      </div>

      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <ElIcon :size="40" color="#409eff">
              <Reading />
            </ElIcon>
          </div>
          <h3>漏洞学习</h3>
          <p>系统学习OWASP Top 10常见漏洞，从原理到防护一应俱全</p>
          <ul>
            <li>详细的漏洞原理解析</li>
            <li>真实案例分析</li>
            <li>防护方案指导</li>
          </ul>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <ElIcon :size="40" color="#67c23a">
              <Monitor />
            </ElIcon>
          </div>
          <h3>实际演示</h3>
          <p>交互式漏洞演示环境，体验真实的攻击和防护过程</p>
          <ul>
            <li>安全的演示环境</li>
            <li>实时攻击模拟</li>
            <li>代码对比分析</li>
          </ul>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <ElIcon :size="40" color="#e6a23c">
              <EditPen />
            </ElIcon>
          </div>
          <h3>知识测试</h3>
          <p>丰富的题库系统，检验学习成果，巩固安全知识</p>
          <ul>
            <li>分级测试题目</li>
            <li>即时反馈评估</li>
            <li>学习进度跟踪</li>
          </ul>
        </div>

        <div class="feature-card">
          <div class="feature-icon">
            <ElIcon :size="40" color="#f56c6c">
              <Trophy />
            </ElIcon>
          </div>
          <h3>挑战模式</h3>
          <p>综合实战挑战，提升实际应用安全防护能力</p>
          <ul>
            <li>综合场景模拟</li>
            <li>多样化挑战任务</li>
            <li>排行榜竞技</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 学习路径 -->
    <div class="learning-path-section">
      <div class="section-header">
        <h2>推荐学习路径</h2>
        <p>循序渐进，系统掌握Web安全</p>
      </div>

      <div class="learning-steps">
        <div class="step-item">
          <div class="step-number">
            1
          </div>
          <div class="step-content">
            <h3>基础学习</h3>
            <p>学习Web安全基础概念和OWASP Top 10漏洞分类</p>
          </div>
        </div>

        <div class="step-arrow">
          <ElIcon><ArrowRight /></ElIcon>
        </div>

        <div class="step-item">
          <div class="step-number">
            2
          </div>
          <div class="step-content">
            <h3>实践演示</h3>
            <p>通过交互式演示深入理解漏洞原理和攻击方式</p>
          </div>
        </div>

        <div class="step-arrow">
          <ElIcon><ArrowRight /></ElIcon>
        </div>

        <div class="step-item">
          <div class="step-number">
            3
          </div>
          <div class="step-content">
            <h3>知识测试</h3>
            <p>通过测试验证学习效果，发现知识盲点</p>
          </div>
        </div>

        <div class="step-arrow">
          <ElIcon><ArrowRight /></ElIcon>
        </div>

        <div class="step-item">
          <div class="step-number">
            4
          </div>
          <div class="step-content">
            <h3>综合挑战</h3>
            <p>在实战挑战中应用所学知识，提升实践能力</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计数据 -->
    <div class="stats-section">
      <div class="stats-container">
        <div class="stat-item">
          <div class="stat-number">
            {{ userStats?.totalUsers || 0 }}+
          </div>
          <div class="stat-label">
            注册用户
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-number">
            10
          </div>
          <div class="stat-label">
            OWASP漏洞
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-number">
            50+
          </div>
          <div class="stat-label">
            演示案例
          </div>
        </div>
        <div class="stat-item">
          <div class="stat-number">
            100+
          </div>
          <div class="stat-label">
            测试题目
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Lock,
  Reading,
  Monitor,
  EditPen,
  Trophy,
  ArrowRight
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
import { useUserStore } from '@/stores/modules/user'

// ================================
// 组件状态
// ================================

const authStore = useAuthStore()
const userStore = useUserStore()

const userStats = ref<any>(null)

// ================================
// 生命周期
// ================================

onMounted(async () => {
  // 获取用户统计数据
  try {
    await userStore.fetchUserStats()
    userStats.value = userStore.userStats
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})
</script>

<style scoped lang="scss">
.home-container {
  min-height: 100vh;
}

// 欢迎区域
.welcome-section {
  min-height: 80vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: white;

  .hero-content {
    display: flex;
    align-items: center;
    max-width: 1200px;
    width: 100%;
    gap: 60px;

    .hero-text {
      flex: 1;

      .title {
        font-size: 48px;
        font-weight: 700;
        margin: 0 0 20px 0;
        line-height: 1.2;
      }

      .subtitle {
        font-size: 24px;
        font-weight: 400;
        margin: 0 0 16px 0;
        opacity: 0.95;
      }

      .description {
        font-size: 16px;
        line-height: 1.6;
        margin: 0 0 40px 0;
        opacity: 0.9;
      }

      .action-buttons {
        display: flex;
        gap: 16px;
        flex-wrap: wrap;
      }
    }

    .hero-image {
      flex: 0 0 auto;
      text-align: center;
      opacity: 0.8;
    }
  }
}

// 功能特色区域
.features-section {
  padding: 80px 20px;
  background: #f8f9fa;

  .section-header {
    text-align: center;
    margin-bottom: 60px;

    h2 {
      font-size: 36px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 16px 0;
    }

    p {
      font-size: 18px;
      color: #606266;
      margin: 0;
    }
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 40px;
    max-width: 1200px;
    margin: 0 auto;

    .feature-card {
      background: white;
      padding: 40px 30px;
      border-radius: 12px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      text-align: center;
      transition: transform 0.3s ease, box-shadow 0.3s ease;

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
      }

      .feature-icon {
        margin-bottom: 24px;
      }

      h3 {
        font-size: 20px;
        font-weight: 600;
        color: #303133;
        margin: 0 0 16px 0;
      }

      p {
        font-size: 14px;
        color: #606266;
        line-height: 1.6;
        margin: 0 0 20px 0;
      }

      ul {
        text-align: left;
        padding: 0;
        margin: 0;
        list-style: none;

        li {
          font-size: 13px;
          color: #8c939d;
          padding: 4px 0;
          position: relative;
          padding-left: 16px;

          &::before {
            content: '•';
            color: #409eff;
            position: absolute;
            left: 0;
          }
        }
      }
    }
  }
}

// 学习路径区域
.learning-path-section {
  padding: 80px 20px;
  background: white;

  .section-header {
    text-align: center;
    margin-bottom: 60px;

    h2 {
      font-size: 36px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 16px 0;
    }

    p {
      font-size: 18px;
      color: #606266;
      margin: 0;
    }
  }

  .learning-steps {
    display: flex;
    align-items: center;
    justify-content: center;
    max-width: 1200px;
    margin: 0 auto;
    flex-wrap: wrap;
    gap: 20px;

    .step-item {
      text-align: center;
      max-width: 200px;

      .step-number {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background: #409eff;
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        font-weight: 600;
        margin: 0 auto 16px auto;
      }

      .step-content {
        h3 {
          font-size: 18px;
          font-weight: 600;
          color: #303133;
          margin: 0 0 8px 0;
        }

        p {
          font-size: 14px;
          color: #606266;
          line-height: 1.5;
          margin: 0;
        }
      }
    }

    .step-arrow {
      color: #409eff;
      font-size: 20px;
      margin: 0 20px;
    }
  }
}

// 统计数据区域
.stats-section {
  padding: 60px 20px;
  background: linear-gradient(135deg, #409eff 0%, #36a3f7 100%);
  color: white;

  .stats-container {
    display: flex;
    justify-content: space-around;
    align-items: center;
    max-width: 800px;
    margin: 0 auto;
    flex-wrap: wrap;
    gap: 40px;

    .stat-item {
      text-align: center;

      .stat-number {
        font-size: 48px;
        font-weight: 700;
        line-height: 1;
        margin-bottom: 8px;
      }

      .stat-label {
        font-size: 16px;
        opacity: 0.9;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .welcome-section {
    padding: 40px 20px;

    .hero-content {
      flex-direction: column;
      text-align: center;
      gap: 40px;

      .hero-text {
        .title {
          font-size: 36px;
        }

        .subtitle {
          font-size: 20px;
        }
      }

      .hero-image {
        .el-icon {
          font-size: 120px !important;
        }
      }
    }
  }

  .features-section,
  .learning-path-section {
    padding: 60px 20px;

    .section-header {
      h2 {
        font-size: 28px;
      }

      p {
        font-size: 16px;
      }
    }
  }

  .learning-steps {
    flex-direction: column;

    .step-arrow {
      transform: rotate(90deg);
      margin: 10px 0;
    }
  }

  .stats-container {
    gap: 30px;

    .stat-item {
      .stat-number {
        font-size: 36px;
      }

      .stat-label {
        font-size: 14px;
      }
    }
  }
}
</style>
