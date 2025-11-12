package com.javaweb.security.converter;

import com.javaweb.security.entity.Question.KnowledgeSource;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * KnowledgeSource枚举转换器
 *
 * <p>将数据库中的code值（如"principle"）转换为枚举（如KnowledgeSource.PRINCIPLE）， 将枚举转换为code值存储到数据库
 *
 * @author Java Web Security Team
 * @since 1.0.0
 */
@Converter(autoApply = true)
public class KnowledgeSourceConverter implements AttributeConverter<KnowledgeSource, String> {

  @Override
  public String convertToDatabaseColumn(KnowledgeSource knowledgeSource) {
    if (knowledgeSource == null) {
      return null;
    }
    return knowledgeSource.getCode();
  }

  @Override
  public KnowledgeSource convertToEntityAttribute(String code) {
    if (code == null) {
      return null;
    }
    return KnowledgeSource.fromCode(code);
  }
}
