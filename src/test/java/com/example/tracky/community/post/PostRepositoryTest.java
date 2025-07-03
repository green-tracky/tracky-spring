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
            log.debug("결과확인===================");
            log.debug("post.id: {}", post.getId());
            log.debug("post.title: {}", post.getTitle());

            if (post.getUser() != null) {
                log.debug("user.id: {}", post.getUser().getId());
                log.debug("user.username: {}", post.getUser().getUsername());
            }

            if (post.getRunRecord() != null) {
                log.debug("runRecord.id: {}", post.getRunRecord().getId());
                log.debug("runRecord.memo: {}", post.getRunRecord().getMemo());
            }
        }
    }

    @Test
    void save_test() {
        User user = User.builder().build();
        em.persist(user);

        RunRecord runRecord = RunRecord.builder().build();
        em.persist(runRecord);

        Post post = Post.builder()
                .user(user)
                .title("title 출력")
                .content("content 출력")
                .runRecord(runRecord)
                .build();

        postRepository.save(post);

        log.debug("결과확인===================");
        log.debug("post.id: {}", post.getId());
        log.debug("post.title: {}", post.getTitle());
        log.debug("post.content: {}", post.getContent());
        log.debug("user.id: {}", post.getUser().getId());
        log.debug("runRecord.id: {}", post.getRunRecord().getId());
    }

    @Test
    void update_test() {

        // given
        User user = User.builder()
                .build();
        em.persist(user);

        Post post = Post.builder()
                .title("원래 제목")
                .content("원래 내용")
                .user(user)
                .build();
        postRepository.save(post);

        // when
        Post postPS = postRepository.findById(post.getId()).orElseThrow();
        postPS.update("수정된 제목", "수정된 내용", null);

        // then
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        log.debug("✅ 제목: {}", updatedPost.getTitle());
        log.debug("✅ 내용: {}", updatedPost.getContent());
    }

}
