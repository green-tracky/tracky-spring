package com.example.tracky.user;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void userRepository_test() {
        // given
        Integer id = 1;

        // when
        User userPS = userRepository.findById(id).orElseThrow();

        // eye
        log.debug("✅유저아이디: " + userPS.getId());
        log.debug("✅유저태그: " + userPS.getUserTag());
        log.debug("✅유저이름: " + userPS.getUsername());
        log.debug("✅유저제공자: " + userPS.getProvider());
    }
}
