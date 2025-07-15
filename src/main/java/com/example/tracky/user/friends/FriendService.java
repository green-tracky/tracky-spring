package com.example.tracky.user.friends;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public List<FriendResponse.SearchDTO> getFriendSearch(String userTag, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 1. # 붙이는 파싱
        String tag = "#" + userTag;

        List<User> friends = userRepository.findByUserTag(tag);
        List<FriendResponse.SearchDTO> searchDTO = new ArrayList<>();
        for (User UserList : friends) {
            searchDTO.add(new FriendResponse.SearchDTO(UserList));
        }

        log.info("{}({})이 {}로 친구를 검색합니다.", userPS.getUsername(), userPS.getId(), userTag);

        return searchDTO;
    }

    public List<FriendResponse.UserDTO> getFriendList(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 나의 친구 전체 조회
        List<Friend> friendsPS = friendRepository.findfriendByUserIdJoinFriend(userPS.getId());

        // 상대방만 골라서 UserDTO로 변환
        List<FriendResponse.UserDTO> friendList = new ArrayList<>();

        for (Friend friend : friendsPS) {
            User other;
            if (friend.getFromUser().getId().equals(userPS.getId())) {
                other = friend.getToUser(); // 내가 from → 상대는 to
            } else {
                other = friend.getFromUser(); // 내가 to → 상대는 from
            }
            friendList.add(new FriendResponse.UserDTO(other));
        }

        log.info("{}({})이 친구 목록을 조회합니다.", userPS.getUsername(), userPS.getId());

        return friendList;
    }
}
