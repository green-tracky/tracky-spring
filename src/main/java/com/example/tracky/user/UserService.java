package com.example.tracky.user;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.ProviderTypeEnum;
import com.example.tracky._core.enums.UserTypeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.kakaojwt.RSAUtil;
import com.example.tracky.user.runlevel.RunLevel;
import com.example.tracky.user.runlevel.RunLevelRepository;
import com.example.tracky.user.utils.UserTagUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RunLevelRepository runLevelRepository;

    @Transactional
    public UserResponse.IdTokenDTO kakaoLogin(String idToken) { // application context에 저장
        // 1. 공개키 존재 확인 없으면 다운로드
        // 2. id Token 검증 (base64 디코딩, 서명검증)
        OAuthProfile oAuthProfile = RSAUtil.verify(idToken);

        User user = null;

        // 3. 회원가입 유무 확인
        Optional<User> userOP = userRepository.findByLoginId(ProviderTypeEnum.KAKAO + "_" + oAuthProfile.getSub());

        if (userOP.isEmpty()) {
            // 4. 안되있으면 강제 회원가입

            // 러닝 레벨 조회
            RunLevel runLevelPS = runLevelRepository.findBySortOrder(0)
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_LEVEL_NOT_FOUND));

            // db 에 있는 유저들의 유저태그 목록 조회
            // TODO

            user = User.builder()
                    .loginId(ProviderTypeEnum.KAKAO + "_" + oAuthProfile.getSub())
                    .password(UUID.randomUUID().toString())
                    .username(oAuthProfile.getNickname())
                    .email(null)
                    .profileUrl(oAuthProfile.getPicture())
                    .runLevel(runLevelPS)
                    .userType(UserTypeEnum.GENERAL)
                    .userTag(UserTagUtil.generateUniqueUserTag())
                    .provider(ProviderTypeEnum.KAKAO)
                    .build();
        } else {
            user = userOP.get();
        }

        // 5. 되어있다면 아무것도 안해도 됨
        return new UserResponse.IdTokenDTO(user, idToken);
    }
}
