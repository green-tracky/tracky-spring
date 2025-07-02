package com.example.tracky.community.post;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.User;
import jakarta.persistence.EntityManager;
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

    @Autowired
    private EntityManager em;


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

    @Test
    void save_test() {
        User user = new User();
        em.persist(user);

        RunRecord runRecord = new RunRecord();
        em.persist(runRecord);

        Post post = Post.builder()
                .user(user)
                .title("title 출력")
                .content("content 출력")
                .runRecord(runRecord)
                .build();

        postRepository.save(post);

        log.info("결과확인===================");
        log.info("post.id: {}", post.getId());
        log.info("post.title: {}", post.getTitle());
        log.info("post.content: {}", post.getContent());
        log.info("user.id: {}", post.getUser().getId());
        log.info("runRecord.id: {}", post.getRunRecord().getId());
    }

}
