package com.example.tracky.runrecord.runsegment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "run_segment_tb")
@Entity
public class RunSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer distanceMeters; // 구간 이동거리. 미터 단위
    private Integer durationSeconds; // 구간 소요시간. 초 단위
    private Timestamp startDate; // 구간 시작 시간. 프론트에서 받아야 한다
    private Timestamp endDate; // 구간 종료 시간. 프론트에서 받아야 한다
    private Double pace; // 구간 페이스. 초 단위 예) 253.85초
    private Integer calories; // 구간 소비 칼로리.

    @ManyToOne(fetch = FetchType.LAZY)
    private RunRecord runRecord; // 부모 러닝 기록

    @OneToMany(mappedBy = "runSegment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunCoordinate> runCoordinates = new ArrayList<>(); // 자식 좌표들

    @Builder
    public RunSegment(Integer id, Integer distanceMeters, Integer durationSeconds, Timestamp startDate,
            Timestamp endDate, Double pace, Integer calories, RunRecord runRecord, List<RunCoordinate> runCoordinates) {
        this.id = id;
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pace = pace;
        this.calories = calories;
        this.runRecord = runRecord;
        this.runCoordinates = runCoordinates;
    }

}
