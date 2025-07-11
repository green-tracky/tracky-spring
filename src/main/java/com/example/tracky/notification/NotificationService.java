package com.example.tracky.notification;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.community.challenges.repository.ChallengeInviteRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.friends.friendinvite.FriendInvite;
import com.example.tracky.user.friends.friendinvite.FriendInviteRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationService {
    private final FriendInviteRepository friendInviteRepository;
    private final ChallengeInviteRepository challengeInviteRepository;
    private final UserRepository userRepository;

    public NotificationResponse.NotificationListDTO getNotifications(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));


        List<NotificationResponse.NotificationBundleDTO> notificationDTO = new ArrayList<>();

        // 친구 요청 알람 조회
        List<FriendInvite> friendInvitesPS = friendInviteRepository.findAllByUserId(userPS.getId());
        for (FriendInvite friendDTO : friendInvitesPS) {
            notificationDTO.add(new NotificationResponse.NotificationBundleDTO(friendDTO));
        }

        // 챌린지 요청 알람 조회
        List<ChallengeInvite> challengeInvitesPS = challengeInviteRepository.findAllByToUserIdJoin(userPS.getId());
        for (ChallengeInvite challengeDTO : challengeInvitesPS) {
            notificationDTO.add(new NotificationResponse.NotificationBundleDTO(challengeDTO));
        }

        // 내림차 순 정렬
        notificationDTO.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));


        // DTO 통합
        return new NotificationResponse.NotificationListDTO(notificationDTO);
    }
}
