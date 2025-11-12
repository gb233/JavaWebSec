<template>
  <div class="challenge-arena">
    <div class="arena-header">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/challenge/list' }">挑战列表</el-breadcrumb-item>
        <el-breadcrumb-item>{{ challenge?.title || '挑战环境' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div v-if="loading" class="loading">
      <el-skeleton :rows="5" animated />
    </div>

    <div v-else-if="challenge" class="arena-content">
      <!-- 挑战目标说明 -->
      <el-card class="challenge-objective">
        <template #header>
          <div class="card-header">
            <span>🎯 挑战目标</span>
          </div>
        </template>
        <div class="objective-content">
          <p><strong>任务描述：</strong>{{ challenge.description }}</p>
          <div class="objective-steps">
            <h4>📋 挑战步骤：</h4>
            <ol>
              <li v-for="(step, index) in challengeSteps" :key="index" 
                  :class="{ 'completed': index < currentStepIndex, 'current': index === currentStepIndex }">
                <strong>步骤 {{ index + 1 }}：</strong>{{ step.title }}
                <p class="step-description">{{ step.description }}</p>
                <div v-if="index === currentStepIndex" class="current-step-hint">
                  <el-alert type="info" :closable="false">
                    <template #title>当前任务</template>
                    <p>{{ step.hint }}</p>
                    <div v-if="step.parameters" class="parameter-hints">
                      <p><strong>需要提供的参数：</strong></p>
                      <ul>
                        <li v-for="param in step.parameters" :key="param.name">
                          <strong>{{ param.name }}：</strong>{{ param.description }}
                        </li>
                      </ul>
                    </div>
                  </el-alert>
                </div>
              </li>
            </ol>
          </div>
        </div>
      </el-card>

      <el-row :gutter="20" class="challenge-overview">
        <el-col :span="12">
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <span>📊 挑战进度</span>
                <el-button v-if="!progress?.isCompleted" type="primary" size="small" @click="refreshProgress">刷新进度</el-button>
              </div>
            </template>
            <el-progress
              :percentage="Math.min(progress?.progressPercentage || 0, 100)"
              :status="progress?.isCompleted ? 'success' : ''"
              :text-inside="true"
              :stroke-width="20"
            />
            <p class="progress-text">
              当前步骤: {{ currentStepIndex + 1 }} / {{ challengeSteps.length }}
              <span v-if="progress?.isCompleted" style="color: #67c23a;">(已完成)</span>
            </p>
            <div class="progress-details">
              <p><strong>开始时间：</strong>{{ formatTime(progress?.startedAt) }}</p>
              <p v-if="progress?.completedAt"><strong>完成时间：</strong>{{ formatTime(progress?.completedAt) }}</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="box-card">
            <template #header>
              <div class="card-header">
                <span>🔧 执行操作</span>
              </div>
            </template>
            <div v-if="!progress?.isCompleted" class="execution-panel">
              <h4>当前步骤：{{ currentStep?.title }}</h4>
              <p class="step-hint">{{ currentStep?.hint }}</p>
              
              <el-form :model="stepParams" label-width="100px" class="step-form">
                <el-form-item 
                  v-for="param in currentStep?.parameters || []" 
                  :key="param.name"
                  :label="param.name"
                  :required="param.required"
                >
                  <el-input 
                    v-model="stepParams[param.name]" 
                    :placeholder="param.placeholder"
                    :type="(param as any).type || 'text'"
                  />
                  <div class="param-hint">{{ param.description }}</div>
                </el-form-item>
              </el-form>
              
              <el-button
                type="primary"
                :disabled="executingStep || !canExecuteStep"
                @click="executeCurrentStep"
                class="execute-button"
              >
                <el-icon v-if="executingStep" class="is-loading"><Loading /></el-icon>
                {{ executingStep ? '执行中...' : '执行当前步骤' }}
              </el-button>
            </div>
            <div v-else class="completion-message">
              <el-result
                icon="success"
                title="挑战完成！"
                sub-title="恭喜你成功完成了所有挑战步骤"
              >
                <template #extra>
                  <el-button type="primary" @click="goBack">返回挑战列表</el-button>
                  <el-button type="warning" @click="resetChallenge">重新开始挑战</el-button>
                </template>
              </el-result>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 执行结果 -->
      <el-card v-if="stepResult" class="execution-result">
        <template #header>
          <div class="card-header">
            <span>📋 执行结果</span>
            <el-tag :type="stepResult.success ? 'success' : 'danger'" size="small">
              {{ stepResult.success ? '成功' : '失败' }}
            </el-tag>
          </div>
        </template>
        
        <!-- 主要结果信息 -->
        <el-alert
          :title="stepResult.message"
          :type="stepResult.success ? 'success' : 'error'"
          show-icon
          :closable="false"
        />
        
        <!-- 详细结果数据 -->
        <div v-if="stepResult.data" class="result-details">
          <!-- 漏洞类型信息 -->
          <div v-if="stepResult.data.vulnerabilityType" class="vulnerability-info">
            <h4>🔍 漏洞类型：{{ stepResult.data.vulnerabilityType }}</h4>
          </div>
          
          <!-- 攻击结果 -->
          <div v-if="stepResult.data.result" class="attack-result">
            <h4>⚡ 攻击结果：</h4>
            <p class="result-text">{{ stepResult.data.result }}</p>
          </div>
          
          <!-- SQL分析 -->
          <div v-if="stepResult.data.sqlAnalysis" class="sql-analysis">
            <h4>🗄️ SQL查询分析：</h4>
            <div class="sql-comparison">
              <div class="sql-original">
                <h5>原始查询：</h5>
                <pre class="sql-code">{{ stepResult.data.sqlAnalysis.originalQuery }}</pre>
              </div>
              <div class="sql-vulnerable">
                <h5>漏洞查询：</h5>
                <pre class="sql-code">{{ stepResult.data.sqlAnalysis.vulnerableQuery }}</pre>
              </div>
              <div class="sql-explanation">
                <h5>攻击原理：</h5>
                <p class="explanation-text">{{ stepResult.data.sqlAnalysis.explanation }}</p>
              </div>
            </div>
          </div>
          
          <!-- HTML渲染分析 -->
          <div v-if="stepResult.data.htmlAnalysis" class="html-analysis">
            <h4>🌐 HTML渲染分析：</h4>
            <div class="html-comparison">
              <div class="html-original">
                <h5>原始内容：</h5>
                <pre class="html-code">{{ stepResult.data.htmlAnalysis.originalContent }}</pre>
              </div>
              <div class="html-vulnerable">
                <h5>漏洞内容：</h5>
                <pre class="html-code">{{ stepResult.data.htmlAnalysis.vulnerableContent }}</pre>
              </div>
              <div class="html-explanation">
                <h5>攻击原理：</h5>
                <p class="explanation-text">{{ stepResult.data.htmlAnalysis.explanation }}</p>
              </div>
            </div>
          </div>
          
          <!-- HTTP请求分析 -->
          <div v-if="stepResult.data.httpAnalysis" class="http-analysis">
            <h4>🌍 HTTP请求分析：</h4>
            <div class="http-comparison">
              <div class="http-original">
                <h5>原始请求：</h5>
                <pre class="http-code">{{ stepResult.data.httpAnalysis.originalRequest }}</pre>
              </div>
              <div class="http-vulnerable">
                <h5>漏洞请求：</h5>
                <pre class="http-code">{{ stepResult.data.httpAnalysis.vulnerableRequest }}</pre>
              </div>
              <div class="http-explanation">
                <h5>攻击原理：</h5>
                <p class="explanation-text">{{ stepResult.data.httpAnalysis.explanation }}</p>
              </div>
            </div>
          </div>
          
          <!-- 文件上传分析 -->
          <div v-if="stepResult.data.uploadAnalysis" class="upload-analysis">
            <h4>📁 文件上传分析：</h4>
            <div class="upload-details">
              <div class="upload-info">
                <h5>上传文件：{{ stepResult.data.uploadAnalysis.originalFilename }}</h5>
                <h5>文件内容：</h5>
                <pre class="file-content">{{ stepResult.data.uploadAnalysis.maliciousContent }}</pre>
              </div>
              <div class="upload-explanation">
                <h5>攻击原理：</h5>
                <p class="explanation-text">{{ stepResult.data.uploadAnalysis.explanation }}</p>
                <h5>安全建议：</h5>
                <p class="security-tip">{{ stepResult.data.uploadAnalysis.securityTip }}</p>
              </div>
            </div>
          </div>
          
          <!-- 业务逻辑分析 -->
          <div v-if="stepResult.data.businessAnalysis" class="business-analysis">
            <h4>💼 业务逻辑分析：</h4>
            <div class="business-details">
              <div class="business-info">
                <h5>操作类型：{{ stepResult.data.businessAnalysis.operationType }}</h5>
                <h5>金额变化：{{ stepResult.data.businessAnalysis.amountChange }}</h5>
                <h5>目标账户：{{ stepResult.data.businessAnalysis.targetAccount }}</h5>
              </div>
              <div class="business-explanation">
                <h5>攻击原理：</h5>
                <p class="explanation-text">{{ stepResult.data.businessAnalysis.explanation }}</p>
                <h5>安全建议：</h5>
                <p class="security-tip">{{ stepResult.data.businessAnalysis.securityTip }}</p>
              </div>
            </div>
          </div>
          
          <!-- JWT分析 -->
          <div v-if="stepResult.data.jwtAnalysis" class="jwt-analysis">
            <h4>🔐 JWT令牌分析：</h4>
            <div class="jwt-details">
              <div class="jwt-info">
                <h5>算法类型：{{ stepResult.data.jwtAnalysis.algorithmType }}</h5>
                <h5>令牌内容：</h5>
                <pre class="jwt-token">{{ stepResult.data.jwtAnalysis.tokenContent }}</pre>
              </div>
              <div class="jwt-explanation">
                <h5>攻击原理：</h5>
                <p class="explanation-text">{{ stepResult.data.jwtAnalysis.explanation }}</p>
                <h5>安全建议：</h5>
                <p class="security-tip">{{ stepResult.data.jwtAnalysis.securityTip }}</p>
              </div>
            </div>
          </div>
          
          <!-- 其他数据 -->
          <div v-if="hasOtherData" class="other-data">
            <h4>📊 其他数据：</h4>
            <pre class="result-json">{{ JSON.stringify(filteredData, null, 2) }}</pre>
          </div>
        </div>
      </el-card>
    </div>

    <div v-else class="empty-state">
      <el-empty description="挑战场景或进度未找到" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { challengeApi } from '@/api/challenge'
import { Loading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const challengeId = ref(route.params.id as string)
const challenge = ref<any>(null)
const progress = ref<any>(null)
const loading = ref(true)
const executingStep = ref(false)
const stepParams = ref<any>({})
const stepResult = ref<any>(null)

// 根据漏洞类型获取步骤配置
const getStepConfigByVulnerabilityType = (vulnerabilityType: string, stepIndex: number) => {
  const stepNumber = stepIndex + 1
  
  // 漏洞类型到步骤配置的映射
  const vulnerabilityStepMap: Record<string, any> = {
    // A01系列
    'A01-越权访问': {
      title: '越权访问',
      description: '利用越权访问漏洞访问他人资源',
      hint: '尝试访问其他用户的资源，修改资源ID参数',
      parameters: [
        { name: 'resourceId', description: '资源ID', placeholder: '例如: 1', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: read', required: true }
      ]
    },
    'A01-权限提升': {
      title: '权限提升',
      description: '利用权限提升漏洞获取更高权限',
      hint: '尝试访问管理员资源，修改资源ID为admin',
      parameters: [
        { name: 'resourceId', description: '资源ID', placeholder: '例如: admin', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: read', required: true }
      ]
    },
    'A01-访问控制': {
      title: '访问控制绕过',
      description: '绕过访问控制机制访问敏感资源',
      hint: '尝试访问受限资源，修改资源ID参数',
      parameters: [
        { name: 'resourceId', description: '资源ID', placeholder: '例如: 1', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: read', required: true }
      ]
    },
    'A01-数据泄露': {
      title: '数据泄露',
      description: '利用访问控制漏洞泄露敏感数据',
      hint: '尝试访问敏感数据资源',
      parameters: [
        { name: 'resourceId', description: '资源ID', placeholder: '例如: sensitive', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: read', required: true }
      ]
    },
    'A01-数据篡改': {
      title: '数据篡改',
      description: '利用访问控制漏洞篡改数据',
      hint: '尝试修改他人数据',
      parameters: [
        { name: 'resourceId', description: '资源ID', placeholder: '例如: 1', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: update', required: true }
      ]
    },
    'A01-信息收集': {
      title: '信息收集',
      description: '通过越权访问收集系统信息',
      hint: '尝试访问系统信息接口',
      parameters: [
        { name: 'resourceId', description: '资源ID', placeholder: '例如: 1', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: read', required: true }
      ]
    },
    'A01-路径穿越': {
      title: '路径穿越',
      description: '利用路径穿越漏洞访问系统文件',
      hint: '尝试使用../访问系统文件，如 /etc/passwd',
      parameters: [
        { name: 'filepath', description: '文件路径', placeholder: '例如: ../../../etc/passwd', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: read', required: true }
      ]
    },
    'A01-文件上传': {
      title: '文件上传漏洞',
      description: '利用文件上传漏洞上传恶意文件',
      hint: '上传包含恶意代码的文件，绕过文件类型检查',
      parameters: [
        { name: 'filename', description: '文件名', placeholder: '例如: shell.php', required: true },
        { name: 'content', description: '文件内容', placeholder: '例如: <?php echo "Hello"; ?>', required: true }
      ]
    },
    'A01-任意读取': {
      title: '任意文件读取',
      description: '利用文件读取漏洞读取敏感文件',
      hint: '读取包含Flag的文件，如 /flag.txt',
      parameters: [
        { name: 'filepath', description: '文件路径', placeholder: '例如: /flag.txt', required: true },
        { name: 'action', description: '操作类型', placeholder: '例如: read', required: true }
      ]
    },
    // A03系列
    'A03-SQL注入': {
      title: 'SQL注入攻击',
      description: '通过SQL注入获取管理员凭据',
      hint: '在登录表单中注入SQL语句，绕过身份验证。尝试使用: \' OR 1=1 --',
      parameters: [
        { name: 'username', description: '用户名', placeholder: '例如: admin', required: true },
        { name: 'password', description: '密码', placeholder: '例如: \' OR 1=1 --', required: true }
      ]
    },
    'A03-注入漏洞': {
      title: 'SQL注入攻击',
      description: '通过SQL注入获取管理员凭据',
      hint: '在登录表单中注入SQL语句，绕过身份验证。尝试使用: \' OR 1=1 --',
      parameters: [
        { name: 'username', description: '用户名', placeholder: '例如: admin', required: true },
        { name: 'password', description: '密码', placeholder: '例如: \' OR 1=1 --', required: true }
      ]
    },
    'A03-LDAP注入': {
      title: 'LDAP注入攻击',
      description: '通过LDAP注入获取用户信息',
      hint: '在LDAP查询中注入恶意代码',
      parameters: [
        { name: 'username', description: '用户名', placeholder: '例如: admin', required: true },
        { name: 'password', description: '密码', placeholder: '例如: *', required: true }
      ]
    },
    'A03-XSS': {
      title: 'XSS攻击',
      description: '通过跨站脚本攻击获取用户Cookie',
      hint: '在博客评论中注入恶意脚本，获取用户Cookie。尝试使用: script标签代码',
      parameters: [
        { name: 'comment', description: '评论内容', placeholder: '例如: script标签代码', required: true },
        { name: 'articleId', description: '文章ID', placeholder: '例如: 1', required: true }
      ]
    },
    'A03-XXE': {
      title: 'XXE攻击',
      description: '利用XML外部实体注入读取敏感文件',
      hint: '构造恶意的XML实体，读取系统文件',
      parameters: [
        { name: 'xml', description: 'XML内容', placeholder: '例如: <!DOCTYPE root [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>', required: true },
        { name: 'entity', description: '实体名称', placeholder: '例如: xxe', required: true }
      ]
    },
    'A03-命令执行': {
      title: '命令执行',
      description: '利用命令注入执行系统命令',
      hint: '注入系统命令，如 whoami 或 cat /flag.txt',
      parameters: [
        { name: 'command', description: '命令', placeholder: '例如: whoami', required: true },
        { name: 'parameter', description: '参数', placeholder: '例如: -a', required: false }
      ]
    },
    // A04系列
    'A04-逻辑缺陷': {
      title: '业务逻辑漏洞',
      description: '利用业务逻辑缺陷绕过验证',
      hint: '尝试修改业务参数，绕过业务逻辑验证',
      parameters: [
        { name: 'action', description: '操作类型', placeholder: '例如: transfer', required: true },
        { name: 'amount', description: '金额', placeholder: '例如: 1000', required: true },
        { name: 'target', description: '目标', placeholder: '例如: user2', required: true }
      ]
    },
    'A04-业务逻辑': {
      title: '业务逻辑漏洞',
      description: '利用业务逻辑缺陷绕过验证',
      hint: '尝试修改业务参数，绕过业务逻辑验证',
      parameters: [
        { name: 'action', description: '操作类型', placeholder: '例如: transfer', required: true },
        { name: 'amount', description: '金额', placeholder: '例如: 1000', required: true },
        { name: 'target', description: '目标', placeholder: '例如: user2', required: true }
      ]
    },
    'A04-逻辑漏洞': {
      title: '业务逻辑漏洞',
      description: '利用业务逻辑缺陷绕过验证',
      hint: '尝试修改业务参数，绕过业务逻辑验证',
      parameters: [
        { name: 'action', description: '操作类型', placeholder: '例如: transfer', required: true },
        { name: 'amount', description: '金额', placeholder: '例如: 1000', required: true },
        { name: 'target', description: '目标', placeholder: '例如: user2', required: true }
      ]
    },
    'A04-条件竞争': {
      title: '条件竞争',
      description: '利用条件竞争漏洞绕过限制',
      hint: '通过并发请求触发条件竞争',
      parameters: [
        { name: 'action', description: '操作类型', placeholder: '例如: purchase', required: true },
        { name: 'amount', description: '金额', placeholder: '例如: -100', required: true },
        { name: 'target', description: '目标', placeholder: '例如: item1', required: true }
      ]
    },
    'A04-金额篡改': {
      title: '金额篡改',
      description: '利用业务逻辑漏洞篡改金额',
      hint: '尝试修改支付金额，绕过金额验证',
      parameters: [
        { name: 'action', description: '操作类型', placeholder: '例如: refund', required: true },
        { name: 'amount', description: '金额', placeholder: '例如: 999999', required: true },
        { name: 'target', description: '目标', placeholder: '例如: order1', required: true }
      ]
    },
    // A05系列
    'A05-CSRF': {
      title: 'CSRF攻击',
      description: '利用跨站请求伪造执行恶意操作',
      hint: '构造恶意请求，利用用户身份执行操作。尝试使用: /api/v1/delete-article',
      parameters: [
        { name: 'targetUrl', description: '目标URL', placeholder: '例如: /api/v1/delete-article', required: true },
        { name: 'articleId', description: '文章ID', placeholder: '例如: 1', required: true }
      ]
    },
    'A05-配置错误': {
      title: '配置错误利用',
      description: '利用安全配置错误绕过限制',
      hint: '尝试修改配置参数，绕过安全限制',
      parameters: [
        { name: 'config', description: '配置项', placeholder: '例如: debug=true', required: true },
        { name: 'value', description: '配置值', placeholder: '例如: true', required: true }
      ]
    },
    // A07系列
    'A07-JWT漏洞': {
      title: 'JWT令牌攻击',
      description: '利用JWT漏洞绕过身份验证',
      hint: '尝试伪造或破解JWT令牌，获取管理员权限',
      parameters: [
        { name: 'token', description: 'JWT令牌', placeholder: '例如: eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0...', required: true },
        { name: 'algorithm', description: '算法', placeholder: '例如: none', required: false }
      ]
    },
    // A08系列
    'A08-反序列化': {
      title: '反序列化攻击',
      description: '利用反序列化漏洞执行恶意代码',
      hint: '构造恶意的序列化数据，触发反序列化漏洞',
      parameters: [
        { name: 'serializedData', description: '序列化数据', placeholder: '例如: O:8:"stdClass":1:{s:4:"test";s:4:"evil";}', required: true }
      ]
    },
    // A10系列
    'A10-SSRF': {
      title: 'SSRF攻击',
      description: '利用服务端请求伪造访问内网资源',
      hint: '构造恶意URL，访问内网服务',
      parameters: [
        { name: 'url', description: '目标URL', placeholder: '例如: http://127.0.0.1:22', required: true },
        { name: 'method', description: '请求方法', placeholder: '例如: GET', required: false }
      ]
    }
  }
  
  // 获取漏洞类型的步骤配置
  const stepConfig = vulnerabilityStepMap[vulnerabilityType]
  if (stepConfig) {
    return {
      ...stepConfig,
      title: `步骤 ${stepNumber}: ${stepConfig.title}`
    }
  }
  
  // 默认配置
  return {
    title: `步骤 ${stepNumber}: 漏洞利用`,
    description: `利用${vulnerabilityType}漏洞`,
    hint: `根据提示执行相应的漏洞利用`,
    parameters: [
      { name: 'param1', description: '参数1', placeholder: '请输入参数1', required: true },
      { name: 'param2', description: '参数2', placeholder: '请输入参数2', required: false }
    ]
  }
}

// 挑战步骤定义 - 根据vulnerability_chain动态生成
const challengeSteps = computed(() => {
  if (!challenge.value) return []
  
  try {
    // 解析vulnerability_chain
    const vulnerabilityChain = typeof challenge.value.vulnerabilityChain === 'string' 
      ? JSON.parse(challenge.value.vulnerabilityChain)
      : challenge.value.vulnerabilityChain
    
    if (!Array.isArray(vulnerabilityChain) || vulnerabilityChain.length === 0) {
      console.warn('vulnerability_chain为空或格式错误，使用默认步骤')
      return [
        {
          title: '步骤 1: 漏洞利用',
          description: '利用系统漏洞',
          hint: '根据提示执行相应的漏洞利用',
          parameters: [
            { name: 'param1', description: '参数1', placeholder: '请输入参数1', required: true },
            { name: 'param2', description: '参数2', placeholder: '请输入参数2', required: false }
          ]
        }
      ]
    }
    
    // 根据vulnerability_chain动态生成步骤
    return vulnerabilityChain.map((vulnerabilityType: string, index: number) => {
      return getStepConfigByVulnerabilityType(vulnerabilityType, index)
    })
  } catch (error) {
    console.error('解析vulnerability_chain失败:', error)
    return [
      {
        title: '步骤 1: 漏洞利用',
        description: '利用系统漏洞',
        hint: '根据提示执行相应的漏洞利用',
        parameters: [
          { name: 'param1', description: '参数1', placeholder: '请输入参数1', required: true },
          { name: 'param2', description: '参数2', placeholder: '请输入参数2', required: false }
        ]
      }
    ]
  }
})

const currentStepIndex = computed(() => {
  return Math.min(progress.value?.currentStep || 0, challengeSteps.value.length - 1)
})

const currentStep = computed(() => {
  return challengeSteps.value[currentStepIndex.value] || null
})

const canExecuteStep = computed(() => {
  if (!currentStep.value) return false
  if (progress.value?.isCompleted) return false
  
  // 检查必填参数
  for (const param of currentStep.value.parameters || []) {
    if (param.required && !stepParams.value[param.name]) {
      return false
    }
  }
  return true
})

// 计算是否有其他数据
const hasOtherData = computed(() => {
  if (!stepResult.value?.data) return false
  
  const data = stepResult.value.data
  const knownKeys = [
    'vulnerabilityType', 'result', 'sqlAnalysis', 'htmlAnalysis', 
    'httpAnalysis', 'uploadAnalysis', 'businessAnalysis', 'jwtAnalysis'
  ]
  
  return Object.keys(data).some(key => !knownKeys.includes(key))
})

// 过滤其他数据
const filteredData = computed(() => {
  if (!stepResult.value?.data) return {}
  
  const data = stepResult.value.data
  const knownKeys = [
    'vulnerabilityType', 'result', 'sqlAnalysis', 'htmlAnalysis', 
    'httpAnalysis', 'uploadAnalysis', 'businessAnalysis', 'jwtAnalysis'
  ]
  
  const filtered: Record<string, any> = {}
  Object.keys(data).forEach(key => {
    if (!knownKeys.includes(key)) {
      filtered[key] = data[key]
    }
  })
  
  return filtered
})

const loadChallengeAndProgress = async () => {
  try {
    loading.value = true
    const [challengeRes, progressRes] = await Promise.all([
      challengeApi.getScenario(parseInt(challengeId.value)),
      challengeApi.getProgress(parseInt(challengeId.value))
    ])

    console.log('Challenge response:', challengeRes)
    console.log('Progress response:', progressRes)

    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    const challengeData = challengeRes as any
    const progressData = progressRes as any

    // 检查挑战场景响应
    if (challengeData && challengeData.code === 200 && challengeData.data) {
      challenge.value = challengeData.data
    } else {
      const errorMessage = challengeData?.message || '加载挑战场景详情失败'
      ElMessage.error(errorMessage)
    }

    // 检查进度响应
    if (progressData && progressData.code === 200 && progressData.data) {
      progress.value = progressData.data
      // Initialize stepParams based on the current step's parameters
      if (currentStep.value?.parameters) {
        stepParams.value = currentStep.value.parameters.reduce((acc: any, param: any) => {
          acc[param.name] = ''; // Initialize with empty string
          return acc;
        }, {});
      }
    } else {
      const errorMessage = progressData?.message || '加载挑战进度失败'
      ElMessage.error(errorMessage)
    }
  } catch (error) {
    console.error('加载挑战数据失败:', error)
    ElMessage.error('加载挑战数据失败')
  } finally {
    loading.value = false
  }
}

const executeCurrentStep = async () => {
  if (!challenge.value || !progress.value || progress.value.isCompleted || !currentStep.value) {
    ElMessage.warning('挑战已完成或数据不完整')
    return
  }

  executingStep.value = true
  stepResult.value = null

  try {
    const currentStepName = `step${currentStepIndex.value + 1}`
    
    // Validate required parameters
    for (const param of currentStep.value.parameters) {
      if (param.required && !stepParams.value[param.name]) {
        ElMessage.error(`参数 "${param.name}" 是必填项！`)
        executingStep.value = false
        return
      }
    }

    const response = await challengeApi.executeStep(
      parseInt(challengeId.value),
      currentStepName,
      stepParams.value // Pass stepParams directly
    )

    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    // response.data 是 ChallengeResult 对象
    const responseData = response as any
    
    // 检查响应结构：ApiResult.success("执行步骤成功", ChallengeResult)
    // responseData.code === 200 表示HTTP成功
    // responseData.data.success 表示业务逻辑成功
    if (responseData && responseData.code === 200 && responseData.data && responseData.data.success) {
      ElMessage.success('步骤执行成功！')
      stepResult.value = responseData.data // ChallengeResult对象
      await refreshProgress() // 刷新进度
    } else {
      // 失败情况：可能是HTTP错误或业务逻辑失败
      const errorMessage = responseData?.data?.message || responseData?.message || '步骤执行失败'
      ElMessage.error(`步骤执行失败: ${errorMessage}`)
      stepResult.value = responseData?.data || { success: false, message: errorMessage }
    }
  } catch (error: any) {
    ElMessage.error(`执行步骤时发生错误: ${error.message}`)
    stepResult.value = { success: false, message: `执行步骤时发生错误: ${error.message}` }
  } finally {
    executingStep.value = false
  }
}

const refreshProgress = async () => {
  try {
    const response = await challengeApi.getProgress(parseInt(challengeId.value))
    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    const responseData = response as any
    
    if (responseData && responseData.code === 200 && responseData.data) {
      progress.value = responseData.data
    }
  } catch (error) {
    console.error('刷新进度失败:', error)
    ElMessage.error('刷新进度失败')
  }
}

const formatTime = (time: string) => {
  if (!time) return '未知'
  return new Date(time).toLocaleString('zh-CN')
}

const goBack = () => {
  router.push('/challenge/list')
}

const resetChallenge = async () => {
  try {
    console.log('开始重置挑战，挑战ID:', challengeId.value)
    const response = await challengeApi.resetChallenge(parseInt(challengeId.value))
    console.log('重置挑战响应:', response)
    
    // 响应拦截器已经返回了data，所以response就是ApiResult对象
    const responseData = response as any
    console.log('解析后的响应数据:', responseData)
    
    // 检查响应结构：ApiResult.success("重置挑战成功", ChallengeProgress)
    if (responseData && responseData.code === 200 && responseData.data) {
      ElMessage.success('挑战已重置，可以重新开始！')
      // 重置步骤结果
      stepResult.value = null
      // 重置步骤参数
      if (currentStep.value?.parameters) {
        stepParams.value = currentStep.value.parameters.reduce((acc: any, param: any) => {
          acc[param.name] = ''
          return acc
        }, {})
      }
      await loadChallengeAndProgress() // 重新加载数据
    } else {
      const errorMessage = responseData?.message || '重置挑战失败'
      console.error('重置挑战失败，响应数据:', responseData)
      ElMessage.error(errorMessage)
    }
  } catch (error: any) {
    console.error('重置挑战异常:', error)
    ElMessage.error(`重置挑战失败: ${error.message || '未知错误'}`)
  }
}

// 监听挑战变化，重新初始化参数
watch(challenge, () => {
  if (currentStep.value?.parameters) {
    stepParams.value = currentStep.value.parameters.reduce((acc: any, param: any) => {
      acc[param.name] = ''; // Initialize with empty string
      return acc;
    }, {});
  }
})

onMounted(() => {
  loadChallengeAndProgress()
})
</script>

<style scoped>
.challenge-arena {
  padding: 20px;
}

.arena-header {
  margin-bottom: 20px;
}

.challenge-objective {
  margin-bottom: 20px;
}

.objective-content {
  line-height: 1.6;
}

.objective-steps {
  margin-top: 15px;
}

.objective-steps ol {
  padding-left: 20px;
}

.objective-steps li {
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.objective-steps li.completed {
  background-color: #f0f9ff;
  border-left: 4px solid #67c23a;
}

.objective-steps li.current {
  background-color: #e6f7ff;
  border-left: 4px solid #409eff;
}

.step-description {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.current-step-hint {
  margin-top: 10px;
}

.parameter-hints {
  margin-top: 10px;
}

.parameter-hints ul {
  margin: 5px 0;
  padding-left: 20px;
}

.challenge-overview {
  margin-bottom: 20px;
}

.box-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-text {
  margin: 10px 0;
  font-size: 14px;
}

.progress-details {
  margin-top: 15px;
  font-size: 12px;
  color: #666;
}

.execution-panel {
  padding: 10px 0;
}

.step-form {
  margin: 20px 0;
}

.param-hint {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.execute-button {
  width: 100%;
  margin-top: 20px;
}

.completion-message {
  text-align: center;
  padding: 20px;
}

.execution-result {
  margin-top: 20px;
}

.result-details {
  margin-top: 15px;
}

.result-json {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  font-size: 12px;
  max-height: 200px;
  overflow-y: auto;
}

/* 详细结果样式 */
.vulnerability-info {
  margin: 15px 0;
  padding: 10px;
  background-color: #f0f9ff;
  border-left: 4px solid #409eff;
  border-radius: 4px;
}

.attack-result {
  margin: 15px 0;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.result-text {
  margin: 5px 0;
  font-size: 14px;
  line-height: 1.5;
}

/* SQL分析样式 */
.sql-analysis {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.sql-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

.sql-original, .sql-vulnerable {
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.sql-code {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #495057;
  overflow-x: auto;
  margin: 5px 0;
}

.sql-explanation {
  grid-column: 1 / -1;
  margin-top: 15px;
  padding: 10px;
  background-color: #fff3cd;
  border-radius: 4px;
  border-left: 4px solid #ffc107;
}

/* HTML分析样式 */
.html-analysis {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.html-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

.html-original, .html-vulnerable {
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.html-code {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #495057;
  overflow-x: auto;
  margin: 5px 0;
}

.html-explanation {
  grid-column: 1 / -1;
  margin-top: 15px;
  padding: 10px;
  background-color: #fff3cd;
  border-radius: 4px;
  border-left: 4px solid #ffc107;
}

/* HTTP分析样式 */
.http-analysis {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.http-comparison {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

.http-original, .http-vulnerable {
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.http-code {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #495057;
  overflow-x: auto;
  margin: 5px 0;
}

.http-explanation {
  grid-column: 1 / -1;
  margin-top: 15px;
  padding: 10px;
  background-color: #fff3cd;
  border-radius: 4px;
  border-left: 4px solid #ffc107;
}

/* 文件上传分析样式 */
.upload-analysis {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.upload-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

.upload-info, .upload-explanation {
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.file-content {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #495057;
  overflow-x: auto;
  margin: 5px 0;
}

/* 业务逻辑分析样式 */
.business-analysis {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.business-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

.business-info, .business-explanation {
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

/* JWT分析样式 */
.jwt-analysis {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.jwt-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 15px;
}

.jwt-info, .jwt-explanation {
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

.jwt-token {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #495057;
  overflow-x: auto;
  margin: 5px 0;
  word-break: break-all;
}

/* 通用样式 */
.explanation-text {
  margin: 5px 0;
  font-size: 14px;
  line-height: 1.5;
  color: #495057;
}

.security-tip {
  margin: 5px 0;
  font-size: 14px;
  line-height: 1.5;
  color: #dc3545;
  font-weight: 500;
}

.other-data {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sql-comparison,
  .html-comparison,
  .http-comparison,
  .upload-details,
  .business-details,
  .jwt-details {
    grid-template-columns: 1fr;
  }
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.loading {
  padding: 20px;
}
</style>