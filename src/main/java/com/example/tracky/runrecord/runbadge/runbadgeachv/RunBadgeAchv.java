package com.example.tracky.runrecord.runbadge.runbadgeachv;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.runbadge.RunBadge;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Table(name = "run_badge_achv_tb")
@Entity
public class RunBadgeAchv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    private Timestamp achievedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private RunRecord runRecord; // 부모 러닝 기록

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 부모 러닝 기록

    @ManyToOne(fetch = FetchType.LAZY)
    private RunBadge runBadge; // 부모 러닝 뱃지

    @Builder
    public RunBadgeAchv(Integer id, RunRecord runRecord, User user, RunBadge runBadge) {
        this.id = id;
        this.runRecord = runRecord;
        this.user = user;
        this.runBadge = runBadge;
    }
}
