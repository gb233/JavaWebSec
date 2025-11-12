<template>
  <div class="test-exam">
    <!-- 考试头部 -->
    <div class="exam-header">
      <div class="header-left">
        <h1 class="exam-title">{{ testRecord?.categoryCode ? `${testRecord.categoryCode}安全知识测试` : '安全知识测试' }}</h1>
        <div class="exam-info">
          <span class="info-item">
            <el-icon><QuestionFilled /></el-icon>
            {{ currentQuestionIndex + 1 }} / {{ questions.length || 0 }}
          </span>
          <span class="info-item">
            <el-icon><Clock /></el-icon>
            {{ formatTime(remainingTime) }}
          </span>
          <span class="info-item mode-info">
            <el-icon><Setting /></el-icon>
            {{ getModeDisplayName(testRecord?.modeCode || 'realtime') }}
          </span>
        </div>
      </div>
      <div class="header-right">
        <el-button @click="showExitDialog" type="danger" plain>
          退出考试
        </el-button>
      </div>
    </div>

    <!-- 进度条 -->
    <div class="progress-section">
      <el-progress 
        :percentage="(questions.length || 0) > 0 ? Math.round((currentQuestionIndex + 1) / (questions.length || 1) * 100) : 0"
        :show-text="false"
        :stroke-width="8"
      />
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 题目内容区域 -->
      <div class="question-content-area">

        <!-- 题目内容 -->
        <div class="question-content" v-if="currentQuestion">
          <div class="question-header">
            <div class="question-number">第 {{ currentQuestionIndex + 1 }} 题</div>
            <div class="question-type">
              <el-tag :type="getQuestionTypeTag(currentQuestion.questionType)">
                {{ getQuestionTypeName(currentQuestion.questionType) }}
              </el-tag>
            </div>
            <div class="question-points">{{ currentQuestion.score }} 分</div>
          </div>
          
          <div class="question-text" v-html="formatQuestionText(currentQuestion.questionText)"></div>

          <!-- 选择题选项 -->
          <div class="question-options" v-if="isChoiceQuestion">
            <el-radio-group 
              v-model="userAnswer" 
              v-if="currentQuestion.questionType === 'single' || currentQuestion.questionType === 'SINGLE' || currentQuestion.questionType === 'single_choice'"
            >
              <el-radio 
                v-for="(option, index) in parsedOptions" 
                :key="index"
                :label="option"
                class="option-item"
              >
                {{ option }}
              </el-radio>
            </el-radio-group>
            
            <el-checkbox-group 
              v-model="userAnswerArray" 
              v-if="currentQuestion.questionType === 'multiple' || currentQuestion.questionType === 'MULTIPLE' || currentQuestion.questionType === 'multiple_choice'"
            >
              <el-checkbox 
                v-for="(option, index) in parsedOptions" 
                :key="index"
                :label="option"
                class="option-item"
              >
                {{ option }}
              </el-checkbox>
            </el-checkbox-group>
          </div>

          <!-- 判断题 -->
          <div class="question-options" v-if="currentQuestion.questionType === 'judge' || currentQuestion.questionType === 'JUDGE' || currentQuestion.questionType === 'true_false'">
            <el-radio-group v-model="userAnswer">
              <el-radio 
                v-for="(option, index) in parsedOptions" 
                :key="index"
                :label="String.fromCharCode(65 + index)"
                class="option-item"
              >
                {{ option }}
              </el-radio>
            </el-radio-group>
          </div>

          <!-- 填空题 -->
          <div class="question-options" v-if="currentQuestion.questionType === 'fill_blank' || currentQuestion.questionType === 'FILL_BLANK'">
            <el-input
              v-model="userAnswer"
              type="textarea"
              :rows="3"
              placeholder="请输入您的答案..."
            />
          </div>
        </div>

        <!-- 空数据提示 -->
        <div class="empty-content" v-else-if="questions.length === 0">
          <el-empty description="暂无测试题目">
            <el-button type="primary" @click="router.back()">返回测试分类</el-button>
          </el-empty>
        </div>

        <!-- 操作按钮 - 只在题目内容区域显示 -->
        <div class="exam-actions">
          <el-button 
            @click="previousQuestion" 
            :disabled="currentQuestionIndex === 0"
            size="large"
          >
            上一题
          </el-button>
          
          <div class="action-center">
            <el-button 
              @click="submitAnswer" 
              type="primary" 
              size="large"
              :loading="submitting"
            >
              {{ currentQuestionIndex === questions.length - 1 ? '提交并完成' : '提交答案' }}
            </el-button>
          </div>
          
          <el-button 
            @click="nextQuestion" 
            :disabled="currentQuestionIndex === questions.length - 1"
            size="large"
          >
            下一题
          </el-button>
        </div>
      </div>

      <!-- 题目导航器 -->
      <div class="navigator-area">
        <QuestionNavigator
          :questions="questionsWithStatus"
          :current-index="currentQuestionIndex"
          :mode-code="testRecord?.modeCode || 'realtime'"
          :allow-navigation="allowNavigation"
          :questions-per-row="questionsPerRow"
          @navigate="navigateToQuestion"
        />
      </div>
    </div>

    <!-- 退出确认对话框 -->
    <el-dialog
      v-model="exitDialogVisible"
      title="确认退出"
      width="400px"
    >
      <p>您确定要退出考试吗？已提交的答案将会保存。</p>
      <template #footer>
        <el-button @click="exitDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="exitExam">确认退出</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled, Clock, Setting } from '@element-plus/icons-vue'
