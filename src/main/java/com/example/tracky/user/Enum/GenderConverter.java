package com.example.tracky.user.Enum;

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

    // Enum -> DB (String)
    // 애플리케이션의 GenderEnum.MALE을 DB의 "남"으로 변환
    @Override
    public String convertToDatabaseColumn(GenderEnum genderEnum) {
        if (genderEnum == null) {
            return null;
        }
        return genderEnum.getValue();
    }

    // DB (String) -> Enum
    // DB의 "남"을 애플리케이션의 GenderEnum.MALE로 변환
    @Override
    public GenderEnum convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return GenderEnum.fromString(value);
    }
}
