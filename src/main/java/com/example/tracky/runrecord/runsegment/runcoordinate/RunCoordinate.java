package com.example.tracky.runrecord.runsegment.runcoordinate;

import java.sql.Timestamp;

import com.example.tracky.runrecord.runsegment.RunSegment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "run_coordinate_tb")
@Entity
public class RunCoordinate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double lat; // 위도
    private Double lon; // 경도
    private Timestamp createdAt; // 프론트에서 좌표 생성시간을 받아야 한다

    @ManyToOne(fetch = FetchType.LAZY)
    private RunSegment runSegment; // 부모 러닝 구간

    @Builder
    public RunCoordinate(Integer id, Double lat, Double lon, RunSegment runSegment) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.runSegment = runSegment;
    }

}
