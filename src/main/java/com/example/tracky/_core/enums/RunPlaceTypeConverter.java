package com.example.tracky._core.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * RunPlace Enum을 데이터베이스의 String 값("도로", "산길")으로 변환하는 컨버터.
 *
 * @Converter(autoApply = true)로 설정하면,
 * 모든 RunPlace 타입 필드에 자동으로 이 컨버터가 적용됩니다.
 */
@Converter(autoApply = true)
public class RunPlaceTypeConverter implements AttributeConverter<RunPlaceTypeEnum, String> {

    // Enum → DB 저장 (영문 코드)
    @Override
    public String convertToDatabaseColumn(RunPlaceTypeEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    // DB → Enum (영문 코드 → Enum)
    @Override
    public RunPlaceTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return RunPlaceTypeEnum.fromValue(dbData);
    }
}
