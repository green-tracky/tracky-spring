package com.example.tracky.runrecord.Enum;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * RunPlace Enum을 데이터베이스의 String 값("도로", "산길")으로 변환하는 컨버터.
 *
 * @Converter(autoApply = true)로 설정하면,
 * 모든 RunPlace 타입 필드에 자동으로 이 컨버터가 적용됩니다.
 */
@Converter(autoApply = true)
public class RunPlaceConverter implements AttributeConverter<RunPlaceEnum, String> {

    // Enum -> DB (String)
    // 애플리케이션의 RunPlace.ROAD을 DB의 "도로"으로 변환
    @Override
    public String convertToDatabaseColumn(RunPlaceEnum runPlaceEnum) {
        if (runPlaceEnum == null) {
            return null;
        }
        return runPlaceEnum.getValue();
    }

    // DB (String) -> Enum
    // DB의 "도로"을 애플리케이션의 RunPlace.ROAD로 변환
    @Override
    public RunPlaceEnum convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return RunPlaceEnum.fromString(value);
    }
}
