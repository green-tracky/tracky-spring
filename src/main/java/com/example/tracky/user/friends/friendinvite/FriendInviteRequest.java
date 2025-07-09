package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.InviteStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;

public class FriendInviteRequest {

    @Data
    public static class SaveDTO {
        private Integer id;
        private Integer fromUser;
        private Integer toUser;
        private InviteStatusEnum status;
        private LocalDateTime createdAt;

        public SaveDTO(FriendInvite invite) {
            this.id = invite.getId();
            this.fromUser = invite.getFromUser().getId();
            this.toUser = invite.getToUser().getId();
            this.status = invite.getStatus();
            this.createdAt = invite.getCreatedAt();
        }
    }
}
