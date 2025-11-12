package com.javaweb.security.controller;

import com.javaweb.security.common.result.ApiResult;
import com.javaweb.security.entity.Question;
import com.javaweb.security.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 题目管理控制器
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "题目管理", description = "题目相关API")
public class QuestionController {

  private final QuestionService questionService;

  /** 获取题目列表 */
  @GetMapping
  @Operation(summary = "获取题目列表", description = "根据分类获取题目列表")
  public ResponseEntity<ApiResult<Page<Question>>> getQuestions(
      @RequestParam String categoryCode, Pageable pageable) {
    try {
      Page<Question> questions = questionService.getQuestions(categoryCode, pageable);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("获取题目列表失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取题目列表失败: " + e.getMessage()));
    }
  }

  /** 获取题目详情 */
  @GetMapping("/{questionId}")
  @Operation(summary = "获取题目详情", description = "根据ID获取题目详情")
  public ResponseEntity<ApiResult<Question>> getQuestion(@PathVariable Long questionId) {
    try {
      Question question = questionService.getQuestion(questionId);
      return ResponseEntity.ok(ApiResult.success(question));
    } catch (Exception e) {
      log.error("获取题目详情失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取题目详情失败: " + e.getMessage()));
    }
  }

  /** 根据类型获取题目 */
  @GetMapping("/by-type")
  @Operation(summary = "根据类型获取题目", description = "根据题目类型获取题目列表")
  public ResponseEntity<ApiResult<Page<Question>>> getQuestionsByType(
      @RequestParam String categoryCode,
      @RequestParam Question.QuestionType questionType,
      Pageable pageable) {
    try {
      Page<Question> questions =
          questionService.getQuestionsByType(categoryCode, questionType, pageable);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据类型获取题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("根据类型获取题目失败: " + e.getMessage()));
    }
  }

  /** 根据难度获取题目 */
  @GetMapping("/by-difficulty")
  @Operation(summary = "根据难度获取题目", description = "根据难度等级获取题目列表")
  public ResponseEntity<ApiResult<Page<Question>>> getQuestionsByDifficulty(
      @RequestParam String categoryCode,
      @RequestParam Question.Difficulty difficulty,
      Pageable pageable) {
    try {
      Page<Question> questions =
          questionService.getQuestionsByDifficulty(categoryCode, difficulty, pageable);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据难度获取题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("根据难度获取题目失败: " + e.getMessage()));
    }
  }

  /** 根据知识点来源获取题目 */
  @GetMapping("/by-knowledge-source")
  @Operation(summary = "根据知识点来源获取题目", description = "根据知识点来源获取题目列表")
  public ResponseEntity<ApiResult<Page<Question>>> getQuestionsByKnowledgeSource(
      @RequestParam String categoryCode,
      @RequestParam Question.KnowledgeSource knowledgeSource,
      Pageable pageable) {
    try {
      Page<Question> questions =
          questionService.getQuestionsByKnowledgeSource(categoryCode, knowledgeSource, pageable);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据知识点来源获取题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("根据知识点来源获取题目失败: " + e.getMessage()));
    }
  }

  /** 获取随机题目 */
  @GetMapping("/random")
  @Operation(summary = "获取随机题目", description = "获取指定分类的随机题目")
  public ResponseEntity<ApiResult<List<Question>>> getRandomQuestions(
      @RequestParam String categoryCode, @RequestParam(defaultValue = "24") int count) {
    try {
      List<Question> questions = questionService.getRandomQuestions(categoryCode, count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("获取随机题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取随机题目失败: " + e.getMessage()));
    }
  }

  /** 根据类型获取随机题目 */
  @GetMapping("/random/by-type")
  @Operation(summary = "根据类型获取随机题目", description = "根据题目类型获取随机题目")
  public ResponseEntity<ApiResult<List<Question>>> getRandomQuestionsByType(
      @RequestParam String categoryCode,
      @RequestParam Question.QuestionType questionType,
      @RequestParam(defaultValue = "8") int count) {
    try {
      List<Question> questions =
          questionService.getRandomQuestionsByType(categoryCode, questionType, count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据类型获取随机题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("根据类型获取随机题目失败: " + e.getMessage()));
    }
  }

  /** 根据难度获取随机题目 */
  @GetMapping("/random/by-difficulty")
  @Operation(summary = "根据难度获取随机题目", description = "根据难度等级获取随机题目")
  public ResponseEntity<ApiResult<List<Question>>> getRandomQuestionsByDifficulty(
      @RequestParam String categoryCode,
      @RequestParam Question.Difficulty difficulty,
      @RequestParam(defaultValue = "8") int count) {
    try {
      List<Question> questions =
          questionService.getRandomQuestionsByDifficulty(categoryCode, difficulty, count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据难度获取随机题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("根据难度获取随机题目失败: " + e.getMessage()));
    }
  }

  /** 根据知识点来源获取随机题目 */
  @GetMapping("/random/by-knowledge-source")
  @Operation(summary = "根据知识点来源获取随机题目", description = "根据知识点来源获取随机题目")
  public ResponseEntity<ApiResult<List<Question>>> getRandomQuestionsByKnowledgeSource(
      @RequestParam String categoryCode,
      @RequestParam Question.KnowledgeSource knowledgeSource,
      @RequestParam(defaultValue = "8") int count) {
    try {
      List<Question> questions =
          questionService.getRandomQuestionsByKnowledgeSource(categoryCode, knowledgeSource, count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据知识点来源获取随机题目失败", e);
      return ResponseEntity.badRequest()
          .body(ApiResult.error("根据知识点来源获取随机题目失败: " + e.getMessage()));
    }
  }

  /** 获取跨分类随机题目 */
  @GetMapping("/random/across-categories")
  @Operation(summary = "获取跨分类随机题目", description = "从所有分类中随机获取题目")
  public ResponseEntity<ApiResult<List<Question>>> getRandomQuestionsAcrossCategories(
      @RequestParam(defaultValue = "24") int count) {
    try {
      List<Question> questions = questionService.getRandomQuestionsAcrossCategories(count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("获取跨分类随机题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取跨分类随机题目失败: " + e.getMessage()));
    }
  }

  /** 根据类型获取跨分类随机题目 */
  @GetMapping("/random/across-categories/by-type")
  @Operation(summary = "根据类型获取跨分类随机题目", description = "根据题目类型从所有分类中随机获取题目")
  public ResponseEntity<ApiResult<List<Question>>> getRandomQuestionsByTypeAcrossCategories(
      @RequestParam Question.QuestionType questionType,
      @RequestParam(defaultValue = "8") int count) {
    try {
      List<Question> questions =
          questionService.getRandomQuestionsByTypeAcrossCategories(questionType, count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据类型获取跨分类随机题目失败", e);
      return ResponseEntity.badRequest()
          .body(ApiResult.error("根据类型获取跨分类随机题目失败: " + e.getMessage()));
    }
  }

  /** 根据难度获取跨分类随机题目 */
  @GetMapping("/random/across-categories/by-difficulty")
  @Operation(summary = "根据难度获取跨分类随机题目", description = "根据难度等级从所有分类中随机获取题目")
  public ResponseEntity<ApiResult<List<Question>>> getRandomQuestionsByDifficultyAcrossCategories(
      @RequestParam Question.Difficulty difficulty, @RequestParam(defaultValue = "8") int count) {
    try {
      List<Question> questions =
          questionService.getRandomQuestionsByDifficultyAcrossCategories(difficulty, count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据难度获取跨分类随机题目失败", e);
      return ResponseEntity.badRequest()
          .body(ApiResult.error("根据难度获取跨分类随机题目失败: " + e.getMessage()));
    }
  }

  /** 根据知识点来源获取跨分类随机题目 */
  @GetMapping("/random/across-categories/by-knowledge-source")
  @Operation(summary = "根据知识点来源获取跨分类随机题目", description = "根据知识点来源从所有分类中随机获取题目")
  public ResponseEntity<ApiResult<List<Question>>>
      getRandomQuestionsByKnowledgeSourceAcrossCategories(
          @RequestParam Question.KnowledgeSource knowledgeSource,
          @RequestParam(defaultValue = "8") int count) {
    try {
      List<Question> questions =
          questionService.getRandomQuestionsByKnowledgeSourceAcrossCategories(
              knowledgeSource, count);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据知识点来源获取跨分类随机题目失败", e);
      return ResponseEntity.badRequest()
          .body(ApiResult.error("根据知识点来源获取跨分类随机题目失败: " + e.getMessage()));
    }
  }

  /** 搜索题目 */
  @GetMapping("/search")
  @Operation(summary = "搜索题目", description = "根据关键词搜索题目")
  public ResponseEntity<ApiResult<Page<Question>>> searchQuestions(
      @RequestParam String keyword, Pageable pageable) {
    try {
      Page<Question> questions = questionService.searchQuestions(keyword, pageable);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("搜索题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("搜索题目失败: " + e.getMessage()));
    }
  }

  /** 根据标签搜索题目 */
  @GetMapping("/search/by-tag")
  @Operation(summary = "根据标签搜索题目", description = "根据标签搜索题目")
  public ResponseEntity<ApiResult<Page<Question>>> searchQuestionsByTag(
      @RequestParam String tag, Pageable pageable) {
    try {
      Page<Question> questions = questionService.searchQuestionsByTag(tag, pageable);
      return ResponseEntity.ok(ApiResult.success(questions));
    } catch (Exception e) {
      log.error("根据标签搜索题目失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("根据标签搜索题目失败: " + e.getMessage()));
    }
  }

  /** 获取题目统计 */
  @GetMapping("/statistics")
  @Operation(summary = "获取题目统计", description = "获取指定分类的题目统计信息")
  public ResponseEntity<ApiResult<Map<String, Object>>> getQuestionStatistics(
      @RequestParam String categoryCode) {
    try {
      Map<String, Object> statistics = questionService.getQuestionStatistics(categoryCode);
      return ResponseEntity.ok(ApiResult.success(statistics));
    } catch (Exception e) {
      log.error("获取题目统计失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取题目统计失败: " + e.getMessage()));
    }
  }

  /** 获取题目类型分布 */
  @GetMapping("/type-distribution")
  @Operation(summary = "获取题目类型分布", description = "获取指定分类的题目类型分布")
  public ResponseEntity<ApiResult<List<Map<String, Object>>>> getQuestionTypeDistribution(
      @RequestParam String categoryCode) {
    try {
      List<Map<String, Object>> distribution =
          questionService.getQuestionTypeDistribution(categoryCode);
      return ResponseEntity.ok(ApiResult.success(distribution));
    } catch (Exception e) {
      log.error("获取题目类型分布失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取题目类型分布失败: " + e.getMessage()));
    }
  }

  /** 获取难度分布 */
  @GetMapping("/difficulty-distribution")
  @Operation(summary = "获取难度分布", description = "获取指定分类的难度分布")
  public ResponseEntity<ApiResult<List<Map<String, Object>>>> getDifficultyDistribution(
      @RequestParam String categoryCode) {
    try {
      List<Map<String, Object>> distribution =
          questionService.getDifficultyDistribution(categoryCode);
      return ResponseEntity.ok(ApiResult.success(distribution));
    } catch (Exception e) {
      log.error("获取难度分布失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取难度分布失败: " + e.getMessage()));
    }
  }

  /** 获取知识点来源分布 */
  @GetMapping("/knowledge-source-distribution")
  @Operation(summary = "获取知识点来源分布", description = "获取指定分类的知识点来源分布")
  public ResponseEntity<ApiResult<List<Map<String, Object>>>> getKnowledgeSourceDistribution(
      @RequestParam String categoryCode) {
    try {
      List<Map<String, Object>> distribution =
          questionService.getKnowledgeSourceDistribution(categoryCode);
      return ResponseEntity.ok(ApiResult.success(distribution));
    } catch (Exception e) {
      log.error("获取知识点来源分布失败", e);
      return ResponseEntity.badRequest().body(ApiResult.error("获取知识点来源分布失败: " + e.getMessage()));
    }
  }
}
