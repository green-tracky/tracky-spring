package com.example.tracky.community.posts.comments;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import(CommentRepository.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    @Test
    void findParentComments_test() {
        Integer postId = 1;
        int page = 1;

        List<Comment> comments = commentRepository.findParentComments(postId, page);
        for (Comment comment : comments) {
            log.debug("comment.content: {}", comment.getContent());
        }
    }

    @Test
    void findChildCommentsByParentIds_test() {

        // given
        List<Integer> parentIds = List.of(1, 2, 3); // 테스트용 부모 댓글 ID 목록

        // when
        List<Comment> comments = commentRepository.findChildCommentsByParentIds(parentIds);

        // then
        for (Comment c : comments) {
            log.debug("child comment = {}", c.getContent());
        }
    }
}
