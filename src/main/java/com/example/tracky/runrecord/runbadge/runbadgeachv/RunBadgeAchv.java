package com.example.tracky.runrecord.runbadge.runbadgeachv;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.RunBadge;

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
@Table(name = "run_badge_achv_tb")
@Entity
public class RunBadgeAchv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    private Timestamp createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private RunRecord runRecord; // 부모 러닝 기록

    @ManyToOne(fetch = FetchType.LAZY)
    private RunBadge runBadge; // 부모 러닝 뱃지

    @Builder
    public RunBadgeAchv(Integer id, Timestamp createdAt, RunRecord runRecord, RunBadge runBadge) {
        this.id = id;
        this.createdAt = createdAt;
        this.runRecord = runRecord;
        this.runBadge = runBadge;
    }

}
