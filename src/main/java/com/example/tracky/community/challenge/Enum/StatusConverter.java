package com.example.tracky.community.challenge.Enum;

import com.example.tracky.community.challenge.Enum.ChallengeStatusEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusConverter implements AttributeConverter<ChallengeStatusEnum, String> {

    @Override
    public String convertToDatabaseColumn(ChallengeStatusEnum attribute) {
        if (attribute == null) return null;
        return attribute.getLabel(); // "진행중", "종료"
    }

    @Override
    public ChallengeStatusEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return ChallengeStatusEnum.fromLabel(dbData);
    }
}