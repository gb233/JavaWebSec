package com.javaweb.security.converter;

import com.javaweb.security.entity.Question.QuestionStatus;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * QuestionStatus枚举转换器
 *
 * <p>将数据库中的code值（如"approved"）转换为枚举（如QuestionStatus.APPROVED）， 将枚举转换为code值存储到数据库
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Converter(autoApply = true)
public class QuestionStatusConverter implements AttributeConverter<QuestionStatus, String> {

  @Override
  public String convertToDatabaseColumn(QuestionStatus questionStatus) {
    if (questionStatus == null) {
      return null;
    }
    return questionStatus.getCode();
  }

  @Override
  public QuestionStatus convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return QuestionStatus.fromCode(code);
  }
}
