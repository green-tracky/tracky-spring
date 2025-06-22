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
    private Integer totalDistanceMeters; // 총 거리. 미터 단위
    private Integer totalDurationSeconds; // 총 시간. 초 단위
    private Integer totalcalories; // 총 칼로리 소모량
    private Double avgPace; // 평균 페이스
    private Double bestPace; // 최고 페이스
    private String memo; // 메모
    private Integer intensity; // 러닝 강도 (1~10)
    private String place; // 장소 (도로|트랙|산길). 이넘 만들어뒀으니 사용

    @CreationTimestamp
    private Timestamp createdAt;

    // 나중에 user 를 ManyToOne 하도록 추가해야함

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunSegment> runSegments = new ArrayList<>(); // 자식 구간들

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunBadgeAchv> runBadgeAchvs = new ArrayList<>(); // 자식 뱃지들

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Picture> pictures = new ArrayList<>(); // 자식 뱃지들

    @Builder
    public RunRecord(Integer id, String title, Integer totalDistanceMeters, Integer totalDurationSeconds,
            Integer totalcalories, Double avgPace, Double bestPace, String memo, Integer intensity, String place) {
        this.id = id;
        this.title = title;
        this.totalDistanceMeters = totalDistanceMeters;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalcalories = totalcalories;
        this.avgPace = avgPace;
        this.bestPace = bestPace;
        this.memo = memo;
        this.intensity = intensity;
        this.place = place;
    }

}
