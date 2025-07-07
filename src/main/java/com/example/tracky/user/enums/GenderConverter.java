package com.example.tracky.user.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Gender Enum을 데이터베이스의 String 값("남", "여")으로 변환하는 컨버터.
 *
 * @Converter(autoApply = true)로 설정하면,
 * 모든 Gender 타입 필드에 자동으로 이 컨버터가 적용됩니다.
 */
@Converter(autoApply = true)
public class GenderConverter implements AttributeConverter<GenderEnum, String> {

    // Enum → DB 저장 (영문 코드)
    @Override
    public String convertToDatabaseColumn(GenderEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    // DB → Enum (영문 코드 → Enum)
    @Override
    public GenderEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return GenderEnum.fromValue(dbData);
    }
}
