package com.example.tracky.user;

import com.example.tracky._core.enums.GenderEnum;
import com.example.tracky._core.enums.ProviderTypeEnum;
import com.example.tracky._core.enums.UserTypeEnum;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.runlevel.RunLevel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Table(name = "user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String loginId; // 로그인용 및 사용자 조회용 아이디
    @Column(nullable = false)
    private String password; // 나중을 위한 password
    @Column(nullable = false)
    private String username; // 유저 이름
    private String email; // 유저 이메일
    private String profileUrl; // 프로필 이미지 주소
    private Double height; // 177.5(cm)
    private Double weight; // 75.5(kg)
    private GenderEnum gender; // (남 | 여)
    private String location; // 활동지
    private String letter; // 자기소개
    @Column(nullable = false)
    private UserTypeEnum userType; // (일반 | 관리자)

    @Enumerated(EnumType.STRING) // 이넘 영어 그대로 사용함
    @Column(nullable = false)
    private ProviderTypeEnum provider; // oauth 제공자 (kakao, google)
    @Column(unique = true, nullable = false)
    private String userTag; // #UUID 6자리
    //    @Column(unique = true, nullable = false)
    private String fcmToken; // 기기 식별 아이디 // 알림서비스용

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private RunLevel runLevel; // 유저 생성할때 기본적으로 1이 들어가야함

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RunRecord> runRecords = new ArrayList<>(); // 자식 러닝들

    @Builder
    public User(Integer id, String loginId, String password, String username, String email, String profileUrl, Double height, Double weight, GenderEnum gender, String location, String letter, UserTypeEnum userType, ProviderTypeEnum provider, String userTag, String fcmToken, RunLevel runLevel, List<RunRecord> runRecords) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.email = email;
        this.profileUrl = profileUrl;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.location = location;
        this.letter = letter;
        this.userType = userType;
        this.provider = provider;
        this.userTag = userTag;
        this.fcmToken = fcmToken;
        this.runLevel = runLevel;
        this.runRecords = runRecords;
    }

    // 기본 생성자 사용 금지
    protected User() {
    }

    /**
     * 사용자의 러닝 레벨을 업데이트하는 편의 메서드
     *
     * @param newRunLevel 새로 도달한 레벨
     */
    public void updateRunLevel(RunLevel newRunLevel) {
        this.runLevel = newRunLevel;
    }

    /**
     * 사용자 정보 수정 메서드
     *
     * @param reqDTO
     */
    public void updateInfo(UserRequest.UpdateDTO reqDTO) {
        this.username = Objects.requireNonNullElse(reqDTO.getUsername(), this.username);
        this.email = Objects.requireNonNullElse(reqDTO.getEmail(), this.email);
        this.profileUrl = Objects.requireNonNullElse(reqDTO.getProfileUrl(), this.profileUrl);
        this.height = Objects.requireNonNullElse(reqDTO.getHeight(), this.height);
        this.weight = Objects.requireNonNullElse(reqDTO.getWeight(), this.weight);
        this.gender = Objects.requireNonNullElse(reqDTO.getGender(), this.gender);
        this.location = Objects.requireNonNullElse(reqDTO.getLocation(), this.location);
        this.letter = Objects.requireNonNullElse(reqDTO.getLetter(), this.letter);
    }

    /**
     * 사용자 fcmToken 수정 메서드
     *
     * @param fcmToken
     */
    public void updateFCMToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

}
