export default {
  // Common
  common: {
    title: 'Java Web Security Teaching System',
    confirm: 'Confirm',
    cancel: 'Cancel',
    save: 'Save',
    delete: 'Delete',
    edit: 'Edit',
    add: 'Add',
    search: 'Search',
    reset: 'Reset',
    submit: 'Submit',
    back: 'Back',
    next: 'Next',
    previous: 'Previous',
    complete: 'Complete',
    skip: 'Skip',
    loading: 'Loading...',
    success: 'Operation successful',
    error: 'Operation failed',
    warning: 'Warning',
    info: 'Info'
  },

  // Navigation
  nav: {
    home: 'Home',
    dashboard: 'Dashboard',
    vulnerability: 'Vulnerability Center',
    knowledgeCenter: 'Knowledge Center',
    challenge: 'Challenge Mode',
    challengeList: 'Challenge List',
    // leaderboard: 'Leaderboard', // 排行榜功能暂时注释掉 - 2025-01-15
    test: 'Knowledge Test',
    testCategories: 'Test Categories',
    testRecords: 'Test Records',
    features: 'Feature Test',
    simpleTest: 'Simple Test',
    basicTest: 'Basic Test',
    helloTest: 'Hello Test',
    profile: 'User Profile',
    settings: 'Settings',
    logout: 'Logout'
  },

  // Language switch
  language: {
    switch: 'Switch Language',
    chinese: '简体中文',
    english: 'English',
    current: 'Current Language'
  },

  // User guide
  guide: {
    title: 'New User Guide',
    welcome: 'Welcome to Java Web Security Teaching System',
    description: 'Let\'s start exploring this security learning platform!',
    previous: 'Previous',
    next: 'Next',
    complete: 'Complete Guide',
    skip: 'Skip Guide',
    trigger: 'New User Guide',
    navigation: 'Navigation Menu',
    vulnerabilityCenter: 'Vulnerability Knowledge Center',
    challengeMode: 'Challenge Mode',
    knowledgeTest: 'Knowledge Test',
    userProfile: 'User Profile',
    languageSwitch: 'Language Switch',
    guideTrigger: 'New User Guide'
  },

  // User authentication
  auth: {
    login: 'Login',
    register: 'Register',
    logout: 'Logout',
    username: 'Username',
    password: 'Password',
    email: 'Email',
    rememberMe: 'Remember Me',
    // forgotPassword: 'Forgot Password?', // 忘记密码功能暂时注释掉 - 2025-01-15
    loginSuccess: 'Login successful',
    logoutSuccess: 'Logout successful',
    registerSuccess: 'Registration successful'
  },

  // Vulnerability learning
  vulnerability: {
    title: 'Vulnerability Knowledge Center',
    learn: 'Learn',
    demo: 'Demo',
    test: 'Test',
    challenge: 'Challenge',
    description: 'Vulnerability Description',
    harm: 'Harm Analysis',
    prevention: 'Prevention Measures',
    code: 'Code Example',
    flowchart: 'Attack Flowchart'
  },

  // Challenge mode
  challenge: {
    title: 'Challenge Mode',
    integratedChallenges: 'Integrated Challenge Scenarios',
    selectDifficulty: 'Select Difficulty',
    all: 'All',
    beginner: 'Beginner',
    intermediate: 'Intermediate',
    advanced: 'Advanced',
    expert: 'Expert',
    minutes: 'minutes',
    points: 'points',
    start: 'Start Challenge',
    submit: 'Submit Answer',
    reset: 'Reset Challenge',
    progress: 'Challenge Progress',
    score: 'Score',
    ranking: 'Ranking'
  },

  // Knowledge test
  test: {
    title: 'Knowledge Test',
    description: 'Assess your security knowledge through testing',
    selectMode: 'Select Test Mode',
    selectModeDescription: 'Please select your suitable test mode',
    realtimeMode: 'Real-time Feedback Mode',
    realtimeModeDesc: 'Real-time feedback per question, suitable for learning and consolidation',
    examMode: 'Exam Mode',
    examModeDesc: 'Unified analysis after completing all questions, suitable for ability testing',
    randomMode: 'Random Comprehensive Mode',
    randomModeDesc: 'Random questions of all types, suitable for comprehensive practice',
    totalTests: 'Total Test Attempts',
    passedTests: 'Pass Attempts',
    averageScore: 'Average Score',
    averageGrade: 'Average Grade',
    selectCategory: 'Select Test Category',
    startTest: 'Start Test',
    start: 'Start Test',
    submit: 'Submit Answer',
    result: 'Test Result',
    score: 'Score',
    correct: 'Correct',
    wrong: 'Wrong',
    explanation: 'Explanation'
  },

  // User profile
  profile: {
    title: 'User Profile',
    description: 'Manage your personal information and learning data',
    basicInfo: 'Basic Information',
    studyTime: 'Study Time (minutes)',
    completedVulnerabilities: 'Completed Vulnerabilities',
    totalPoints: 'Total Points',
    learningProgress: 'Learning Progress',
    achievements: 'Achievements',
    notes: 'Learning Notes',
    favorites: 'My Favorites',
    settings: 'Account Settings'
  },

  // Dashboard
  dashboard: {
    title: 'Dashboard',
    welcome: 'Welcome back, {name}!',
    learnedVulnerabilities: 'Learned Vulnerabilities',
    passedTests: 'Passed Tests',
    completedChallenges: 'Completed Challenges',
    totalPoints: 'Total Points',
    learningProgress: 'Learning Progress',
    owaspProgress: 'OWASP Top 10 Learning Progress',
    overallProgress: 'Overall Learning Progress',
    quickNavigation: 'Quick Navigation',
    startLearning: 'Start Learning',
    knowledgeTest: 'Knowledge Test',
    challengeMode: 'Challenge Mode',
    vulnerabilityDemo: 'Vulnerability Demo',
    learningCalendar: 'Learning Calendar',
    consecutiveDays: 'Consecutive Learning Days',
    calendarComingSoon: 'Learning calendar feature coming soon',
    recentActivity: 'Recent Activity',
    noRecentActivity: 'No recent activity',
    activity: {
      sqlInjectionPreview: 'Completed SQL injection course preview',
      xssTestAttempt: 'Attempted XSS vulnerability knowledge test',
      testPassed: 'Passed {testName}',
      testCompleted: 'Completed {testName}',
      twoHoursAgo: '2 hours ago',
      oneDayAgo: '1 day ago',
      daysAgo: '{days} days ago',
      hoursAgo: '{hours} hours ago',
      justNow: 'Just now'
    }
  },

  // Knowledge Center
  knowledge: {
    title: 'Vulnerability Knowledge Center',
    description: 'Systematic learning of web security vulnerability knowledge',
    vulnerabilityTypes: 'Vulnerability Types',
    completed: 'Completed',
    studyTime: 'Study Time (minutes)',
    completionRate: 'Completion Rate',
    owaspTop10: 'OWASP Top 10 Vulnerability Classification',
    categories: '10 Categories',
    vulnerabilityCount: 'Vulnerability Count',
    completionProgress: 'Completion Progress',
    startLearning: 'Start Learning',
    viewDetails: 'View Details',
    a01: {
      name: 'Broken Access Control',
      description: 'Learn about security vulnerabilities related to access control'
    },
    a02: {
      name: 'Cryptographic Failures',
      description: 'Understand encryption algorithms and key management'
    },
    a03: {
      name: 'Injection',
      description: 'SQL injection, NoSQL injection, etc.'
    },
    a04: {
      name: 'Insecure Design',
      description: 'Security issues caused by design flaws'
    },
    a05: {
      name: 'Security Misconfiguration',
      description: 'Security risks caused by improper configuration'
    },
    a06: {
      name: 'Vulnerable and Outdated Components',
      description: 'Risks brought by using outdated components'
    },
    a07: {
      name: 'Identification and Authentication Failures',
      description: 'Authentication and authorization issues'
    },
    a08: {
      name: 'Software and Data Integrity Failures',
      description: 'Data integrity issues'
    },
    a09: {
      name: 'Security Logging and Monitoring Failures',
      description: 'Logging and monitoring related issues'
    },
    a10: {
      name: 'Server-Side Request Forgery',
      description: 'SSRF attack related vulnerabilities'
    }
  },

  // Vulnerability Detail Page
  vulnDetail: {
    back: 'Back',
    tab: {
      theory: 'Theoretical Knowledge',
      knowledge: 'Knowledge Explanation',
      demo: 'Vulnerability Demo',
      repair: 'Repair Suggestions'
    },
    tabDesc: {
      theory: 'Learn basic concepts and principles of vulnerabilities',
      knowledge: 'Deep dive into technical details of vulnerabilities',
      demo: 'Understand vulnerability exploitation through practical demonstrations',
      repair: 'Learn how to fix and protect against vulnerabilities'
    },
    section: {
      definitionPrinciple: 'Vulnerability Definition and Principles',
      harmAnalysis: 'Harm Scenario Analysis',
      learningObjectives: 'Learning Objectives',
      typesExplanation: 'Vulnerability Types Explanation',
      attackScenarios: 'Attack Scenarios Analysis',
      technicalAnalysis: 'Technical Analysis'
    },
    label: {
      definition: 'Vulnerability Definition',
      principle: 'Technical Principles',
      attackVector: 'Attack Vector',
      realCase: 'Real Cases',
      typicalExamples: 'Typical Examples',
      attackSteps: 'Attack Steps'
    },
    subsection: {
      causes: 'Root Causes',
      techniques: 'Attack Techniques',
      detection: 'Detection Methods'
    }
  },

  // Question Navigator
  navigator: {
    title: 'Question Navigator',
    answered: 'Answered',
    correct: 'Correct',
    wrong: 'Wrong',
    unanswered: 'Unanswered',
    currentQuestion: 'Current Question',
    answeredCorrect: 'Correct',
    answeredWrong: 'Wrong',
    visited: 'Visited',
    notVisited: 'Not Visited',
    grid: 'Grid',
    list: 'List',
    type: {
      SINGLE: 'Single Choice',
      MULTIPLE: 'Multiple Choice',
      JUDGE: 'True/False',
      FILL_BLANK: 'Fill in the Blank'
    },
    tooltip: {
      questionIndex: 'Question {index}',
      category: 'Category: {code}',
      type: 'Type: {type}',
      visitedUnanswered: 'Visited Unanswered'
    }
  }
}
