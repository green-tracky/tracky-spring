package com.example.tracky.notification;

import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.user.friends.friendinvite.FriendInvite;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {
    @Data
    public static class NotificationListDTO {
        private List<NotificationbandleDTO> notifications;

        public NotificationListDTO(List<NotificationbandleDTO> notifications) {
            this.notifications = notifications;
        }
    }

    @Data
    public static class NotificationbandleDTO {
        private String type;
        private String profileUrl;
        private String username;
        private String status;
        private LocalDateTime createdAt;

        public NotificationbandleDTO(FriendInvite friendInvite) {
            this.type = "FriendInvite";
            this.profileUrl = friendInvite.getFromUser().getProfileUrl();
            this.username = friendInvite.getFromUser().getUsername();
            this.status = friendInvite.getStatus().toString();
            this.createdAt = friendInvite.getCreatedAt();
        }

        public NotificationbandleDTO(ChallengeInvite challengeInvite) {
            this.type = "challengeInvite";
            this.profileUrl = challengeInvite.getFromUser().getProfileUrl();
            this.username = challengeInvite.getFromUser().getUsername();
            this.status = challengeInvite.getStatus().toString();
            this.createdAt = challengeInvite.getCreatedAt();
        }

    }
}
