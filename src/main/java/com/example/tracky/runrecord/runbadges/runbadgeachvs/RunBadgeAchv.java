package com.example.tracky.runrecord.runbadges.runbadgeachvs;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadges.RunBadge;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Table(name = "run_badge_achv_tb")
@Entity
public class RunBadgeAchv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    private LocalDateTime achievedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RunRecord runRecord; // 부모 러닝 기록

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user; // 뱃지 획득 유저

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RunBadge runBadge; // 부모 러닝 뱃지

    @Builder
    public RunBadgeAchv(Integer id, RunRecord runRecord, User user, RunBadge runBadge) {
        this.id = id;
        this.runRecord = runRecord;
        this.user = user;
        this.runBadge = runBadge;
    }

    protected RunBadgeAchv() {
    }
}
