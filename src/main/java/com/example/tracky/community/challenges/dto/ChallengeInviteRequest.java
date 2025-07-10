package com.example.tracky.community.challenges.dto;

import com.example.tracky._core.enums.InviteStatusEnum;


public class ChallengeInviteRequest {

    public static class saveDTO {
        private Integer id;
        private Integer fromUser;
        private Integer toUser;
        private InviteStatusEnum status;
    }
}
