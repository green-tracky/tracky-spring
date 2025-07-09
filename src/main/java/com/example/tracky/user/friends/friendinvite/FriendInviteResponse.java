package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.friends.friendinvite.enums.InviteStatusType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class FriendInviteResponse {
    /**
     * 친구 요청 목록 응답 DTO
     */
    @Data
    public static class DTO {
        private List<InvitesDTO> invitesList;

        public DTO(List<InvitesDTO> invitesList) {
            this.invitesList = invitesList;
        }
    }

    /**
     * 친구 요청 단건 정보 DTO
     */
    @Data
    public static class InvitesDTO {
        private Integer id;
        private String profileUrl; // 프로필 이미지 주소
        private String name;
        private InviteStatusType status;
        private LocalDateTime createdAt;

        public InvitesDTO(FriendInvite friendInvite) {
            this.id = friendInvite.getId();
            this.profileUrl = friendInvite.getToUser().getProfileUrl();
            this.name = friendInvite.getToUser().getUsername();
            this.status = friendInvite.getStatus();
            this.createdAt = friendInvite.getCreatedAt();
        }
    }

    /**
     * 친구 요청 응답 결과 DTO (수락/거절 시 응답)
     */
    @Data
    public static class ResponseDTO {
        private Integer id;
        private InviteStatusType status;
        private LocalDateTime responsedAt;

        public ResponseDTO(FriendInvite invite) {
            this.id = invite.getId();
            this.status = invite.getStatus();
            this.responsedAt = invite.getResponsedAt();
        }
    }
}
