package com.example.tracky.community.challenges.dto;

import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
import com.example.tracky.user.User;
import com.example.tracky.user.friends.Friend;
import lombok.Data;


public class ChallengeInviteResponse {

    @Data
    public static class saveDTO {
        private Integer id;
        private Integer fromUser;
        private Integer toUser;
        private Integer challengeId;
        private InviteStatusEnum status;

        public saveDTO(ChallengeInvite challengeInvite) {
            this.id = challengeInvite.getId();
            this.fromUser = challengeInvite.getFromUser().getId();
            this.toUser = challengeInvite.getToUser().getId();
            this.challengeId = challengeInvite.getChallenge().getId();
            this.status = challengeInvite.getStatus();
        }
    }

    @Data
    public static class friendDTO {
        private Integer id;
        private String profileUrl;
        private String username;

        public friendDTO(Friend friend, User user) {
            User otherUser;
            if (friend.getFromUser().getId().equals(user.getId())) {
                otherUser = friend.getToUser();
            } else {
                otherUser = friend.getFromUser();
            }

            this.id = otherUser.getId();
            this.profileUrl = otherUser.getProfileUrl();
            this.username = otherUser.getUsername();
        }
    }

    /**
     * 친구 요청 응답 결과 DTO (수락/거절 시 응답)
     */
    @Data
    public static class ResponseDTO {
        private Integer id;
        private InviteStatusEnum status;

        public ResponseDTO(ChallengeInvite invite) {
            this.id = invite.getId();
            this.status = invite.getStatus();
        }
    }
}
