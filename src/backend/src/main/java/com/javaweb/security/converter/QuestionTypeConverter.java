package com.javaweb.security.converter;

import com.javaweb.security.entity.Question.QuestionType;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * QuestionType枚举转换器
 *
 * <p>将数据库中的code值（如"single"）转换为枚举（如QuestionType.SINGLE）， 将枚举转换为code值存储到数据库
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Converter(autoApply = true)
public class QuestionTypeConverter implements AttributeConverter<QuestionType, String> {

  @Override
  public String convertToDatabaseColumn(QuestionType questionType) {
    if (questionType == null) {
      return null;
    }
    return questionType.getCode();
  }

  @Override
  public QuestionType convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return QuestionType.fromCode(code);
  }
}


