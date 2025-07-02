package com.example.tracky.runrecord.runsegment.runcoordinate;

import com.example.tracky.runrecord.runsegment.RunSegment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Table(name = "run_coordinate_tb")
@Entity
public class RunCoordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double lat; // 위도
    private Double lon; // 경도
    private LocalDateTime createdAt; // 프론트에서 좌표 생성시간을 받아야 한다

    @ManyToOne(fetch = FetchType.LAZY)
    private RunSegment runSegment; // 부모 러닝 구간

    @Builder
    public RunCoordinate(Integer id, Double lat, Double lon, LocalDateTime createdAt, RunSegment runSegment) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.createdAt = createdAt;
        this.runSegment = runSegment;
    }

    // 기본생성자 사용금지
    protected RunCoordinate() {
    }

}
