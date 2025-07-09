package com.example.tracky.runrecord.pictures;

import com.example.tracky.runrecord.RunRecord;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Table(name = "picture_tb")
@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileUrl; // 이미지 실제 주소
    private Double lat; // 위도
    private Double lon; // 경도
    private LocalDateTime savedAt; // 휴대폰에 저장된 날짜

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RunRecord runRecord; // 부모 러닝 기록

    @CreationTimestamp
    private LocalDateTime createdAt; // db 저장 시간

    @Builder
    public Picture(Integer id, String fileUrl, Double lat, Double lon, LocalDateTime savedAt, RunRecord runRecord) {
        this.id = id;
        this.fileUrl = fileUrl;
        this.lat = lat;
        this.lon = lon;
        this.savedAt = savedAt;
        this.runRecord = runRecord;
    }

    protected Picture() {
    }
}
