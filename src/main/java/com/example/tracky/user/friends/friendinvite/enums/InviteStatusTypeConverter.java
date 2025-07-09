package com.example.tracky.user.friends.friendinvite.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InviteStatusTypeConverter implements AttributeConverter<InviteStatusType, String> {

    @Override
    public String convertToDatabaseColumn(InviteStatusType attribute) {
        return attribute == null ? null : attribute.name(); // DB에는 문자열로 저장 (WAITING, ACCEPTED, ...)
    }

    @Override
    public InviteStatusType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return InviteStatusType.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("DB에 저장된 친구 요청 상태 값이 올바르지 않습니다: " + dbData);
        }
    }
}
