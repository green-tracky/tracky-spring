package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import lombok.Data;

public class FriendResponse {
    @Data
    public static class SearchDTO {
        private Integer id;
        private String profileUrl;
        private String username;

        public SearchDTO(User user) {
            this.id = user.getId();
            this.profileUrl = user.getProfileUrl();
            this.username = user.getUsername();
        }
    }
}
