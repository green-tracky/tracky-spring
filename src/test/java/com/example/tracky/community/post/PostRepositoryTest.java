package com.example.tracky.community.post;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import(PostRepository.class)
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void findAllJoinRunRecord_test() {
        List<Post> posts = postRepository.findAllJoinRunRecord();
        for (Post post : posts) {
            log.info("결과확인===================");
            log.info("post.id: {}", post.getId());
            log.info("post.title: {}", post.getTitle());

            if (post.getUser() != null) {
                log.info("user.id: {}", post.getUser().getId());
                log.info("user.username: {}", post.getUser().getUsername());
            }

            if (post.getRunRecord() != null) {
                log.info("runRecord.id: {}", post.getRunRecord().getId());
                log.info("runRecord.memo: {}", post.getRunRecord().getMemo());
            }
        }
    }

}
