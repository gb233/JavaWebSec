package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.Question;
import com.javaweb.security.entity.TestAnswer;
import com.javaweb.security.entity.TestSession;
import com.javaweb.security.entity.UserTestRecord;
import com.javaweb.security.service.AuthenticationService;
import com.javaweb.security.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 测试管理控制器
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "测试管理", description = "知识测试功能相关API")
public class TestController {

  private final TestService testService;
  private final AuthenticationService authenticationService;

  /** 开始测试会话 */
  @PostMapping("/start")
  @Operation(summary = "开始测试会话", description = "开始新的测试会话")
  public ResponseEntity<ApiResult<TestSession>> startTestSession(
      @RequestParam String modeCode, @RequestParam(required = false) String categoryCode) {
    try {
      Long userId = authenticationService.getCurrentUserId();
      if (userId == null) {
        return ResponseEntity.status(401).body(ApiResult.error("未登录或会话已过期"));
      }

      log.info("开始测试会话 - 用户ID: {}, 模式: {}, 分类: {}", userId, modeCode, categoryCode);

      TestSession session = testService.startTestSession(userId, modeCode, categoryCode);
      return ResponseEntity.ok(ApiResult.success(session));
    } catch (Exception e) {
      log.error("开始测试会话失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("开始测试会话失败: " + e.getMessage()));
    }
  }

  /** 获取测试会话信息 */
  @GetMapping("/session/{sessionCode}")
  @Operation(summary = "获取测试会话信息", description = "根据会话代码获取测试会话信息")
  public ResponseEntity<ApiResult<TestSession>> getTestSession(@PathVariable String sessionCode) {
    try {
      TestSession session = testService.getTestSession(sessionCode);
      return ResponseEntity.ok(ApiResult.success(session));
    } catch (Exception e) {
      log.error("获取测试会话失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取测试会话失败: " + e.getMessage()));
    }
  }

