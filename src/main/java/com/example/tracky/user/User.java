package com.example.tracky.user;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.Enum.GenderEnum;
import com.example.tracky.user.Enum.UserTypeEnum;
import com.example.tracky.user.runlevel.RunLevel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username; // 유저 이름
    private String email; // 유저 이메일
    private String profileUrl; // 프로필 이미지 주소
    private Double height; // 177.5(cm)
    private Double weight; // 75.5(kg)
    private GenderEnum gender; // (남 | 여)
    private UserTypeEnum userType; // (일반 | 관리자)
    private String provider; // oauth 제공자 (kakao, google)
    private String userTag; // #UUID 6자리
    private String flutterTokenId; // 기기 식별 아이디 // 알림서비스용

    @ManyToOne(fetch = FetchType.LAZY)
    private RunLevel runLevel; // 유저 생성할때 기본적으로 1이 들어가야함

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunRecord> runRecords = new ArrayList<>(); // 자식 러닝들

    @Builder
    public User(Integer id, String username, String email, String profileUrl, Double height, Double weight, GenderEnum gender, UserTypeEnum userType, String provider, String userTag, String flutterTokenId, RunLevel runLevel, List<RunRecord> runRecords) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.userType = userType;
        this.provider = provider;
        this.userTag = userTag;
        this.flutterTokenId = flutterTokenId;
        this.runLevel = runLevel;
        this.runRecords = runRecords;
    }

    /**
     * 사용자의 러닝 레벨을 업데이트하는 편의 메서드입니다.
     * 이 메서드를 통해 객체의 상태 변경 책임을 User 엔티티 스스로가 갖게 됩니다.
     *
     * @param newLevel 새로 도달한 레벨
     */
    public void updateRunLevel(RunLevel newLevel) {
        this.runLevel = newLevel;
    }

}