import testApi, { type TestQuestion, type TestSession } from '@/api/test'
import { isSuccessResponse } from '@/utils/api-helpers'
import { useTestStore } from '@/stores/modules/test'
import { useTestMode, getModeDisplayName, isNavigationAllowed, isImmediateFeedbackEnabled, isRetryAllowed, getTimeLimit, getFeedbackType } from '@/composables/useTestMode'
import QuestionNavigator from './components/QuestionNavigator.vue'

const route = useRoute()
const router = useRouter()
const testStore = useTestStore()

// 响应式数据
const testRecord = ref<TestSession | null>(null)
const questions = ref<TestQuestion[]>([])
const currentQuestionIndex = ref(0)
const userAnswer = ref('')
const userAnswerArray = ref<string[]>([])
const submitting = ref(false)
const exitDialogVisible = ref(false)
const remainingTime = ref(0)
const timer = ref<ReturnType<typeof setInterval> | null>(null)

// 计算属性
const currentQuestion = computed(() => {
  if (questions.value.length === 0) return null
  if (currentQuestionIndex.value >= questions.value.length) return null
  return questions.value[currentQuestionIndex.value]
})

const isChoiceQuestion = computed(() => {
  const questionType = currentQuestion.value?.questionType
  return questionType === 'single' || questionType === 'multiple' ||
         questionType === 'SINGLE' || questionType === 'MULTIPLE' ||
         questionType === 'single_choice' || questionType === 'multiple_choice'
})

// 获取测试模式显示名称 - 使用导入的函数

// 解析选项数据
const parsedOptions = computed(() => {
  if (!currentQuestion.value?.options) return []
  
  // 如果options是字符串，解析JSON
  if (typeof currentQuestion.value.options === 'string') {
    try {
      return JSON.parse(currentQuestion.value.options)
    } catch (error) {
      console.error('解析选项JSON失败:', error)
      return []
    }
  }
  
  // 如果options已经是数组，直接返回
  return currentQuestion.value.options
})

// 题目导航器相关
const questionsPerRow = ref(10)
const allowNavigation = computed(() => {
  const modeCode = testRecord.value?.modeCode || 'realtime'
  return isNavigationAllowed(modeCode)
})

// 带状态的题目数据
const questionsWithStatus = computed(() => {
  return questions.value.map((question, index) => ({
    ...question,
    isVisited: index <= currentQuestionIndex.value,
    isAnswered: question.isAnswered || false,
    isCorrect: question.isCorrect || false
  }))
})

