package com.example.tracky.community.posts.comments;


import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.posts.Post;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.util.List;

@Slf4j
@Import({CommentRepository.class, UserRepository.class})
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Test
    void find_parent_comments_test() {
        Integer postId = 1;
        int page = 1;

        List<Comment> comments = commentRepository.findParentComments(postId, page);
        for (Comment comment : comments) {
            log.debug("comment.content: {}", comment.getContent());
        }
    }

    @Test
    void find_child_comments_by_parent_ids_test() {

        // given
        List<Integer> parentIds = List.of(1, 2, 3); // 테스트용 부모 댓글 ID 목록

        // when
        List<Comment> comments = commentRepository.findChildCommentsByParentIds(parentIds);

        // then
        for (Comment c : comments) {
            log.debug("child comment = {}", c.getContent());
        }
    }


    @Test
    void count_total_comments_in_page_test() {
        Integer postId = 1;
        Integer page = 1;

        Integer totalCount = commentRepository.countTotalCommentsInPage(postId, page);
        log.debug(String.valueOf(totalCount));
    }

    @Test
    void count_parent_comments_test() {
        Integer postId = 1;

        Integer parentCount = commentRepository.countParentComments(postId);
        log.debug(String.valueOf(parentCount));
    }

    @Test
    void save_test() {
        User user = userRepository.findById(1).
                orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post post = Post.builder()
                .user(user)
                .build();
        em.persist(post);

        Comment comment = Comment.builder()
                .user(user)
                .post(post)
                .content("content")
                .build();

        commentRepository.save(comment);

        log.debug("결과확인===================");
        log.debug("post.id: {}", comment.getId());
        log.debug("post.content: {}", comment.getContent());
        log.debug("user.id: {}", comment.getUser().getId());
    }

    @Test
    void delete_test() {

        Integer parentId = 1;

        int deletedLikes = em.createQuery("delete from Like l where l.comment.id = :commentId")
                .setParameter("commentId", parentId)
                .executeUpdate();
        log.debug("✅ 댓글 좋아요 삭제 완료 ({}건)", deletedLikes);

        // 대댓글 먼저 삭제
        em.createQuery("delete from Comment c where c.parent.id = :parentId")
                .setParameter("parentId", parentId)
                .executeUpdate();

        // 부모 댓글 삭제
        int deletedComment = em.createQuery("delete from Comment c where c.id = :id")
                .setParameter("id", parentId)
                .executeUpdate();

        log.debug("✅ 댓글 삭제 완료 ({}건)", deletedComment);
    }

}
