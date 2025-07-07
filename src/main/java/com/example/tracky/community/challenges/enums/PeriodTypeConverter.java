package com.example.tracky.community.challenges.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * [핵심] @Converter(autoApply = true)
 * 이 설정을 통해 JPA는 코드에서 PeriodTypeEnum 타입을 발견할 때마다
 * 자동으로 이 컨버터를 사용하여 DB와 값을 변환합니다.
 */
@Converter(autoApply = true)
public class PeriodTypeConverter implements AttributeConverter<PeriodTypeEnum, String> {

    // Enum → DB 저장 (영문 코드)
    @Override
    public String convertToDatabaseColumn(PeriodTypeEnum attribute) {
        return attribute == null ? null : attribute.name();
    }

    // DB → Enum (영문 코드 → Enum)
    @Override
    public PeriodTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        return PeriodTypeEnum.fromValue(dbData);
    }
}
