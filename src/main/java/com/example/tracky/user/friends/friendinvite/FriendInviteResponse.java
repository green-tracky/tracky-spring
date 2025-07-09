package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
import com.example.tracky.user.friends.friendinvite.enums.InviteStatusType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class FriendInviteResponse {
    @Data
    public static class DTO {
        private List<InvitesDTO> invitesList;

        public DTO(List<InvitesDTO> invitesList) {
            this.invitesList = invitesList;
        }
    }

    @Data
    public static class InvitesDTO {
        private Integer id;
        private String profileUrl; // 프로필 이미지 주소
        private String name;
        private InviteStatusType status;
        private LocalDateTime createdAt;

        public InvitesDTO(Integer id, User user, InviteStatusType status, LocalDateTime createdAt) {
            this.id = id;
            this.profileUrl = user.getProfileUrl();
            this.name = user.getUsername();
            this.status = status;
            this.createdAt = createdAt;
        }
    }

}
