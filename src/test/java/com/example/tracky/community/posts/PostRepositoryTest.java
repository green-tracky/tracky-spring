package com.example.tracky.community.posts;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.posts.comments.Comment;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Slf4j
@Import({PostRepository.class, UserRepository.class, RunRecordRepository.class})
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RunRecordRepository runRecordRepository;

    @Autowired
    private EntityManager em;


    @Test
    void find_all_join_run_record_test() {
        List<Post> posts = postRepository.findAllJoinRunRecord();
        for (Post post : posts) {
            log.debug("결과확인===================");
            log.debug("post.id: {}", post.getId());

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
        User user = userRepository.findById(1).
                orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        RunRecord runRecord = runRecordRepository.findById(1)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));

        Post post = Post.builder()
                .user(user)
                .content("content 출력")
                .runRecord(runRecord)
                .build();

        postRepository.save(post);

        log.debug("결과확인===================");
        log.debug("post.id: {}", post.getId());
        log.debug("post.content: {}", post.getContent());
        log.debug("user.id: {}", post.getUser().getId());
        log.debug("runRecord.id: {}", post.getRunRecord().getId());
    }

    @Test
    @Transactional
    void delete_test() {
        Integer postId = 1;

        // 게시글 조회
        Post post = em.find(Post.class, postId);
        if (post == null) {
            log.debug("❌ 삭제 대상 게시글이 존재하지 않습니다. postId = {}", postId);
            return;
        }

        // 댓글 좋아요 먼저 삭제
        List<Integer> commentIds = em.createQuery(
                        "select c.id from Comment c where c.post.id = :postId", Integer.class)
                .setParameter("postId", postId)
                .getResultList();

        if (!commentIds.isEmpty()) {
            int deletedLikes = em.createQuery("delete from Like l where l.comment.id in :commentIds")
                    .setParameter("commentIds", commentIds)
                    .executeUpdate();
            log.debug("✅ 댓글 좋아요 삭제 완료 ({}건)", deletedLikes);
        }

        // 게시글 좋아요 삭제
        int deletedPostLikes = em.createQuery("delete from Like l where l.post.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
        log.debug("✅ 게시글 좋아요 삭제 완료 ({}건)", deletedPostLikes);

        // 댓글을 자식부터 정렬해서 삭제
        List<Comment> comments = em.createQuery(
                        "select c from Comment c where c.post.id = :postId order by c.parent.id desc nulls last", Comment.class)
                .setParameter("postId", postId)
                .getResultList();

        for (Comment comment : comments) {
            em.remove(comment);
        }
        log.debug("✅ 댓글 삭제 완료 ({}건)", comments.size());

        // 게시글 삭제
        em.remove(post);
        em.flush();

        // 삭제 확인
        Post found = em.find(Post.class, postId);
        if (found == null) {
            log.debug("✅ 게시글 삭제 성공: postId = {}", postId);
        } else {
            log.debug("❌ 게시글 삭제 실패: postId = {}, title = {}", postId, found.getContent());
        }
    }

    @Test
    void update_test() {

        // given
        Integer postId = 1;

        // when
        Post postPS = postRepository.findById(postId).orElseThrow();
        var dto = new PostRequest.UpdateDTO();
        dto.setContent("수정된 내용");
        postPS.update(dto);

        // then
        Post updatedPost = postRepository.findById(postId).orElseThrow();
        log.debug("✅ 내용: {}", updatedPost.getContent());
    }

}