// 导航到指定题目
const navigateToQuestion = (index: number) => {
  if (index < 0 || index >= questions.value.length) return
  
  const modeCode = testRecord.value?.modeCode || 'realtime'
  
  // 考试模式限制：只能导航到已访问的题目
  if (modeCode === 'exam' && index > currentQuestionIndex.value) {
    ElMessage.warning('考试模式下只能查看已访问的题目')
    return
  }
  
  currentQuestionIndex.value = index
  // 重置用户答案
  userAnswer.value = ''
  userAnswerArray.value = []
}

// 加载当前题目
const loadCurrentQuestion = () => {
  if (currentQuestion.value) {
    // 重置用户答案
    userAnswer.value = ''
    userAnswerArray.value = []
  }
}

// 生命周期
onMounted(() => {
  const categoryId = route.params.categoryId as string
  if (categoryId) {
    loadTestDataByCategory(categoryId)
  }
})

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})

// 根据分类加载测试数据
const loadTestDataByCategory = async (categoryId: string) => {
  try {
    // 从test store获取当前测试数据
    const currentSession = testStore.currentSession
    if (currentSession && questions.value.length > 0) {
      testRecord.value = currentSession
      startTimer()
    } else {
      // 如果没有store数据，先创建测试记录
      console.log('创建新的测试记录，分类:', categoryId)
      // 从路由查询参数获取测试模式，默认为realtime
      const modeCode = route.query.mode as string || 'realtime'
      const startResponse = await testApi.startTestSession(modeCode, categoryId)
      console.log('开始测试API响应:', startResponse)
      
      if (isSuccessResponse(startResponse) && startResponse.data) {
        const session = startResponse.data
        testRecord.value = session
        console.log('创建测试会话成功，会话ID:', session.id, '会话代码:', session.sessionCode)
        
        // 获取测试题目
        const questionsResponse = await testApi.getTestQuestions(session.sessionCode)
        console.log('获取测试题目API响应:', questionsResponse)
        if (isSuccessResponse(questionsResponse) && questionsResponse.data) {
          questions.value = questionsResponse.data
          console.log('获取测试题目成功，题目数量:', questions.value.length)
          console.log('questions数组内容:', questions.value)
          console.log('currentQuestionIndex:', currentQuestionIndex.value)
          console.log('currentQuestion:', currentQuestion.value)
          
          // 调试分数信息
          if (questions.value.length > 0) {
            console.log('第一题分数:', questions.value[0].score)
            console.log('第一题完整数据:', questions.value[0])
          }
          
          // 检查题目数量
          if (questions.value.length === 0) {
            console.warn('警告：获取到的题目数量为0')
            ElMessage.warning('当前分类下没有可用的题目，请选择其他分类')
            router.back()
            return
          }
          
          startTimer()
        } else {
          console.error('获取测试题目失败:', questionsResponse)
          ElMessage.error('获取测试题目失败: ' + (questionsResponse?.message || '未知错误'))
          router.back()
        }
      } else {
        console.error('创建测试会话失败:', startResponse)
        ElMessage.error('创建测试会话失败: ' + (startResponse?.message || '未知错误'))
        router.back()
      }
    }
  } catch (error) {
    console.error('加载测试数据失败:', error)
    ElMessage.error('加载测试数据失败')
    router.back()
  }
}

// 加载题目（模拟数据）
// 题目数据现在从API获取，不再使用硬编码数据

// 开始计时器
const startTimer = () => {
  remainingTime.value = 30 * 60 // 30分钟
  timer.value = setInterval(() => {
    remainingTime.value--
    if (remainingTime.value <= 0) {
      ElMessage.warning('考试时间已到，将自动提交')
      submitExam()
    }
  }, 1000)
}

