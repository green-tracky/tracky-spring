package com.example.tracky.user;

import com.example.tracky._core.enums.ProviderTypeEnum;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.kakaojwt.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse.IdTokenDTO 카카오로그인(String idToken) { // application context에 저장
        // 1. 공개키 존재 확인 없으면 다운로드
        // 2. id Token 검증 (base64 디코딩, 서명검증)
        OAuthProfile oAuthProfile = RSAUtil.verify(idToken);

        User user = null;

        // 3. 회원가입 유무 확인
        Optional<User> userOP = userRepository.findByUsername(ProviderTypeEnum.KAKAO + "_" + oAuthProfile.getSub());

        if (userOP.isEmpty()) {
            System.out.println("1");
            // 4. 안되있으면 강제 회원가입
            user = User.builder()
                    .username(ProviderTypeEnum.KAKAO + "_" + oAuthProfile.getSub())
                    .password(UUID.randomUUID().toString())
                    .email(null)
                    .provider(ProviderTypeEnum.KAKAO)
                    .build();
            userRepository.save(user);
        } else {
            user = userOP.get();
        }

        // 5. 되어있다면 아무것도 안해도 됨
        return new UserResponse.IdTokenDTO(user, idToken);
    }
}
