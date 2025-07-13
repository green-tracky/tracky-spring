package com.example.tracky.user;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.ProviderTypeEnum;
import com.example.tracky._core.enums.UserTypeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.kakaojwt.RSAUtil;
import com.example.tracky.user.runlevel.RunLevel;
import com.example.tracky.user.runlevel.RunLevelRepository;
import com.example.tracky.user.utils.LoginIdUtil;
import com.example.tracky.user.utils.UserTagUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RunLevelRepository runLevelRepository;
    private final RSAUtil rsaUtil;

    @Transactional
    public UserResponse.IdTokenDTO kakaoLogin(String idToken) { // application context에 저장
        // 1. 공개키 존재 확인 없으면 다운로드
        // 2. id Token 검증 (base64 디코딩, 서명검증)
        OAuthProfile oAuthProfile = rsaUtil.verify(idToken);

        User user = null;

        // 3. 회원가입 유무 확인
        Optional<User> userOP = userRepository.findByLoginId(ProviderTypeEnum.KAKAO + "_" + oAuthProfile.getSub());

        if (userOP.isEmpty()) {
            // 4. 안되있으면 강제 회원가입

            // 러닝 레벨 조회
            RunLevel runLevelPS = runLevelRepository.findBySortOrder(0)
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_LEVEL_NOT_FOUND));

            // db 에 있는 유저들의 유저태그 목록 조회
            List<String> userTagsPS = userRepository.findAllUserTag();

            user = User.builder()
                    .loginId(LoginIdUtil.extractProvider(oAuthProfile.getIss()) + "_" + oAuthProfile.getSub())
                    .password(UUID.randomUUID().toString())
                    .username(oAuthProfile.getNickname())
                    .email(null)
                    .profileUrl(oAuthProfile.getPicture())
                    .runLevel(runLevelPS)
                    .userType(UserTypeEnum.GENERAL)
                    .provider(ProviderTypeEnum.KAKAO)
                    .userTag(UserTagUtil.generateUniqueUserTag(userTagsPS))
                    .build();

            user = userRepository.save(user);
        } else {
            user = userOP.get();
        }

        // 5. 되어있다면 아무것도 안해도 됨
        return new UserResponse.IdTokenDTO(user, idToken);
    }

    @Transactional
    public UserResponse.UpdateDTO update(Integer id, UserRequest.UpdateDTO reqDTO, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 권한 체크
        checkAccess(id, userPS);

        // 정보 수정
        userPS.updateInfo(reqDTO);

        // updatedAt 반영을 위해 flush
        userRepository.flush();

        // DTO 응답
        return new UserResponse.UpdateDTO(userPS);
    }

    @Transactional
    public void delete(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 권한 체크
        checkAccess(id, userPS);

        userRepository.delete(userPS);
    }

    /**
     * 사용자 정보에 대한 사용자의 접근 권한을 확인합니다.
     * 권한이 없을 경우 ExceptionApi403 예외를 발생시킵니다.
     *
     * @param id
     * @param userPS
     */
    private void checkAccess(Integer id, User userPS) {
        if (!id.equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

    public UserResponse.DetailDTO getUser(Integer id, OAuthProfile sessionProfile) {
        User userPS = userRepository.findByIdJoin(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        return new UserResponse.DetailDTO(userPS, LoginIdUtil.makeLoginId(sessionProfile));
    }

    @Transactional
    public void updateFCMToken(Integer id, OAuthProfile sessionProfile, UserRequest.FCMDTO reqDTO) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 권한 체크
        checkAccess(id, userPS);

        userPS.updateFCMToken(reqDTO.getFcmToken());
    }
}