// 格式化时间
const formatTime = (seconds: number) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 格式化题目文本，处理转义字符和代码块
const formatQuestionText = (text: string | undefined): string => {
  if (!text) return ''
  
  // 转义HTML特殊字符，防止XSS攻击
  const escapeHtml = (str: string): string => {
    const map: Record<string, string> = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;'
    }
    return str.replace(/[&<>"']/g, (m) => map[m])
  }
  
  // 先处理markdown代码块：```language\ncode\n```
  // 使用非贪婪匹配，匹配代码块内容
  let formatted = text.replace(/```(\w+)?\\n([\s\S]*?)```/g, (match, lang, code) => {
    // 处理代码块内的转义字符
    let codeContent = code
      .replace(/\\n/g, '\n')      // 转义的换行符 -> 实际换行
      .replace(/\\t/g, '\t')      // 转义的制表符 -> 实际制表符
      .replace(/\\r/g, '\r')      // 转义的回车符 -> 实际回车
      .replace(/\\\\/g, '\\')     // 转义的反斜杠 -> 实际反斜杠
    
    // 转义代码内容中的HTML特殊字符
    const escapedCode = escapeHtml(codeContent.trim())
    // 返回HTML格式的代码块
    return `<pre class="code-block"><code class="language-${lang || 'text'}">${escapedCode}</code></pre>`
  })
  
  // 处理代码块外的转义字符：将字面量的\n转换为实际换行
  formatted = formatted
    .replace(/\\n/g, '\n')      // 转义的换行符 -> 实际换行
    .replace(/\\t/g, '\t')      // 转义的制表符 -> 实际制表符
    .replace(/\\r/g, '\r')      // 转义的回车符 -> 实际回车
    .replace(/\\\\/g, '\\')     // 转义的反斜杠 -> 实际反斜杠
  
  // 转义HTML特殊字符（代码块已经处理过，这里只处理代码块外的内容）
  // 先标记代码块，避免转义代码块内的内容
  const codeBlockPlaceholder = '___CODE_BLOCK_PLACEHOLDER___'
  const codeBlocks: string[] = []
  formatted = formatted.replace(/<pre class="code-block">[\s\S]*?<\/pre>/g, (match) => {
    codeBlocks.push(match)
    return codeBlockPlaceholder
  })
  
  // 转义非代码块内容
  formatted = escapeHtml(formatted)
  
  // 恢复代码块
  codeBlocks.forEach((codeBlock) => {
    formatted = formatted.replace(codeBlockPlaceholder, codeBlock)
  })
  
  // 将剩余的换行符转换为<br>标签
  formatted = formatted.replace(/\n/g, '<br>')
  
  return formatted
}

// 获取题目类型标签
type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger'

const getQuestionTypeTag = (type: string): TagType => {
  const typeMap: Record<string, TagType> = {
    'SINGLE': 'success',
    'MULTIPLE': 'warning',
    'JUDGE': 'info',
    'FILL_BLANK': 'primary',
    // 兼容小写格式
    'single_choice': 'success',
    'multiple_choice': 'warning',
    'true_false': 'info',
    'fill_blank': 'primary'
  }
  return typeMap[type] ?? 'info'
}

// 获取题目类型名称
const getQuestionTypeName = (type: string) => {
  const typeMap: Record<string, string> = {
    'SINGLE': '单选题',
    'MULTIPLE': '多选题', 
    'JUDGE': '判断题',
    'FILL_BLANK': '填空题',
    // 兼容小写格式
    'single_choice': '单选题',
    'multiple_choice': '多选题',
    'true_false': '判断题',
    'fill_blank': '填空题'
  }
  return typeMap[type] || '未知类型'
}

// 上一题
const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
    loadCurrentAnswer()
  }
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < questions.value.length - 1) {
    currentQuestionIndex.value++
    loadCurrentAnswer()
  }
}

// 加载当前答案
const loadCurrentAnswer = () => {
  // 这里应该从本地存储或状态中加载已保存的答案
  userAnswer.value = ''
  userAnswerArray.value = []
}

