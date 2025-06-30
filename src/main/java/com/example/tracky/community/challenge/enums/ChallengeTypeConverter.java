package com.example.tracky.community.challenge.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChallengeTypeConverter implements AttributeConverter<ChallengeTypeEnum, String> {

    // Enum -> DB (String)
    // 애플리케이션의 ChallengeTypeEnum.PUBLIC 을 DB의 "공개"으로 변환
    @Override
    public String convertToDatabaseColumn(ChallengeTypeEnum challengeTypeEnum) {
        if (challengeTypeEnum == null) {
            return null;
        }
        return challengeTypeEnum.getValue();
    }

    // DB (String) -> Enum
    // DB의 "공개"을 애플리케이션의 ChallengeTypeEnum.PUBLIC 로 변환
    @Override
    public ChallengeTypeEnum convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return ChallengeTypeEnum.fromString(value);
    }
}