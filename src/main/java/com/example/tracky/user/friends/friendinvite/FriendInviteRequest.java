package com.example.tracky.user.friends.friendinvite;

import com.example.tracky.user.User;
import com.example.tracky.user.friends.friendinvite.enums.InviteStatusType;
import lombok.Data;

import java.time.LocalDateTime;

public class FriendInviteRequest {

    @Data
    public static class SaveDTO {
        private Integer id;
        private User fromUser;
        private User toUser;
        private InviteStatusType status;
        private LocalDateTime createdAt;

        public SaveDTO(FriendInvite invite) {
            this.id = invite.getId();
            this.fromUser = invite.getFromUser();
            this.toUser = invite.getToUser();
            this.status = invite.getStatus();
            this.createdAt = invite.getCreatedAt();
        }
    }
}
