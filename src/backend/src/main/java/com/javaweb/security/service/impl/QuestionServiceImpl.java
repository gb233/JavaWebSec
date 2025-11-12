package com.javaweb.security.service.impl;

import com.javaweb.security.entity.Question;
import com.javaweb.security.repository.QuestionRepository;
import com.javaweb.security.service.QuestionService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 题目服务实现类
 *
 * @author Java Web Security Teaching System
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {

  private final QuestionRepository questionRepository;

  public Question getQuestionById(Long questionId) {
    try {
      log.info("获取题目详情 - 题目ID: {}", questionId);
      return questionRepository
          .findById(questionId)
          .orElseThrow(() -> new RuntimeException("题目不存在"));
    } catch (Exception e) {
      log.error("获取题目详情失败", e);
      throw new RuntimeException("获取题目详情失败: " + e.getMessage());
    }
  }

  public List<Question> getQuestionsByCategory(String categoryCode) {
    try {
      log.info("获取分类题目列表 - 分类代码: {}", categoryCode);
      return questionRepository
          .findByCategoryCodeOrderByCreatedAtDesc(categoryCode, Pageable.unpaged())
          .getContent();
    } catch (Exception e) {
      log.error("获取分类题目列表失败", e);
      throw new RuntimeException("获取分类题目列表失败: " + e.getMessage());
    }
  }

  public List<Question> getRandomQuestions(int count) {
    try {
      log.info("获取随机题目 - 数量: {}", count);
      return questionRepository.findRandomQuestionsAcrossCategories(count);
    } catch (Exception e) {
      log.error("获取随机题目失败", e);
      throw new RuntimeException("获取随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public Page<Question> getQuestions(String categoryCode, Pageable pageable) {
    try {
      log.info("获取题目列表 - 分类代码: {}, 分页: {}", categoryCode, pageable);
      return questionRepository.findByCategoryCodeOrderByCreatedAtDesc(categoryCode, pageable);
    } catch (Exception e) {
      log.error("获取题目列表失败", e);
      throw new RuntimeException("获取题目列表失败: " + e.getMessage());
    }
  }

  @Override
  public Question getQuestion(Long questionId) {
    return getQuestionById(questionId);
  }

  @Override
  public Page<Question> getQuestionsByType(
      String categoryCode, Question.QuestionType questionType, Pageable pageable) {
    try {
      log.info("根据类型获取题目 - 分类: {}, 类型: {}", categoryCode, questionType);
      return questionRepository.findByCategoryCodeAndQuestionTypeOrderByCreatedAtDesc(
          categoryCode, questionType, pageable);
    } catch (Exception e) {
      log.error("根据类型获取题目失败", e);
      throw new RuntimeException("根据类型获取题目失败: " + e.getMessage());
    }
  }

  @Override
  public Page<Question> getQuestionsByDifficulty(
      String categoryCode, Question.Difficulty difficulty, Pageable pageable) {
    try {
      log.info("根据难度获取题目 - 分类: {}, 难度: {}", categoryCode, difficulty);
      return questionRepository.findByCategoryCodeAndDifficultyOrderByCreatedAtDesc(
          categoryCode, difficulty, pageable);
    } catch (Exception e) {
      log.error("根据难度获取题目失败", e);
      throw new RuntimeException("根据难度获取题目失败: " + e.getMessage());
    }
  }

  @Override
  public Page<Question> getQuestionsByKnowledgeSource(
      String categoryCode, Question.KnowledgeSource knowledgeSource, Pageable pageable) {
    try {
      log.info("根据知识点来源获取题目 - 分类: {}, 来源: {}", categoryCode, knowledgeSource);
      return questionRepository.findByCategoryCodeAndKnowledgeSourceOrderByCreatedAtDesc(
          categoryCode, knowledgeSource, pageable);
    } catch (Exception e) {
      log.error("根据知识点来源获取题目失败", e);
      throw new RuntimeException("根据知识点来源获取题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestions(String categoryCode, int count) {
    try {
      log.info("获取分类随机题目 - 分类: {}, 数量: {}", categoryCode, count);
      return questionRepository.findRandomQuestionsByCategory(categoryCode, count);
    } catch (Exception e) {
      log.error("获取分类随机题目失败", e);
      throw new RuntimeException("获取分类随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestionsByType(
      String categoryCode, Question.QuestionType questionType, int count) {
    try {
      log.info("根据类型获取随机题目 - 分类: {}, 类型: {}, 数量: {}", categoryCode, questionType, count);
      return questionRepository.findRandomQuestionsByCategoryAndType(
          categoryCode, questionType.name(), count);
    } catch (Exception e) {
      log.error("根据类型获取随机题目失败", e);
      throw new RuntimeException("根据类型获取随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestionsByDifficulty(
      String categoryCode, Question.Difficulty difficulty, int count) {
    try {
      log.info("根据难度获取随机题目 - 分类: {}, 难度: {}, 数量: {}", categoryCode, difficulty, count);
      return questionRepository.findRandomQuestionsByCategoryAndDifficulty(
          categoryCode, difficulty.name(), count);
    } catch (Exception e) {
      log.error("根据难度获取随机题目失败", e);
      throw new RuntimeException("根据难度获取随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestionsByKnowledgeSource(
      String categoryCode, Question.KnowledgeSource knowledgeSource, int count) {
    try {
      log.info("根据知识点来源获取随机题目 - 分类: {}, 来源: {}, 数量: {}", categoryCode, knowledgeSource, count);
      return questionRepository.findRandomQuestionsByCategoryAndKnowledgeSource(
          categoryCode, knowledgeSource.name(), count);
    } catch (Exception e) {
      log.error("根据知识点来源获取随机题目失败", e);
      throw new RuntimeException("根据知识点来源获取随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestionsAcrossCategories(int count) {
    try {
      log.info("获取跨分类随机题目 - 数量: {}", count);
      return questionRepository.findRandomQuestionsAcrossCategories(count);
    } catch (Exception e) {
      log.error("获取跨分类随机题目失败", e);
      throw new RuntimeException("获取跨分类随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestionsByTypeAcrossCategories(
      Question.QuestionType questionType, int count) {
    try {
      log.info("根据类型获取跨分类随机题目 - 类型: {}, 数量: {}", questionType, count);
      return questionRepository.findRandomQuestionsByTypeAcrossCategories(
          questionType.name(), count);
    } catch (Exception e) {
      log.error("根据类型获取跨分类随机题目失败", e);
      throw new RuntimeException("根据类型获取跨分类随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestionsByDifficultyAcrossCategories(
      Question.Difficulty difficulty, int count) {
    try {
      log.info("根据难度获取跨分类随机题目 - 难度: {}, 数量: {}", difficulty, count);
      return questionRepository.findRandomQuestionsByDifficultyAcrossCategories(
          difficulty.name(), count);
    } catch (Exception e) {
      log.error("根据难度获取跨分类随机题目失败", e);
      throw new RuntimeException("根据难度获取跨分类随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRandomQuestionsByKnowledgeSourceAcrossCategories(
      Question.KnowledgeSource knowledgeSource, int count) {
    try {
      log.info("根据知识点来源获取跨分类随机题目 - 来源: {}, 数量: {}", knowledgeSource, count);
      return questionRepository.findRandomQuestionsByKnowledgeSourceAcrossCategories(
          knowledgeSource.name(), count);
    } catch (Exception e) {
      log.error("根据知识点来源获取跨分类随机题目失败", e);
      throw new RuntimeException("根据知识点来源获取跨分类随机题目失败: " + e.getMessage());
    }
  }

  @Override
  public Page<Question> searchQuestions(String keyword, Pageable pageable) {
    try {
      log.info("搜索题目 - 关键词: {}", keyword);
      return questionRepository.searchQuestionsByKeyword(keyword, pageable);
    } catch (Exception e) {
      log.error("搜索题目失败", e);
      throw new RuntimeException("搜索题目失败: " + e.getMessage());
    }
  }

  @Override
  public Page<Question> searchQuestionsByTag(String tag, Pageable pageable) {
    try {
      log.info("根据标签搜索题目 - 标签: {}", tag);
      return questionRepository.searchQuestionsByTag(tag, pageable);
    } catch (Exception e) {
      log.error("根据标签搜索题目失败", e);
      throw new RuntimeException("根据标签搜索题目失败: " + e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getQuestionStatistics(String categoryCode) {
    try {
      log.info("获取题目统计 - 分类: {}", categoryCode);
      Map<String, Object> stats = new HashMap<>();

      // 总题目数
      long totalCount = questionRepository.countByCategoryCode(categoryCode);
      stats.put("totalCount", totalCount);

      // 各类型题目数
      List<Object[]> typeDistribution =
          questionRepository.getQuestionTypeDistributionByCategory(categoryCode);
      stats.put("typeDistribution", typeDistribution);

      // 各难度题目数
      List<Object[]> difficultyDistribution =
          questionRepository.getDifficultyDistributionByCategory(categoryCode);
      stats.put("difficultyDistribution", difficultyDistribution);

      // 各知识点来源题目数
      List<Object[]> knowledgeSourceDistribution =
          questionRepository.getKnowledgeSourceDistributionByCategory(categoryCode);
      stats.put("knowledgeSourceDistribution", knowledgeSourceDistribution);

      return stats;
    } catch (Exception e) {
      log.error("获取题目统计失败", e);
      throw new RuntimeException("获取题目统计失败: " + e.getMessage());
    }
  }

  @Override
  public List<Map<String, Object>> getQuestionTypeDistribution(String categoryCode) {
    try {
      log.info("获取题目类型分布 - 分类: {}", categoryCode);
      List<Object[]> results =
          questionRepository.getQuestionTypeDistributionByCategory(categoryCode);
      return convertToMapList(results);
    } catch (Exception e) {
      log.error("获取题目类型分布失败", e);
      throw new RuntimeException("获取题目类型分布失败: " + e.getMessage());
    }
  }

  @Override
  public List<Map<String, Object>> getDifficultyDistribution(String categoryCode) {
    try {
      log.info("获取难度分布 - 分类: {}", categoryCode);
      List<Object[]> results = questionRepository.getDifficultyDistributionByCategory(categoryCode);
      return convertToMapList(results);
    } catch (Exception e) {
      log.error("获取难度分布失败", e);
      throw new RuntimeException("获取难度分布失败: " + e.getMessage());
    }
  }

  @Override
  public List<Map<String, Object>> getKnowledgeSourceDistribution(String categoryCode) {
    try {
      log.info("获取知识点来源分布 - 分类: {}", categoryCode);
      List<Object[]> results =
          questionRepository.getKnowledgeSourceDistributionByCategory(categoryCode);
      return convertToMapList(results);
    } catch (Exception e) {
      log.error("获取知识点来源分布失败", e);
      throw new RuntimeException("获取知识点来源分布失败: " + e.getMessage());
    }
  }

  @Override
  public Question createQuestion(Question question) {
    try {
      log.info("创建题目 - 分类: {}, 类型: {}", question.getCategoryCode(), question.getQuestionType());
      question.setCreatedAt(LocalDateTime.now());
      question.setUpdatedAt(LocalDateTime.now());
      return questionRepository.save(question);
    } catch (Exception e) {
      log.error("创建题目失败", e);
      throw new RuntimeException("创建题目失败: " + e.getMessage());
    }
  }

  @Override
  public Question updateQuestion(Long questionId, Question question) {
    try {
      log.info("更新题目 - 题目ID: {}", questionId);
      Question existingQuestion = getQuestionById(questionId);

      // 更新字段
      existingQuestion.setQuestionText(question.getQuestionText());
      existingQuestion.setOptions(question.getOptions());
      existingQuestion.setCorrectAnswer(question.getCorrectAnswer());
      existingQuestion.setExplanation(question.getExplanation());
      existingQuestion.setScore(question.getScore());
      existingQuestion.setTags(question.getTags());
      existingQuestion.setUpdatedAt(LocalDateTime.now());

      return questionRepository.save(existingQuestion);
    } catch (Exception e) {
      log.error("更新题目失败", e);
      throw new RuntimeException("更新题目失败: " + e.getMessage());
    }
  }

  @Override
  public boolean deleteQuestion(Long questionId) {
    try {
      log.info("删除题目 - 题目ID: {}", questionId);
      questionRepository.deleteById(questionId);
      return true;
    } catch (Exception e) {
      log.error("删除题目失败", e);
      throw new RuntimeException("删除题目失败: " + e.getMessage());
    }
  }

  @Override
  public boolean reviewQuestion(Long questionId, Question.QuestionStatus status) {
    try {
      log.info("审核题目 - 题目ID: {}, 状态: {}", questionId, status);
      Question question = getQuestionById(questionId);
      question.setStatus(status);
      question.setUpdatedAt(LocalDateTime.now());
      questionRepository.save(question);
      return true;
    } catch (Exception e) {
      log.error("审核题目失败", e);
      throw new RuntimeException("审核题目失败: " + e.getMessage());
    }
  }

  @Override
  public int batchReviewQuestions(List<Long> questionIds, Question.QuestionStatus status) {
    try {
      log.info("批量审核题目 - 数量: {}, 状态: {}", questionIds.size(), status);
      int successCount = 0;
      for (Long questionId : questionIds) {
        try {
          if (reviewQuestion(questionId, status)) {
            successCount++;
          }
        } catch (Exception e) {
          log.warn("审核题目失败 - 题目ID: {}", questionId, e);
        }
      }
      return successCount;
    } catch (Exception e) {
      log.error("批量审核题目失败", e);
      throw new RuntimeException("批量审核题目失败: " + e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getQuestionDetails(Long questionId) {
    try {
      log.info("获取题目详情 - 题目ID: {}", questionId);
      Question question = getQuestionById(questionId);
      Map<String, Object> details = new HashMap<>();

      details.put("id", question.getId());
      details.put("categoryCode", question.getCategoryCode());
      details.put("questionType", question.getQuestionType());
      details.put("difficulty", question.getDifficulty());
      details.put("knowledgeSource", question.getKnowledgeSource());
      details.put("questionText", question.getQuestionText());
      details.put("questionImage", question.getQuestionImage());
      details.put("options", question.getOptions());
      details.put("correctAnswer", question.getCorrectAnswer());
      details.put("explanation", question.getExplanation());
      details.put("score", question.getScore());
      details.put("tags", question.getTags());
      details.put("status", question.getStatus());
      details.put("createdAt", question.getCreatedAt());
      details.put("updatedAt", question.getUpdatedAt());

      return details;
    } catch (Exception e) {
      log.error("获取题目详情失败", e);
      throw new RuntimeException("获取题目详情失败: " + e.getMessage());
    }
  }

  @Override
  public Map<String, Object> validateAnswer(Long questionId, String userAnswer) {
    try {
      log.info("验证答案 - 题目ID: {}, 答案: {}", questionId, userAnswer);
      Question question = getQuestionById(questionId);

      boolean isCorrect = checkAnswer(question, userAnswer);
      int score = isCorrect ? question.getScore() : 0;

      Map<String, Object> result = new HashMap<>();
      result.put("isCorrect", isCorrect);
      result.put("score", score);
      result.put("correctAnswer", question.getCorrectAnswer());
      result.put("explanation", question.getExplanation());

      return result;
    } catch (Exception e) {
      log.error("验证答案失败", e);
      throw new RuntimeException("验证答案失败: " + e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getQuestionExplanation(Long questionId) {
    try {
      log.info("获取题目解析 - 题目ID: {}", questionId);
      Question question = getQuestionById(questionId);

      Map<String, Object> explanation = new HashMap<>();
      explanation.put("questionText", question.getQuestionText());
      explanation.put("correctAnswer", question.getCorrectAnswer());
      explanation.put("explanation", question.getExplanation());
      explanation.put("score", question.getScore());

      return explanation;
    } catch (Exception e) {
      log.error("获取题目解析失败", e);
      throw new RuntimeException("获取题目解析失败: " + e.getMessage());
    }
  }

  @Override
  public List<Question> getRelatedQuestions(Long questionId, int count) {
    try {
      log.info("获取相关题目 - 题目ID: {}, 数量: {}", questionId, count);
      Question question = getQuestionById(questionId);

      // 获取同分类的其他题目
      List<Question> questions =
          questionRepository
              .findByCategoryCodeOrderByCreatedAtDesc(
                  question.getCategoryCode(), Pageable.unpaged())
              .getContent();

      // 过滤掉当前题目
      return questions.stream()
          .filter(q -> !q.getId().equals(questionId))
          .limit(count)
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("获取相关题目失败", e);
      throw new RuntimeException("获取相关题目失败: " + e.getMessage());
    }
  }

  @Override
  public List<String> getQuestionTags(Long questionId) {
    try {
      log.info("获取题目标签 - 题目ID: {}", questionId);
      Question question = getQuestionById(questionId);

      if (question.getTags() != null && !question.getTags().isEmpty()) {
        // 解析JSON格式的标签
        // 这里需要根据实际的JSON解析逻辑来实现
        return Arrays.asList(question.getTags().split(","));
      }

      return new ArrayList<>();
    } catch (Exception e) {
      log.error("获取题目标签失败", e);
      throw new RuntimeException("获取题目标签失败: " + e.getMessage());
    }
  }

  @Override
  public boolean updateQuestionTags(Long questionId, List<String> tags) {
    try {
      log.info("更新题目标签 - 题目ID: {}, 标签: {}", questionId, tags);
      Question question = getQuestionById(questionId);

      // 将标签列表转换为JSON字符串
      String tagsJson = String.join(",", tags);
      question.setTags(tagsJson);
      question.setUpdatedAt(LocalDateTime.now());

      questionRepository.save(question);
      return true;
    } catch (Exception e) {
      log.error("更新题目标签失败", e);
      throw new RuntimeException("更新题目标签失败: " + e.getMessage());
    }
  }

  @Override
  public Map<String, Object> getQuestionAuthor(Long questionId) {
    try {
      log.info("获取题目作者 - 题目ID: {}", questionId);
      Question question = getQuestionById(questionId);

      Map<String, Object> author = new HashMap<>();
      author.put("authorId", question.getAuthorId());
      // 这里可以添加更多作者信息，比如从用户表获取作者姓名等

      return author;
    } catch (Exception e) {
      log.error("获取题目作者失败", e);
      throw new RuntimeException("获取题目作者失败: " + e.getMessage());
    }
  }

  @Override
  public String getQuestionCreatedTime(Long questionId) {
    try {
      log.info("获取题目创建时间 - 题目ID: {}", questionId);
      Question question = getQuestionById(questionId);
      return question.getCreatedAt().toString();
    } catch (Exception e) {
      log.error("获取题目创建时间失败", e);
      throw new RuntimeException("获取题目创建时间失败: " + e.getMessage());
    }
  }

  @Override
  public String getQuestionUpdatedTime(Long questionId) {
    try {
      log.info("获取题目更新时间 - 题目ID: {}", questionId);
      Question question = getQuestionById(questionId);
      return question.getUpdatedAt().toString();
    } catch (Exception e) {
      log.error("获取题目更新时间失败", e);
      throw new RuntimeException("获取题目更新时间失败: " + e.getMessage());
    }
  }

  @Override
  public boolean existsQuestion(Long questionId) {
    try {
      return questionRepository.existsById(questionId);
    } catch (Exception e) {
      log.error("检查题目是否存在失败", e);
      throw new RuntimeException("检查题目是否存在失败: " + e.getMessage());
    }
  }

  @Override
  public boolean isQuestionApproved(Long questionId) {
    try {
      Question question = getQuestionById(questionId);
      return question.getStatus() == Question.QuestionStatus.APPROVED;
    } catch (Exception e) {
      log.error("检查题目是否已审核失败", e);
      throw new RuntimeException("检查题目是否已审核失败: " + e.getMessage());
    }
  }

  @Override
  public Question.QuestionStatus getQuestionStatus(Long questionId) {
    try {
      Question question = getQuestionById(questionId);
      return question.getStatus();
    } catch (Exception e) {
      log.error("获取题目状态失败", e);
      throw new RuntimeException("获取题目状态失败: " + e.getMessage());
    }
  }

  @Override
  public String getQuestionCategory(Long questionId) {
    try {
      Question question = getQuestionById(questionId);
      return question.getCategoryCode();
    } catch (Exception e) {
      log.error("获取题目分类失败", e);
      throw new RuntimeException("获取题目分类失败: " + e.getMessage());
    }
  }

  @Override
  public Question.QuestionType getQuestionType(Long questionId) {
    try {
      Question question = getQuestionById(questionId);
      return question.getQuestionType();
    } catch (Exception e) {
      log.error("获取题目类型失败", e);
      throw new RuntimeException("获取题目类型失败: " + e.getMessage());
    }
  }

  @Override
  public Question.Difficulty getQuestionDifficulty(Long questionId) {
    try {
      Question question = getQuestionById(questionId);
      return question.getDifficulty();
    } catch (Exception e) {
      log.error("获取题目难度失败", e);
      throw new RuntimeException("获取题目难度失败: " + e.getMessage());
    }
  }

  @Override
  public Question.KnowledgeSource getQuestionKnowledgeSource(Long questionId) {
    try {
      Question question = getQuestionById(questionId);
      return question.getKnowledgeSource();
    } catch (Exception e) {
      log.error("获取题目知识点来源失败", e);
      throw new RuntimeException("获取题目知识点来源失败: " + e.getMessage());
    }
  }

  // 私有辅助方法
  private boolean checkAnswer(Question question, String userAnswer) {
    if (userAnswer == null || userAnswer.trim().isEmpty()) {
      return false;
    }

    // 简单的答案比较，实际项目中可能需要更复杂的逻辑
    return question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
  }

  private List<Map<String, Object>> convertToMapList(List<Object[]> results) {
    List<Map<String, Object>> mapList = new ArrayList<>();
    for (Object[] result : results) {
      Map<String, Object> map = new HashMap<>();
      map.put("name", result[0]);
      map.put("count", result[1]);
      mapList.add(map);
    }
    return mapList;
  }
}
