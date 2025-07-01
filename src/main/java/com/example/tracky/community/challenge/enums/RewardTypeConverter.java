package com.example.tracky.community.challenge.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * [핵심] @Converter(autoApply = true)
 * 이 설정을 통해 JPA는 코드에서 RewardTypeEnum 타입을 발견할 때마다
 * 자동으로 이 컨버터를 사용하여 DB와 값을 변환합니다.
 */
@Converter(autoApply = true)
public class RewardTypeConverter implements AttributeConverter<RewardTypeEnum, String> {

    /**
     * Enum 객체 -> DB 데이터(String)로 변환
     *
     * @param rewardTypeEnum RewardTypeEnum.GOLD 등
     * @return "금메달" 등
     */
    @Override
    public String convertToDatabaseColumn(RewardTypeEnum rewardTypeEnum) {
        if (rewardTypeEnum == null) {
            return null;
        }
        // Enum이 가지고 있는 한글 이름을 반환
        return rewardTypeEnum.getValue();
    }

    /**
     * DB 데이터(String) -> Enum 객체로 변환
     *
     * @param value "금메달" 등
     * @return RewardTypeEnum.GOLD 등
     */
    @Override
    public RewardTypeEnum convertToEntityAttribute(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // Enum에 만들어 둔 fromString 메소드를 사용하여 안전하게 변환
        return RewardTypeEnum.fromString(value);
    }
}
