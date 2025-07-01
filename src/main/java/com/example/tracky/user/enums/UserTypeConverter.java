package com.example.tracky.user.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Type Enum을 데이터베이스의 String 값("일반", "관리자")으로 변환하는 컨버터.
 *
 * @Converter(autoApply = true)로 설정하면,
 * 모든 Type 타입 필드에 자동으로 이 컨버터가 적용됩니다.
 */
@Converter(autoApply = true)
public class UserTypeConverter implements AttributeConverter<UserTypeEnum, String> {

    // Enum -> DB (String)
    // 애플리케이션의 UserTypeEnum.GENERAL을 DB의 "일반"으로 변환
    @Override
    public String convertToDatabaseColumn(UserTypeEnum userTypeEnum) {
        if (userTypeEnum == null) {
            return null;
        }
        return userTypeEnum.getValue();
    }

    // DB (String) -> Enum
    // DB의 "일반"을 애플리케이션의 UserTypeEnum.GENERAL로 변환
    @Override
    public UserTypeEnum convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return UserTypeEnum.fromString(value);
    }
}
