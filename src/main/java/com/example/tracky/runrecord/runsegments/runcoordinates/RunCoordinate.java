package com.example.tracky.runrecord.runsegments.runcoordinates;

import com.example.tracky.runrecord.runsegments.RunSegment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Table(name = "run_coordinate_tb")
@Entity
public class RunCoordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Lob
    // 1. @Convert 어노테이션을 사용하여 CoordinateListConverter를 이 필드에 적용합니다.
    //    이제 JPA는 이 필드를 DB에 저장하거나 읽어올 때 항상 CoordinateListConverter를 사용합니다.
    @Convert(converter = CoordinateListConverter.class)
    // 2. 필드 타입을 String에서 List<Coordinate>로 변경합니다.
    //    애플리케이션 코드에서는 이 필드를 순수한 자바 리스트처럼 다룰 수 있습니다.
    @Column(name = "coordinates")
    private List<Coordinate> coordinates = new ArrayList<>();

    // 3. 기존의 lat, lon, recordedAt 필드는 삭제합니다.
    //    이 정보들은 이제 coordinates 리스트 안의 Coordinate 객체가 관리합니다.

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // PK를 부모 엔티티인 RunSegment와 공유합니다.
    @JoinColumn(name = "run_segment_id")
    private RunSegment runSegment;

    // 4. 빌더의 파라미터를 수정된 필드에 맞게 변경합니다.
    @Builder
    public RunCoordinate(List<Coordinate> coordinates, RunSegment runSegment) {
        this.coordinates = coordinates;
        this.runSegment = runSegment;
    }

    // 기본 생성자의 직접적인 사용을 막기 위해 protected로 선언합니다.
    protected RunCoordinate() {
    }
}
