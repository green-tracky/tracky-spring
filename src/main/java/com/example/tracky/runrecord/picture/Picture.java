package com.example.tracky.runrecord.picture;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runsegment.runcoordinate.RunCoordinate;

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
@Table(name = "picture_tb")
@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileUrl; // 이미지 실제 주소
    private Integer duration; // 러닝 시작후 사진이 저장된 시점까지의 시간
    private Double lat; // 위도
    private Double lon; // 경도

    @CreationTimestamp
    private Timestamp createdAt;

    // @ManyToOne(fetch = FetchType.LAZY)
    // private RunCoordinate runCoordinate; // 이 좌표는 테이블에 있는 좌표를 사용할지. 사진에 있는 좌표를
    // 사용할지 애매함 일단 뺌

    @ManyToOne(fetch = FetchType.LAZY)
    private RunRecord runRecord; // 부모 러닝 기록

    @Builder
    public Picture(Integer id, String fileUrl, Integer duration, RunCoordinate runCoordinate, RunRecord runRecord) {
        this.id = id;
        this.fileUrl = fileUrl;
        this.duration = duration;
        this.runRecord = runRecord;
        // this.runCoordinate = runCoordinate;
    }

}
