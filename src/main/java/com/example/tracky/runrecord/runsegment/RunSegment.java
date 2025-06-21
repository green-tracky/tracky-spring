package com.example.tracky.runrecord.runsegment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

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
    private Double pace; // 구간 평균 페이스

    @CreationTimestamp
    private Timestamp startDate; // 구간 시작 시간

    @CreationTimestamp
    private Timestamp endDate; // 구간 종료 시간

    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private RunRecord runRecord; // 부모 러닝 기록

    @OneToMany(mappedBy = "runSegment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunCoordinate> runCoordinates = new ArrayList<>(); // 자식 좌표들

    @Builder
    public RunSegment(Integer id, Double pace, Timestamp startDate, Timestamp endDate, RunRecord runRecord) {
        this.id = id;
        this.pace = pace;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runRecord = runRecord;
    }

}
