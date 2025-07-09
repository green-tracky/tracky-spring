package com.example.tracky._core.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * [핵심] @Converter(autoApply = true)
 * 이 설정을 통해 JPA는 코드에서 RewardTypeEnum 타입을 발견할 때마다
 * 자동으로 이 컨버터를 사용하여 DB와 값을 변환합니다.
 */
@Converter(autoApply = true)
public class RewardTypeConverter implements AttributeConverter<RewardTypeEnum, String> {

    // Enum → DB 저장 (영문 코드)
    @Override
    public String convertToDatabaseColumn(RewardTypeEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    // DB → Enum (영문 코드 → Enum)
    @Override
    public RewardTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return RewardTypeEnum.fromValue(dbData);
    }
}
