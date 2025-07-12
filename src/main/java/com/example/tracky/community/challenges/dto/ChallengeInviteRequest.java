package com.example.tracky.community.challenges.dto;

import lombok.Data;

import java.util.List;


public class ChallengeInviteRequest {

    @Data
    public static class InviteRequestDTO {
        private List<Integer> friendIds;
    }
}
