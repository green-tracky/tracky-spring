package com.example.tracky.runrecord.pictures;

import com.example.tracky.runrecord.RunRecord;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

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
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private RunRecord runRecord; // 부모 러닝 기록

    @Builder
    public Picture(Integer id, String fileUrl, Double lat, Double lon, LocalDateTime createdAt,
                   RunRecord runRecord) {
        this.id = id;
        this.fileUrl = fileUrl;
        this.lat = lat;
        this.lon = lon;
        this.createdAt = createdAt;
        this.runRecord = runRecord;
    }

    protected Picture() {
    }
}
