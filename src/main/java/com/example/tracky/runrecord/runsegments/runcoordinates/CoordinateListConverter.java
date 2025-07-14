package com.example.tracky.runrecord.runsegments.runcoordinates;

import com.example.tracky._core.utils.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.List;

/**
 * List<RunCoordinate> 타입을 DB의 JSON 문자열로, 또는 그 반대로 변환하는 AttributeConverter.
 *
 * @Converter 어노테이션을 통해 이 클래스가 JPA 속성 변환기임을 선언합니다.
 * autoApply = true 속성은 모든 List<RunCoordinate> 타입에 이 컨버터를 자동으로 적용하지만,
 * 여기서는 명시적으로 @Convert 어노테이션을 사용하여 적용할 필드를 지정하는 방식을 선택합니다.
 */
@Converter
public class CoordinateListConverter implements AttributeConverter<List<RunCoordinate>, String> {

    /**
     * 엔티티의 List<RunCoordinate> 속성을 데이터베이스에 저장하기 위해 JSON 문자열로 변환합니다.
     * 이 메서드는 엔티티가 저장(persist)되거나 수정(merge)될 때 호출됩니다.
     *
     * @param attribute 엔티티에 있는 List<RunCoordinate> 필드 값
     * @return 데이터베이스의 컬럼에 저장될 JSON 형식의 문자열
     */
    @Override
    public String convertToDatabaseColumn(List<RunCoordinate> attribute) {
        // 필드 값이 null이거나 비어있으면, DB에는 빈 JSON 배열 "[]"을 저장합니다.
        // DB에 null을 저장하지 않음으로써, 애플리케이션 레벨에서 null 체크 로직을 줄일 수 있습니다.
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        // 이전에 만들어 둔 JsonUtil을 사용하여 객체 리스트를 JSON 문자열로 직렬화합니다.
        return JsonUtil.toJson(attribute);
    }

    /**
     * 데이터베이스의 JSON 문자열을 읽어와 엔티티의 List<RunCoordinate> 속성으로 변환합니다.
     * 이 메서드는 데이터베이스에서 엔티티를 조회(find, query)할 때 호출됩니다.
     *
     * @param dbData 데이터베이스 컬럼에서 읽어온 JSON 형식의 문자열
     * @return 엔티티의 List<RunCoordinate> 필드에 채워질 객체 리스트
     */
    @Override
    public List<RunCoordinate> convertToEntityAttribute(String dbData) {
        // DB에 저장된 문자열이 null이거나 비어있으면, 빈 리스트(Collections.emptyList())를 반환합니다.
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        // JsonUtil과 TypeReference를 사용하여 JSON 문자열을 정확한 제네릭 타입(List<RunCoordinate>)으로 역직렬화합니다.
        // TypeReference는 Jackson이 List 안의 요소가 'RunCoordinate' 타입임을 알 수 있게 해주는 중요한 역할을 합니다.
        return JsonUtil.fromJson(dbData, new TypeReference<>() {
        });
    }
}
