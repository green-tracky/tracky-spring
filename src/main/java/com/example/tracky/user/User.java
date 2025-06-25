package com.example.tracky.user;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.Enum.Gender;
import com.example.tracky.user.Enum.GenderConverter;
import com.example.tracky.user.Enum.UserType;
import com.example.tracky.user.Enum.UserTypeConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
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

    @Convert(converter = GenderConverter.class)
    private Gender gender; // (남 | 여)

    @Convert(converter = UserTypeConverter.class)
    private UserType userType; // (일반 | 관리자)

    private String provider; // oauth 제공자 (kakao, google)
    private String userTag; // #UUID 6자리
    private String flutterTokenId; // 기기 식별 아이디 // 알림서비스용
//    private RunLevel runLevel; 나중에 런 레벨 완성하면 추가

    @CreationTimestamp
    private Timestamp createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RunRecord> runRecords = new ArrayList<>(); // 자식 러닝들

    @Builder
    public User(Integer id, String username, String email, String profileUrl, Double height, Double weight, Gender gender, UserType userType, String provider, String userTag, String flutterTokenId) {
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
    }

}
