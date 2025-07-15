package com.example.tracky.runrecord.runsegments.runcoordinates;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DB의 JSON 문자열 내부에 저장될 개별 좌표 데이터를 표현하는 '값 객체(Value Object)'.
 * 이 클래스는 JPA 엔티티가 아니므로 @Entity 어노테이션이 없습니다.
 * 테이블과 직접 매핑되지 않고, 다른 엔티티의 속성(Attribute)으로 사용됩니다.
 */
@Getter
@Setter // JSON <-> 객체 변환 시 Jackson 라이브러리가 사용합니다.
@NoArgsConstructor // Jackson 라이브러리가 사용합니다.
@AllArgsConstructor
public class Coordinate {
    private Double lat; // 위도
    private Double lon; // 경도
    private LocalDateTime recordedAt; // 기록 시간
}
