package com.example.tracky.user.friends.friendinvite;

import com.example.tracky._core.enums.InviteStatusEnum;
import lombok.Data;

public class FriendInviteRequest {

    @Data
    public static class SaveDTO {
        private Integer id;
        private Integer fromUser;
        private Integer toUser;
        private InviteStatusEnum status;
    }
}
