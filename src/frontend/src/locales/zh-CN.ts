export default {
  // 通用
  common: {
    title: 'Java Web安全教学系统',
    confirm: '确定',
    cancel: '取消',
    save: '保存',
    delete: '删除',
    edit: '编辑',
    add: '添加',
    search: '搜索',
    reset: '重置',
    submit: '提交',
    back: '返回',
    next: '下一步',
    previous: '上一步',
    complete: '完成',
    skip: '跳过',
    loading: '加载中...',
    success: '操作成功',
    error: '操作失败',
    warning: '警告',
    info: '提示'
  },

  // 导航
  nav: {
    home: '首页',
    dashboard: '控制台',
    vulnerability: '漏洞知识中心',
    knowledgeCenter: '知识中心',
    challenge: '挑战模式',
    challengeList: '挑战列表',
    // leaderboard: '排行榜', // 排行榜功能暂时注释掉 - 2025-01-15
    test: '知识测试',
    testCategories: '测试分类',
    testRecords: '测试记录',
    features: '功能测试',
    simpleTest: '简单测试',
    basicTest: '基础测试',
    helloTest: 'Hello测试',
    profile: '个人中心',
    settings: '设置',
    logout: '退出登录'
  },

  // 语言切换
  language: {
    switch: '切换语言',
    chinese: '简体中文',
    english: 'English',
    current: '当前语言'
  },

  // 新手指引
  guide: {
    title: '新手指引',
    welcome: '欢迎使用Java Web安全教学系统',
    description: '让我们开始探索这个安全学习平台吧！',
    previous: '上一步',
    next: '下一步',
    complete: '完成指引',
    skip: '跳过指引',
    trigger: '新手指引',
    navigation: '导航菜单',
    vulnerabilityCenter: '漏洞知识中心',
    challengeMode: '挑战模式',
    knowledgeTest: '知识测试',
    userProfile: '个人中心',
    languageSwitch: '语言切换',
    guideTrigger: '新手指引'
  },

  // 用户认证
  auth: {
    login: '登录',
    register: '注册',
    logout: '退出登录',
    username: '用户名',
    password: '密码',
    email: '邮箱',
    rememberMe: '记住我',
    // forgotPassword: '忘记密码？', // 忘记密码功能暂时注释掉 - 2025-01-15
    loginSuccess: '登录成功',
    logoutSuccess: '退出成功',
    registerSuccess: '注册成功'
  },

  // 漏洞学习
  vulnerability: {
    title: '漏洞知识中心',
    learn: '学习',
    demo: '演示',
    test: '测试',
    challenge: '挑战',
    description: '漏洞描述',
    harm: '危害分析',
    prevention: '防护措施',
    code: '代码示例',
    flowchart: '攻击流程图'
  },

  // 挑战模式
  challenge: {
    title: '挑战模式',
    integratedChallenges: '综合挑战场景',
    selectDifficulty: '选择难度',
    all: '全部',
    beginner: '初级',
    intermediate: '中级',
    advanced: '高级',
    expert: '专家',
    minutes: '分钟',
    points: '分',
    start: '开始挑战',
    submit: '提交答案',
    reset: '重置挑战',
    progress: '挑战进度',
    score: '得分'
    // ranking: '排行榜' // 排行榜功能暂时注释掉 - 2025-01-15
  },

  // 知识测试
  test: {
    title: '知识测试',
    description: '通过测试检验您的安全知识掌握程度',
    selectMode: '选择测试模式',
    selectModeDescription: '请选择适合您的测试模式',
    realtimeMode: '实时反馈模式',
    realtimeModeDesc: '逐题实时反馈，适合学习巩固',
    examMode: '考试模式',
    examModeDesc: '完整答题后统一分析，适合能力测试',
    randomMode: '随机综合模式',
    randomModeDesc: '全类型随机出题，适合综合练习',
    totalTests: '总测试次数',
    passedTests: '通过次数',
    averageScore: '平均分数',
    averageGrade: '平均成绩',
    selectCategory: '选择测试分类',
    startTest: '开始测试',
    start: '开始测试',
    submit: '提交答案',
    result: '测试结果',
    score: '得分',
    correct: '正确',
    wrong: '错误',
    explanation: '解析'
  },

  // 个人中心
  profile: {
    title: '个人中心',
    description: '管理您的个人信息和学习数据',
    basicInfo: '基本信息',
    studyTime: '学习时长(分钟)',
    completedVulnerabilities: '完成漏洞',
    totalPoints: '总积分',
    learningProgress: '学习进度',
    achievements: '成就徽章',
    notes: '学习笔记',
    favorites: '我的收藏',
    settings: '账户设置'
  },

  // 仪表板
  dashboard: {
    title: '控制台',
    welcome: '欢迎回来，{name}！',
    learnedVulnerabilities: '已学习漏洞',
    passedTests: '通过测试',
    completedChallenges: '完成挑战',
    totalPoints: '总积分',
    learningProgress: '学习进度',
    owaspProgress: 'OWASP Top 10 学习进度',
    overallProgress: '总体学习进度',
    quickNavigation: '快速导航',
    startLearning: '开始学习',
    knowledgeTest: '知识测试',
    challengeMode: '挑战模式',
    vulnerabilityDemo: '漏洞演示',
    learningCalendar: '学习日历',
    consecutiveDays: '连续学习天数',
    calendarComingSoon: '学习日历功能即将上线',
    recentActivity: '最近活动',
    noRecentActivity: '暂无最近活动',
    activity: {
      sqlInjectionPreview: '完成了 SQL 注入课程预习',
      xssTestAttempt: '尝试 XSS 漏洞知识测试',
      testPassed: '通过了 {testName}',
      testCompleted: '完成了 {testName}',
      twoHoursAgo: '2 小时前',
      oneDayAgo: '1 天前',
      daysAgo: '{days} 天前',
      hoursAgo: '{hours} 小时前',
      justNow: '刚刚'
    }
  },

  // 知识中心
  knowledge: {
    title: '漏洞知识中心',
    description: '系统化学习Web安全漏洞知识',
    vulnerabilityTypes: '漏洞类型',
    completed: '已完成',
    studyTime: '学习时长(分钟)',
    completionRate: '完成率',
    owaspTop10: 'OWASP Top 10 漏洞分类',
    categories: '10个分类',
    vulnerabilityCount: '漏洞数量',
    completionProgress: '完成进度',
    startLearning: '开始学习',
    viewDetails: '查看详情',
    a01: {
      name: '访问控制失效',
      description: '学习访问控制相关的安全漏洞'
    },
    a02: {
      name: '加密失败',
      description: '了解加密算法和密钥管理'
    },
    a03: {
      name: '注入漏洞',
      description: 'SQL注入、NoSQL注入等'
    },
    a04: {
      name: '不安全设计',
      description: '设计缺陷导致的安全问题'
    },
    a05: {
      name: '安全配置错误',
      description: '配置不当引起的安全风险'
    },
    a06: {
      name: '过时的组件',
      description: '使用过时组件带来的风险'
    },
    a07: {
      name: '身份验证失败',
      description: '身份认证和授权问题'
    },
    a08: {
      name: '软件和数据完整性失效',
      description: '数据完整性问题'
    },
    a09: {
      name: '安全日志记录和监控失效',
      description: '日志和监控相关问题'
    },
    a10: {
      name: '服务器请求伪造',
      description: 'SSRF攻击相关漏洞'
    }
  },

  // 漏洞详情页面
  vulnDetail: {
    back: '返回',
    tab: {
      theory: '理论知识',
      knowledge: '知识讲解',
      demo: '漏洞演示',
      repair: '修复建议'
    },
    tabDesc: {
      theory: '学习漏洞的基本概念和原理',
      knowledge: '深入了解漏洞的技术细节',
      demo: '通过实际演示理解漏洞利用',
      repair: '学习如何修复和防护漏洞'
    },
    section: {
      definitionPrinciple: '漏洞定义与原理',
      harmAnalysis: '危害场景分析',
      learningObjectives: '学习目标',
      typesExplanation: '漏洞类型详解',
      attackScenarios: '攻击场景分析',
      technicalAnalysis: '技术原理分析'
    },
    label: {
      definition: '漏洞定义',
      principle: '技术原理',
      attackVector: '攻击向量',
      realCase: '实际案例',
      typicalExamples: '典型示例',
      attackSteps: '攻击步骤'
    },
    subsection: {
      causes: '漏洞成因',
      techniques: '攻击技术',
      detection: '检测方法'
    }
  },

  // 题目导航器
  navigator: {
    title: '题目导航',
    answered: '已答',
    correct: '正确',
    wrong: '错误',
    unanswered: '未答',
    currentQuestion: '当前题目',
    answeredCorrect: '答对',
    answeredWrong: '答错',
    visited: '已访问',
    notVisited: '未访问',
    grid: '网格',
    list: '列表',
    type: {
      SINGLE: '单选题',
      MULTIPLE: '多选题',
      JUDGE: '判断题',
      FILL_BLANK: '填空题'
    },
    tooltip: {
      questionIndex: '第{index}题',
      category: '分类：{code}',
      type: '类型：{type}',
      visitedUnanswered: '已访问未答'
    }
  }
}
