package com.example.tracky.notification;

import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky._core.enums.NotificationTypeEnum;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.user.friends.friendinvite.FriendInvite;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {
    @Data
    public static class NotificationListDTO {
        private List<NotificationBundleDTO> notifications;

        public NotificationListDTO(List<NotificationBundleDTO> notifications) {
            this.notifications = notifications;
        }
    }

    @Data
    public static class NotificationBundleDTO {
        private Integer InviteId;
        private NotificationTypeEnum type;
        private String profileUrl;
        private String username;
        private InviteStatusEnum status;
        private LocalDateTime createdAt;

        public NotificationBundleDTO(FriendInvite friendInvite) {
            this.InviteId = friendInvite.getId();
            this.type = NotificationTypeEnum.FRIEND_INVITE;
            this.profileUrl = friendInvite.getFromUser().getProfileUrl();
            this.username = friendInvite.getFromUser().getUsername();
            this.status = friendInvite.getStatus();
            this.createdAt = friendInvite.getCreatedAt();
        }

        public NotificationBundleDTO(ChallengeInvite challengeInvite) {
            this.InviteId = challengeInvite.getId();
            this.type = NotificationTypeEnum.CHALLENGE_INVITE;
            this.profileUrl = challengeInvite.getFromUser().getProfileUrl();
            this.username = challengeInvite.getFromUser().getUsername();
            this.status = challengeInvite.getStatus();
            this.createdAt = challengeInvite.getCreatedAt();
        }
    }
}
