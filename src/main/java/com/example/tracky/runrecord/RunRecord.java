package com.example.tracky.runrecord;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.example.tracky.runrecord.picture.Picture;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runsegment.RunSegment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "run_record_tb")
@Entity
public class RunRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title; // 제목
    private Double distance; // 총 거리
    private Integer elapsedTime; // 총 시간
    private Integer calories; // 총 칼로리 소모량
    private String memo; // 메모

    @CreationTimestamp
    private Timestamp createdAt;

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunSegment> runSegments = new ArrayList(); // 자식 구간들

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunBadgeAchv> runBadgeAchvs = new ArrayList(); // 자식 뱃지들

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Picture> pictures = new ArrayList(); // 자식 뱃지들

    @Builder
    public RunRecord(Integer id, String title, Double distance, Integer elapsedTime, Integer calories, String memo) {
        this.id = id;
        this.title = title;
        this.distance = distance;
        this.elapsedTime = elapsedTime;
        this.calories = calories;
        this.memo = memo;
    }

}
