package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import lombok.Data;

import java.util.List;

public class FriendResponse {
    @Data
    public static class SearchDTO {
        private User user;

        public SearchDTO(User user) {
            this.user = user;
        }
    }

    @Data
    public static class FriendListDTO {
        private List<UserDTO> FriendList;

        public FriendListDTO(List<UserDTO> friendList) {
            FriendList = friendList;
        }
    }

    @Data
    public static class UserDTO {
        private Integer id;
        private String profileUrl;
        private String username;

        public UserDTO(User user) {
            this.id = user.getId();
            this.profileUrl = user.getProfileUrl();
            this.username = user.getUsername();
        }
    }
}
