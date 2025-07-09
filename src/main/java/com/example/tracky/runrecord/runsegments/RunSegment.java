package com.example.tracky.runrecord.runsegments;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegments.runcoordinates.RunCoordinate;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Table(name = "run_segment_tb")
@Entity
public class RunSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer distanceMeters; // 구간 이동거리. 미터 단위
    private Integer durationSeconds; // 구간 소요시간. 초 단위
    private LocalDateTime startDate; // 구간 시작 시간. 프론트에서 받아야 한다
    private LocalDateTime endDate; // 구간 종료 시간. 프론트에서 받아야 한다
    private Integer pace; // 페이스. 초 단위. km 단위

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RunRecord runRecord; // 부모 러닝 기록

    @OneToOne(mappedBy = "runSegment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    private RunCoordinate runCoordinate; // 구간별 1:1 좌표 정보

    @Builder
    public RunSegment(Integer id, Integer distanceMeters, Integer durationSeconds, LocalDateTime startDate, LocalDateTime endDate, Integer pace, RunRecord runRecord) {
        this.id = id;
        this.distanceMeters = distanceMeters;
        this.durationSeconds = durationSeconds;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pace = pace;
        this.runRecord = runRecord;
    }

    // 기본생성자 사용금지
    protected RunSegment() {
    }

    /**
     * OneToOne 에서 연관관계 설정하는 방법
     *
     * @param runCoordinate
     */
    public void setRunCoordinate(RunCoordinate runCoordinate) {
        this.runCoordinate = runCoordinate;
    }
}