// 提交答案
const submitAnswer = async () => {
  if (!testRecord.value) {
    ElMessage.error('测试记录不存在，无法提交答案')
    return
  }
  if (!currentQuestion.value) {
    ElMessage.error('题目信息缺失，无法提交答案')
    return
  }
  if (!userAnswer.value && userAnswerArray.value.length === 0) {
    ElMessage.warning('请先选择答案')
    return
  }

  try {
    submitting.value = true
    
    // 处理答案格式
    let answer = ''
    if (currentQuestion.value.questionType === 'multiple' || 
        currentQuestion.value.questionType === 'MULTIPLE' || 
        currentQuestion.value.questionType === 'multiple_choice') {
      // 多选题：将选项文本转换为字母格式
      answer = userAnswerArray.value.map(option => {
        const index = parsedOptions.value.findIndex((o: string) => o === option)
        return index >= 0 ? String.fromCharCode(65 + index) : option
      }).join(',')
      console.log('多选题答案处理:', {
        原始答案: userAnswerArray.value,
        转换后: answer,
        选项: parsedOptions.value
      })
    } else {
      // 单选题和判断题：将选项文本转换为字母格式
      if (userAnswer.value) {
        const index = parsedOptions.value.findIndex((o: string) => o === userAnswer.value)
        answer = index >= 0 ? String.fromCharCode(65 + index) : userAnswer.value
        console.log('单选题答案处理:', {
          原始答案: userAnswer.value,
          转换后: answer,
          选项: parsedOptions.value,
          匹配索引: index
        })
      } else {
        answer = userAnswer.value
      }
    }
    
    console.log('最终提交答案:', answer)

    // 使用新的测试模式逻辑
    const modeCode = testRecord.value?.modeCode || 'realtime'
    const { processAnswer } = useTestMode(modeCode)
    
    const answerResult = await processAnswer(
      testRecord.value.sessionCode,
      currentQuestion.value,
      answer
    )
    
    // 调试信息
    console.log('答案提交响应:', answerResult)
    console.log('得分:', answerResult.score)
    console.log('是否正确:', answerResult.isCorrect)
    
    // 根据模式配置显示反馈
    if (answerResult.showImmediately) {
      if (answerResult.isCorrect) {
        const score = answerResult.score || 0
        ElMessage.success(`答案正确！得分：${score}分`)
      } else {
        // 格式化答案显示
        const formatAnswer = (answer: string, questionType: string) => {
          if (questionType === 'MULTIPLE' || questionType === 'multiple' || questionType === 'multiple_choice') {
            // 检查答案格式：如果是字母组合（如ABCD），直接返回
            if (/^[A-Z]+$/.test(answer)) {
              return answer
            }
            // 如果是选项文本格式（如"A. 身份认证,B. 授权检查"），转换为字母
            return answer.split(',').map(opt => {
              const index = parsedOptions.value.findIndex((o: string) => o === opt)
              return index >= 0 ? String.fromCharCode(65 + index) : opt
            }).join('')
          }
          return answer
        }
        
        const formattedUserAnswer = formatAnswer(answer, currentQuestion.value?.questionType || '')
        const formattedCorrectAnswer = formatAnswer(answerResult.correctAnswer || '', currentQuestion.value?.questionType || '')
        
        ElMessage.error(`答案错误！您的答案：${formattedUserAnswer}，正确答案：${formattedCorrectAnswer}`)
      }
    } else {
      // 考试模式：不显示反馈
      ElMessage.info('答案已提交，请继续下一题')
    }
    
    // 显示详细解析（仅在实时反馈模式且显示即时反馈时）
    if (answerResult.showImmediately && answerResult.explanation) {
      const formatAnswer = (answer: string, questionType: string) => {
        if (questionType === 'MULTIPLE' || questionType === 'multiple' || questionType === 'multiple_choice') {
          // 检查答案格式：如果是字母组合（如ABCD），直接返回
          if (/^[A-Z]+$/.test(answer)) {
            return answer
          }
          // 如果是选项文本格式（如"A. 身份认证,B. 授权检查"），转换为字母
          return answer.split(',').map(opt => {
            const index = parsedOptions.value.findIndex((o: string) => o === opt)
            return index >= 0 ? String.fromCharCode(65 + index) : opt
          }).join('')
        }
        return answer
      }
      
      const formattedUserAnswer = formatAnswer(answer, currentQuestion.value?.questionType || '')
      const formattedCorrectAnswer = formatAnswer(answerResult.correctAnswer || '', currentQuestion.value?.questionType || '')
      
      const score = answerResult.score || 0
      const detailedExplanation = `
题目：${currentQuestion.value.questionText}

您的答案：${formattedUserAnswer}
正确答案：${formattedCorrectAnswer}
得分：${score}分

详细解析：
${answerResult.explanation}
      `.trim()
      
      ElMessageBox.alert(
        detailedExplanation,
        '答案解析',
        {
          type: 'info',
          confirmButtonText: '知道了',
          customClass: 'explanation-dialog'
        }
      )
    }
    
    // 更新当前题目的状态
    if (currentQuestion.value) {
      currentQuestion.value.isAnswered = true
      currentQuestion.value.isCorrect = answerResult.isCorrect
      currentQuestion.value.score = answerResult.score || 0
      currentQuestion.value.userAnswer = answer
      currentQuestion.value.correctAnswer = answerResult.correctAnswer
      currentQuestion.value.explanation = answerResult.explanation
      
      // 同步更新questions数组中对应题目的状态
      const questionIndex = questions.value.findIndex(q => q.id === currentQuestion.value?.id)
      if (questionIndex !== -1) {
        questions.value[questionIndex].isAnswered = true
        questions.value[questionIndex].isCorrect = answerResult.isCorrect
        questions.value[questionIndex].score = answerResult.score || 0
        questions.value[questionIndex].userAnswer = answer
        questions.value[questionIndex].correctAnswer = answerResult.correctAnswer
        questions.value[questionIndex].explanation = answerResult.explanation
      }
    }
    
    // 如果是最后一题，完成考试
    if (currentQuestionIndex.value === questions.value.length - 1) {
      await submitExam()
    } else {
      // 移动到下一题
      nextQuestion()
    }
  } catch (error: any) {
    console.error('提交答案失败:', error)
    ElMessage.error(error.message || '提交答案失败')
  } finally {
    submitting.value = false
  }
}

