package com.example.tracky.community.challenge.Enum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChallengeStatusConverter implements AttributeConverter<ChallengeStatus, String> {

    @Override
    public String convertToDatabaseColumn(ChallengeStatus attribute) {
        if (attribute == null) return null;
        return attribute.getLabel(); // "진행중", "종료"
    }

    @Override
    public ChallengeStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return ChallengeStatus.fromLabel(dbData);
    }
}