  /** 获取测试题目列表 */
  @GetMapping("/session/{sessionCode}/questions")
  @Operation(summary = "获取测试题目列表", description = "获取指定会话的题目列表")
  public ResponseEntity<ApiResult<List<Question>>> getTestQuestions(
      @PathVariable String sessionCode) {
    try {
      List<Question> questions = testService.getTestQuestions(sessionCode);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("获取测试题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取测试题目失败: " + e.getMessage()));
    }
  }

  /** 获取当前题目 */
  @GetMapping("/session/{sessionCode}/question/{questionIndex}")
  @Operation(summary = "获取当前题目", description = "获取指定索引的题目")
  public ResponseEntity<ApiResult<Question>> getCurrentQuestion(
      @PathVariable String sessionCode, @PathVariable Integer questionIndex) {
    try {
      Question question = testService.getCurrentQuestion(sessionCode, questionIndex);
      return ResponseEntity.ok(ApiResult.success(question));
    } catch (Exception e) {
      log.error("获取当前题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取当前题目失败: " + e.getMessage()));
    }
  }

  /** 提交答案（支持多种测试模式） */
  @PostMapping("/session/{sessionCode}/answer")
  @Operation(summary = "提交答案", description = "提交单题答案并根据测试模式返回相应反馈")
  public ResponseEntity<ApiResult<Map<String, Object>>> submitAnswer(
      @PathVariable String sessionCode,
      @RequestParam Long questionId,
      @RequestParam String userAnswer) {
    try {
      // 获取测试会话信息
      TestSession session = testService.getTestSession(sessionCode);
      String modeCode = session.getModeCode();

      TestAnswer answer = testService.submitAnswer(sessionCode, questionId, userAnswer);
      Question question = testService.getQuestionById(questionId);

      // 构建基础响应数据
      Map<String, Object> responseData = new HashMap<>();
      responseData.put("id", answer.getId());
      responseData.put("sessionId", answer.getSessionId());
      responseData.put("questionId", answer.getQuestionId());
      responseData.put("questionIndex", answer.getQuestionIndex());
      responseData.put("userAnswer", answer.getUserAnswer());
      responseData.put("isCorrect", answer.getIsCorrect());
      responseData.put("score", answer.getScore());
      responseData.put("feedbackShown", answer.getFeedbackShown());
      responseData.put("answeredAt", answer.getAnsweredAt());
      responseData.put("answered", answer.isAnswered());

      // 根据测试模式调整返回数据
      switch (modeCode) {
        case "realtime":
        case "REALTIME_FEEDBACK":
          // 实时反馈模式：返回完整反馈信息
          responseData.put("correctAnswer", question.getCorrectAnswer());
          responseData.put("explanation", question.getExplanation());
          responseData.put("showFeedback", true);
          responseData.put("feedbackType", "detailed");
          break;

        case "exam":
        case "EXAM_MODE":
          // 考试模式：不返回正确答案和解析
          responseData.put("showFeedback", false);
          responseData.put("feedbackType", "none");
          break;

        case "random":
        case "RANDOM_COMPREHENSIVE":
          // 随机综合模式：返回简单反馈
          responseData.put("correctAnswer", question.getCorrectAnswer());
          responseData.put("explanation", question.getExplanation());
          responseData.put("showFeedback", true);
          responseData.put("feedbackType", "simple");
          break;

        default:
          // 默认按实时反馈模式处理
          responseData.put("correctAnswer", question.getCorrectAnswer());
          responseData.put("explanation", question.getExplanation());
          responseData.put("showFeedback", true);
          responseData.put("feedbackType", "detailed");
      }

      return ResponseEntity.ok(ApiResult.success(responseData));
    } catch (Exception e) {
      log.error("提交答案失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("提交答案失败: " + e.getMessage()));
    }
  }

  /** 批量提交答案（考试模式） */
  @PostMapping("/session/{sessionCode}/answers")
  @Operation(summary = "批量提交答案", description = "批量提交所有答案")
  public ResponseEntity<ApiResult<List<TestAnswer>>> submitAnswers(
      @PathVariable String sessionCode, @RequestBody Map<Long, String> answers) {
    try {
      List<TestAnswer> result = testService.submitAnswers(sessionCode, answers);
      return ResponseEntity.ok(ApiResult.success(result));
    } catch (Exception e) {
      log.error("批量提交答案失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("批量提交答案失败: " + e.getMessage()));
    }
  }

  /** 获取答题反馈 */
  @GetMapping("/session/{sessionCode}/answer/{questionId}")
  @Operation(summary = "获取答题反馈", description = "获取指定题目的答题反馈")
  public ResponseEntity<ApiResult<TestAnswer>> getAnswerFeedback(
      @PathVariable String sessionCode, @PathVariable Long questionId) {
    try {
      TestAnswer answer = testService.getAnswerFeedback(sessionCode, questionId);
      return ResponseEntity.ok(ApiResult.success(answer));
    } catch (Exception e) {
      log.error("获取答题反馈失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取答题反馈失败: " + e.getMessage()));
    }
  }

  /** 结束测试会话 */
  @PostMapping("/session/{sessionCode}/end")
  @Operation(summary = "结束测试会话", description = "结束测试会话并计算最终结果")
  public ResponseEntity<ApiResult<TestSession>> endTestSession(@PathVariable String sessionCode) {
    try {
      TestSession session = testService.endTestSession(sessionCode);
      return ResponseEntity.ok(ApiResult.success(session));
    } catch (Exception e) {
      log.error("结束测试会话失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("结束测试会话失败: " + e.getMessage()));
    }
  }

  /** 获取测试结果 */
  @GetMapping("/session/{sessionCode}/result")
  @Operation(summary = "获取测试结果", description = "获取测试的详细结果")
  public ResponseEntity<ApiResult<Map<String, Object>>> getTestResult(
      @PathVariable String sessionCode) {
    try {
      Map<String, Object> result = testService.getTestResult(sessionCode);
      return ResponseEntity.ok(ApiResult.success(result));
    } catch (Exception e) {
      log.error("获取测试结果失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取测试结果失败: " + e.getMessage()));
    }
  }

  /** 获取用户测试记录 */
  @GetMapping("/records")
  @Operation(summary = "获取用户测试记录", description = "获取当前用户的测试记录")
  public ResponseEntity<ApiResult<Page<UserTestRecord>>> getUserTestRecords(Pageable pageable) {
    try {
      Long userId = authenticationService.getCurrentUserId();
      if (userId == null) {
        return ResponseEntity.status(401).body(ApiResult.error("未登录或会话已过期"));
      }

      Page<UserTestRecord> records = testService.getUserTestRecords(userId, pageable);
      return ResponseEntity.ok(ApiResult.success(records));
    } catch (Exception e) {
      log.error("获取用户测试记录失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取用户测试记录失败: " + e.getMessage()));
    }
  }

  /** 获取用户测试统计 */
  @GetMapping("/statistics")
  @Operation(summary = "获取用户测试统计", description = "获取当前用户的测试统计信息")
  public ResponseEntity<ApiResult<Map<String, Object>>> getUserTestStatistics() {
    try {
      Long userId = authenticationService.getCurrentUserId();
      if (userId == null) {
        return ResponseEntity.status(401).body(ApiResult.error("未登录或会话已过期"));
      }

      Map<String, Object> statistics = testService.getUserTestStatistics(userId);
      return ResponseEntity.ok(ApiResult.success(statistics));
    } catch (Exception e) {
      log.error("获取用户测试统计失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取用户测试统计失败: " + e.getMessage()));
    }
  }

  /** 获取测试会话历史 */
  @GetMapping("/sessions/history")
  @Operation(summary = "获取测试会话历史", description = "获取当前用户的测试会话历史")
  public ResponseEntity<ApiResult<Page<TestSession>>> getTestSessionHistory(Pageable pageable) {
    try {
      Long userId = authenticationService.getCurrentUserId();
      if (userId == null) {
        return ResponseEntity.status(401).body(ApiResult.error("未登录或会话已过期"));
      }

      Page<TestSession> sessions = testService.getTestSessionHistory(userId, pageable);
      return ResponseEntity.ok(ApiResult.success(sessions));
    } catch (Exception e) {
      log.error("获取测试会话历史失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取测试会话历史失败: " + e.getMessage()));
    }
  }

  /** 获取测试会话详情 */
  @GetMapping("/sessions/{sessionCode}/details")
  @Operation(summary = "获取测试会话详情", description = "获取指定测试会话的详细信息")
  public ResponseEntity<ApiResult<Map<String, Object>>> getTestSessionDetails(
      @PathVariable String sessionCode) {
    try {
      Map<String, Object> details = testService.getTestSessionDetails(sessionCode);
      return ResponseEntity.ok(ApiResult.success(details));
    } catch (Exception e) {
      log.error("获取测试会话详情失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取测试会话详情失败: " + e.getMessage()));
    }
  }

  /** 获取测试记录详情 */
  @GetMapping("/records/{recordId}")
  @Operation(summary = "获取测试记录详情", description = "获取指定测试记录的详细信息")
  public ResponseEntity<ApiResult<Map<String, Object>>> getTestRecordDetail(
      @PathVariable Long recordId) {
    try {
      Long userId = authenticationService.getCurrentUserId();
      if (userId == null) {
        return ResponseEntity.status(401).body(ApiResult.error("未登录或会话已过期"));
      }

      Map<String, Object> details = testService.getTestRecordDetail(recordId, userId);
      return ResponseEntity.ok(ApiResult.success(details));
    } catch (Exception e) {
      log.error("获取测试记录详情失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取测试记录详情失败: " + e.getMessage()));
    }
  }
}