// 完成考试
const submitExam = async () => {
  if (!testRecord.value) {
    ElMessage.error('测试记录不存在，无法提交考试')
    return
  }
  try {
    const response = await testApi.endTestSession(testRecord.value.sessionCode)
    if (isSuccessResponse(response) && response.data) {
      ElMessage.success('考试完成')
      
      // 获取测试结果，从中获取正确的记录ID
      try {
        const resultResponse = await testApi.getTestResult(testRecord.value.sessionCode)
        if (isSuccessResponse(resultResponse) && resultResponse.data) {
          const recordId = resultResponse.data.recordId || resultResponse.data.id
          if (recordId) {
            router.push({
              name: 'TestResult',
              params: { recordId: recordId.toString() }
            })
          } else {
            // 如果没有记录ID，跳转到测试分类页面
            ElMessage.warning('测试完成，但无法获取结果详情')
            router.push({ name: 'TestCategories' })
          }
        } else {
          // 如果获取结果失败，跳转到测试分类页面
          ElMessage.warning('测试完成，但无法获取结果详情')
          router.push({ name: 'TestCategories' })
        }
      } catch (resultError) {
        console.error('获取测试结果失败:', resultError)
        // 如果获取结果失败，跳转到测试分类页面
        ElMessage.warning('测试完成，但无法获取结果详情')
        router.push({ name: 'TestCategories' })
      }
    } else {
      ElMessage.error(response?.message || '完成考试失败')
    }
  } catch (error: any) {
    console.error('完成考试失败:', error)
    ElMessage.error(error.message || '完成考试失败')
  }
}

// 显示退出对话框
const showExitDialog = () => {
  exitDialogVisible.value = true
}

// 退出考试
const exitExam = async () => {
  try {
    if (timer.value) {
      clearInterval(timer.value)
    }
    
    // 如果有已提交的答案，先保存测试结果
    const answeredQuestions = questions.value.filter(q => q.isAnswered)
    console.log('退出考试 - 已答题数量:', answeredQuestions.length)
    console.log('退出考试 - 已答题详情:', answeredQuestions.map(q => ({ id: q.id, isAnswered: q.isAnswered, userAnswer: q.userAnswer })))
    
    if (testRecord.value && answeredQuestions.length > 0) {
      try {
        await testApi.endTestSession(testRecord.value.sessionCode)
        ElMessage.success(`测试结果已保存，共${answeredQuestions.length}题已提交`)
      } catch (error) {
        console.error('保存测试结果失败:', error)
        ElMessage.warning('保存测试结果失败，但已提交的答案不会丢失')
      }
    } else {
      console.log('退出考试 - 没有已提交的答案，直接退出')
    }
    
    // 关闭对话框
    exitDialogVisible.value = false
    
    // 跳转到测试分类页面
    router.push({ name: 'TestCategories' })
  } catch (error) {
    console.error('退出考试失败:', error)
    ElMessage.error('退出考试失败')
  }
}
</script>

