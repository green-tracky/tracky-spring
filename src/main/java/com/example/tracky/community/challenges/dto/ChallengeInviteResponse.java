package com.example.tracky.community.challenges.dto;

import com.example.tracky._core.enums.InviteStatusEnum;
import com.example.tracky.community.challenges.domain.ChallengeInvite;
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


}
