package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;

    public List<FriendResponse.SearchDTO> getFriendSearch(String userTag, User user) {
        // 1. # 붙이는 파싱
        String tag = "#" + userTag;

        List<User> friends = userRepository.findByUserTag(tag);
        List<FriendResponse.SearchDTO> searchDTO = new ArrayList<>();
        for (User UserList : friends) {
            searchDTO.add(new FriendResponse.SearchDTO(UserList));
        }

        return searchDTO;
    }
}
