package com.example.tracky.user.friend;

import com.example.tracky.user.friends.FriendRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class FriendRepositoryTest {

    @Autowired
    private FriendRepository friendRepository;

    @Test
    public void userRepository_test() {
        // given
        String Tag = "#121JKL";
//        String Tag = "#11B2C3";

        // when
//        List<Friend> friendList = friendRepository.findByUserTag(Tag);

        // eye
//        for (Friend friend : friendList) {
//            log.info("회원 정보" + friend.toString());
//        }
    }
}
