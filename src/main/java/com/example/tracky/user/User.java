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
    private String profileUrl; // 프로필 이미지 주소
    private Double height; // 177.5(cm)
    private Double weight; // 75.5(kg)
    @Enumerated(EnumType.STRING)
    private GenderEnum gender; // (남 | 여)
    private String location; // 활동지
    private String letter; // 자기소개
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserTypeEnum userType; // (일반 | 관리자)

    @Enumerated(EnumType.STRING) // 이넘 영어 그대로 사용함
    @Column(nullable = false)
    private ProviderTypeEnum provider; // oauth 제공자 (kakao, google)
    @Column(unique = true, nullable = false)
    private String userTag; // #UUID 6자리
    @Column(unique = true, nullable = true)
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
    public User(Integer id, String loginId, String password, String username, String profileUrl, Double height, Double weight, GenderEnum gender, String location, String letter, UserTypeEnum userType, ProviderTypeEnum provider, String userTag, String fcmToken, RunLevel runLevel, List<RunRecord> runRecords) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
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
        this.username = reqDTO.getUsername() == null ? this.username : reqDTO.getUsername();
        this.profileUrl = reqDTO.getProfileUrl() == null ? this.profileUrl : reqDTO.getProfileUrl();
        this.height = reqDTO.getHeight() == null ? this.height : reqDTO.getHeight();
        this.weight = reqDTO.getWeight() == null ? this.weight : reqDTO.getWeight();
        this.gender = reqDTO.getGender() == null ? this.gender : reqDTO.getGender();
        this.location = reqDTO.getLocation() == null ? this.location : reqDTO.getLocation();
        this.letter = reqDTO.getLetter() == null ? this.letter : reqDTO.getLetter();
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
