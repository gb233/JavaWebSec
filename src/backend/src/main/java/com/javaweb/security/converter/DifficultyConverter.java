package com.javaweb.security.converter;

import com.javaweb.security.entity.Question.Difficulty;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Difficulty枚举转换器
 *
 * <p>将数据库中的code值（如"easy"）转换为枚举（如Difficulty.EASY）， 将枚举转换为code值存储到数据库
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Converter(autoApply = true)
public class DifficultyConverter implements AttributeConverter<Difficulty, String> {

  @Override
  public String convertToDatabaseColumn(Difficulty difficulty) {
    if (difficulty == null) {
      return null;
    }
    return difficulty.getCode();
  }

  @Override
  public Difficulty convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return Difficulty.fromCode(code);
  }
}


