package com.example.tracky.user.friends;

import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;

    public List<FriendResponse.SearchDTO> getFriendSearch(String userTag, User user) {
        // 1. # 붙이는 파싱
        String tag = "#" + userTag;

        List<Friend> friends = friendRepository.findByUserTag(tag);
        List<FriendResponse.SearchDTO> searchDTOs = new ArrayList<>();
        for (User friendList : friends) {

        }


        return null;
    }
}