<style lang="scss" scoped>
.test-exam {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;

  .exam-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: 20px 24px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;

    .header-left {
      .exam-title {
        font-size: 24px;
        font-weight: 600;
        color: #1f2937;
        margin-bottom: 8px;
      }

      .exam-info {
        display: flex;
        gap: 24px;

        .info-item {
          display: flex;
          align-items: center;
          font-size: 14px;
          color: #6b7280;

          .el-icon {
            margin-right: 4px;
          }
        }
      }
    }
  }

  .progress-section {
    background: white;
    padding: 16px 24px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;
  }

  .main-content {
    display: flex;
    gap: 24px;
    align-items: flex-start;

    .question-content-area {
      flex: 1;
      min-width: 0;
    }

    .navigator-area {
      flex-shrink: 0;
      width: 320px;
      position: sticky;
      top: 24px;
    }
  }

  .question-content {
    background: white;
    border-radius: 12px;
    padding: 32px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    margin-bottom: 24px;

    .question-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding-bottom: 16px;
      border-bottom: 1px solid #e5e7eb;

      .question-number {
        font-size: 18px;
        font-weight: 600;
        color: #1f2937;
      }

      .question-type {
        .el-tag {
          font-size: 12px;
        }
      }

      .question-points {
        font-size: 14px;
        color: #6b7280;
        background: #f3f4f6;
        padding: 4px 8px;
        border-radius: 6px;
      }
    }

    .question-text {
      font-size: 16px;
      line-height: 1.6;
      color: #1f2937;
      margin-bottom: 32px;
      
      // 代码块样式
      :deep(.code-block) {
        background: #282c34;
        color: #abb2bf;
        padding: 16px;
        border-radius: 8px;
        margin: 16px 0;
        overflow-x: auto;
        font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'Courier New', monospace;
        font-size: 14px;
        line-height: 1.6;
        border: 1px solid #e5e7eb;
        
        code {
          display: block;
          white-space: pre;
          color: inherit;
          background: transparent;
          padding: 0;
          border: none;
          
          // 代码高亮样式
          .keyword {
            color: #c678dd;
          }
          
          .string {
            color: #98c379;
          }
          
          .comment {
            color: #5c6370;
            font-style: italic;
          }
          
          .annotation {
            color: #e5c07b;
          }
          
          .function {
            color: #61afef;
          }
        }
      }
    }

    .question-options {
      .option-item {
        display: block;
        margin-bottom: 16px;
        padding: 12px 16px;
        border: 1px solid #e5e7eb;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          border-color: #3b82f6;
          background: #f8fafc;
        }

        &.is-checked,
        &.is-active {
          border-color: #3b82f6;
          background: #eff6ff;
        }
      }
    }
  }

  .exam-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: 20px 24px;
    border-radius: 12px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

    .action-center {
      flex: 1;
      text-align: center;
    }
  }

  // 响应式设计
  @media (max-width: 1200px) {
    .main-content {
      flex-direction: column;
      
      .navigator-area {
        width: 100%;
        position: static;
        order: -1;
      }
    }
  }

  @media (max-width: 768px) {
    padding: 16px;
    
    .exam-header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
      
      .header-right {
        width: 100%;
        text-align: right;
      }
    }
    
    .question-content {
      padding: 20px;
    }
    
    .exam-actions {
      flex-direction: column;
      gap: 12px;
      
      .action-center {
        order: -1;
      }
    }
  }
}

// 全局样式，用于答案解析对话框
:global(.explanation-dialog) {
  .el-message-box__content {
    white-space: pre-line;
    line-height: 1.6;
    max-height: 400px;
    overflow-y: auto;
  }
}
</style>
