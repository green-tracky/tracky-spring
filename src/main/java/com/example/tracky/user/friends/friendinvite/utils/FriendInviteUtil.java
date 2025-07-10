package com.example.tracky.user.friends.friendinvite.utils;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.user.User;
import com.example.tracky.user.friends.friendinvite.FriendInvite;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FriendInviteUtil {
    /**
     * 친구 요청 권한 체크 유틸
     *
     * @param invite 요청 엔티티
     * @param user   현재 로그인한 유저
     */
    public static void FriendInviteResult(FriendInvite invite, User user) {
        // 권한 체크
        if (!invite.getToUser().getId().equals(user.getId())) {
            throw new ExceptionApi404(ErrorCodeEnum.ACCESS_DENIED);
        }
    }
}
