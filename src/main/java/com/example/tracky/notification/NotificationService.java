package com.example.tracky.notification;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.enums.NotificationTypeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.challenges.domain.Challenge;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.community.challenges.repository.ChallengeInviteRepository;
import com.example.tracky.community.challenges.repository.ChallengeRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.friends.friendinvite.FriendInvite;
import com.example.tracky.user.friends.friendinvite.FriendInviteRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final FriendInviteRepository friendInviteRepository;
    private final ChallengeInviteRepository challengeInviteRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;
    // FirebaseConfig에서 Bean으로 등록한 FirebaseMessaging 객체를 주입받습니다.
    private final FirebaseMessaging firebaseMessaging;

    /**
     * 알림 목록 조회
     *
     * @param sessionProfile
     * @return
     */
    public NotificationResponse.NotificationListDTO getNotifications(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        List<NotificationResponse.NotificationBundleDTO> notificationDTO = new ArrayList<>();

        // 친구 요청 알람 조회
        List<FriendInvite> friendInvitesPS = friendInviteRepository.findAllByToUserIdJoin(userPS.getId());
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

        log.info("{}({})이 알람 목록을 조회합니다", userPS.getUsername(), userPS.getId());
        // DTO 통합
        return new NotificationResponse.NotificationListDTO(notificationDTO);
    }

    /**
     * 친구 요청 알림을 특정 사용자에게 보냅니다.
     *
     * @param toUserId   알림을 받을 사용자의 ID
     * @param fromUserId 친구 요청을 보낸 사용자의 ID
     */
    @Async // 이 어노테이션 하나로 비동기 처리가 적용됩니다.
    public void sendFriendInviteNotification(Integer fromUserId, Integer toUserId) {
        // 1. 알림을 받을 사용자(수신자)와 요청을 보낸 사용자(요청자)의 정보를 DB에서 조회합니다.
        Optional<User> fromUserOP = userRepository.findById(fromUserId);
        Optional<User> toUserOP = userRepository.findById(toUserId);

        // 2. 사용자 정보가 없는 경우, 로그를 남기고 함수를 종료합니다.
        if (toUserOP.isEmpty() || fromUserOP.isEmpty()) {
            log.warn("알림을 보낼 사용자 또는 요청자가 존재하지 않습니다.");
            return;
        }

        User toUser = toUserOP.get();
        User fromUser = fromUserOP.get();

        String toUserFCMToken = toUser.getFcmToken();
        // 3. 수신자의 FCM 토큰이 등록되어 있는지 확인합니다. 토큰이 없으면 알림을 보낼 수 없습니다.
        if (toUserFCMToken == null || toUserFCMToken.isEmpty()) {
            log.warn("수신자의 FCM 토큰이 등록되지 않았습니다: {}", toUser.getUsername());
            return;
        }

        // 4. 알림 메시지를 구성합니다. (Notification Payload)
        // 이 부분은 사용자의 디바이스에 직접 표시될 알림의 제목과 본문에 해당합니다.
        Notification notification = Notification.builder()
                .setTitle("새로운 친구 요청")
                .setBody(fromUser.getUsername() + " 님이 친구 요청을 보냈습니다.")
                .build();

        // 5. 알림과 함께 보낼 추가 데이터를 구성합니다. (Data Payload)
        // 이 데이터는 Flutter 앱이 백그라운드나 포그라운드에서 받아서 특정 로직을 처리하는 데 사용됩니다.
        // (예: 알림 클릭 시 특정 페이지로 이동)
        Map<String, String> data = Map.of(
                "type", NotificationTypeEnum.FRIEND_INVITE.name(),
                "fromUserId", String.valueOf(fromUserId),
                "fromUsername", fromUser.getUsername(),
                "toUserId", String.valueOf(toUserId)
        );

        // 6. FCM 메시지 객체를 최종적으로 생성합니다.
        Message message = Message.builder()
                .setToken(toUserFCMToken)        // 이 토큰을 가진 기기로 메시지를 보냅니다.
                .setNotification(notification)   // 사용자에게 보여줄 알림 내용
                .putAllData(data)                // 앱에서 처리할 추가 데이터
                .build();

        // 7. 구성된 메시지를 FCM으로 전송합니다.
        try {
            String response = firebaseMessaging.send(message);
            log.debug("Successfully sent message to FCM: {}", response);
        } catch (FirebaseMessagingException e) {
            // 비동기 호출에서는 예외를 다시 던져서 핸들러가 처리하도록 해야 합니다.
            throw new RuntimeException("FCM 메시지 전송 실패", e);
        }
    }

    /**
     * 챌린지 초대 요청 알림을 특정 사용자에게 보냅니다.
     *
     * @param toUserId   알림을 받을 사용자의 ID
     * @param fromUserId 챌린지 초대 요청을 보낸 사용자의 ID
     */
    @Async // 이 어노테이션 하나로 비동기 처리가 적용됩니다.
    public void sendChallengeInviteNotification(Integer fromUserId, Integer toUserId, Integer challengeId) {
        // 1. 알림을 받을 사용자(수신자)와 요청을 보낸 사용자(요청자)의 정보를 DB에서 조회합니다.
        Optional<User> fromUserOP = userRepository.findById(fromUserId);
        Optional<User> toUserOP = userRepository.findById(toUserId);

        // 2. 알림 대상 챌린지를 DB에서 조회합니다.
        Optional<Challenge> challengeOP = challengeRepository.findById(challengeId);

        // 3. 사용자 정보가 없는 경우, 로그를 남기고 함수를 종료합니다.
        if (toUserOP.isEmpty() || fromUserOP.isEmpty()) {
            log.warn("알림을 보낼 사용자 또는 요청자가 존재하지 않습니다.");
            return;
        }

        // 4. 챌린지 정보가 없는 경우, 로그를 남기고 함수를 종료합니다.
        if (challengeOP.isEmpty()) {
            log.warn("알림을 보낼 챌린지가 존재하지 않습니다");
            return;
        }

        User toUser = toUserOP.get();
        User fromUser = fromUserOP.get();
        Challenge challenge = challengeOP.get();

        String toUserFCMToken = toUser.getFcmToken();
        // 5. 수신자의 FCM 토큰이 등록되어 있는지 확인합니다. 토큰이 없으면 알림을 보낼 수 없습니다.
        if (toUserFCMToken == null || toUserFCMToken.isEmpty()) {
            log.warn("수신자의 FCM 토큰이 등록되지 않았습니다: {}", toUser.getUsername());
            return;
        }

        // 6. 알림 메시지를 구성합니다. (Notification Payload)
        // 이 부분은 사용자의 디바이스에 직접 표시될 알림의 제목과 본문에 해당합니다.
        Notification notification = Notification.builder()
                .setTitle("새로운 챌린지 초대")
                .setBody(fromUser.getUsername() + " 님이 [" + challenge.getName() + "] 초대 요청을 보냈습니다.")
                .build();

        // 7. 알림과 함께 보낼 추가 데이터를 구성합니다. (Data Payload)
        // 이 데이터는 Flutter 앱이 백그라운드나 포그라운드에서 받아서 특정 로직을 처리하는 데 사용됩니다.
        // (예: 알림 클릭 시 특정 페이지로 이동)
        Map<String, String> data = Map.of(
                "type", NotificationTypeEnum.CHALLENGE_INVITE.name(),
                "fromUserId", String.valueOf(fromUserId),
                "fromUsername", fromUser.getUsername(),
                "toUserId", String.valueOf(toUserId)
        );

        // 6. FCM 메시지 객체를 최종적으로 생성합니다.
        Message message = Message.builder()
                .setToken(toUserFCMToken)        // 이 토큰을 가진 기기로 메시지를 보냅니다.
                .setNotification(notification)   // 사용자에게 보여줄 알림 내용
                .putAllData(data)                // 앱에서 처리할 추가 데이터
                .build();

        // 7. 구성된 메시지를 FCM으로 전송합니다.
        try {
            String response = firebaseMessaging.send(message);
            log.debug("Successfully sent message to FCM: {}", response);
        } catch (FirebaseMessagingException e) {
            // 비동기 호출에서는 예외를 다시 던져서 핸들러가 처리하도록 해야 합니다.
            throw new RuntimeException("FCM 메시지 전송 실패", e);
        }
    }

}
