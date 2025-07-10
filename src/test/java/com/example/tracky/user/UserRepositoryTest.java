package com.example.tracky.user;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByIdJoin_test() {
        // given
        Integer id = 1;

        // when
        User userPS = userRepository.findByIdJoin(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // eye
        log.debug("✅유저아이디: " + userPS.getId());
        log.debug("✅유저태그: " + userPS.getUserTag());
        log.debug("✅유저이름: " + userPS.getUsername());
        log.debug("✅유저제공자: " + userPS.getProvider());
    }

    @Test
    public void findAllUserTag_test() {
        // given

        // when
        List<String> userTagsPS = userRepository.findAllUserTag();

        // eye
        log.debug("✅유저태그: " + userTagsPS.get(0));
    }
}
