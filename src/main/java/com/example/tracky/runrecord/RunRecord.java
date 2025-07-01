package com.example.tracky.runrecord;

import com.example.tracky.runrecord.enums.RunPlaceTypeEnum;
import com.example.tracky.runrecord.picture.Picture;
import com.example.tracky.runrecord.runbadge.runbadgeachv.RunBadgeAchv;
import com.example.tracky.runrecord.runsegment.RunSegment;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Integer calories; // 총 칼로리 소모량
    private String memo; // 메모
    private Integer avgPace; // 평균 페이스. 초단위. km 단위
    private Integer bestPace; // 최고 페이스. 초단위. km 단위

    @ColumnDefault("0")
    @Max(value = 10, message = "러닝 강도는 10을 초과할 수 없습니다.")
    private Integer intensity; // 러닝 강도 (1~10). 기본값 0
    private RunPlaceTypeEnum place; // 장소 (도로|트랙|산길). 이넘 만들어뒀으니 사용. 기본값 null

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY) // 추가함
    private User user;

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunSegment> runSegments = new ArrayList<>(); // 자식 구간들

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunBadgeAchv> runBadgeAchvs = new ArrayList<>(); // 자식 뱃지들

    @OneToMany(mappedBy = "runRecord", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Picture> pictures = new ArrayList<>(); // 자식 사진들

    @Builder
    public RunRecord(Integer id, String title, Integer totalDistanceMeters, Integer totalDurationSeconds, Integer calories, String memo, Integer avgPace, Integer bestPace, Integer intensity, RunPlaceTypeEnum place, User user) {
        this.id = id;
        this.title = title;
        this.totalDistanceMeters = totalDistanceMeters;
        this.totalDurationSeconds = totalDurationSeconds;
        this.calories = calories;
        this.memo = memo;
        this.avgPace = avgPace;
        this.bestPace = bestPace;
        this.intensity = intensity;
        this.place = place;
        this.user = user;
    }

    /**
     * 변경 내용
     * <pre>
     * - title
     * - memo
     * - intensity
     * - place
     * </pre>
     *
     * @param reqDTO
     */
    public void update(RunRecordRequest.UpdateDTO reqDTO) {
        this.title = reqDTO.getTitle();
        this.memo = reqDTO.getMemo();
        this.intensity = reqDTO.getIntensity();
        this.place = reqDTO.getPlace();
    }

    // 사진 변경 로직 나중에 추가

}
