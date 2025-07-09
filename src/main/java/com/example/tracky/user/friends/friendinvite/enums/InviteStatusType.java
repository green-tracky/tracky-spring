package com.example.tracky.user.friends.friendinvite.enums;

public enum InviteStatusType {
    WAITING("대기 중"),
    ACCEPTED("수락됨"),
    REJECTED("거절됨");

    private final String displayName;

    InviteStatusType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
