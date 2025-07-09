package com.example.tracky.runrecord.runsegments.runcoordinates;

import com.example.tracky.runrecord.runsegments.RunSegment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Table(name = "run_coordinate_tb")
@Entity
public class RunCoordinate {
    @Id
    @Column(name = "run_segment_id")
    private Integer id;

    // TODO : 좌표엔티티 원복하고 db 에 넣을때만 문자열로 변환 AttributeConverter 사용
    @Lob
    private String coordinate; //[{"lat": 37.123, "lon": 127.456, "recordedAt": "2025-07-03 09:00:00"}]

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // PK와 FK를 공유
    @JoinColumn(name = "run_segment_id")
    private RunSegment runSegment; // 부모 러닝 구간

    @Builder
    public RunCoordinate(Integer id, String coordinate, RunSegment runSegment) {
        this.id = id;
        this.coordinate = coordinate;
        this.runSegment = runSegment;
    }

    // 기본생성자 사용금지
    protected RunCoordinate() {
    }

}
