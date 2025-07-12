package com.example.tracky.community.posts.likes;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.posts.Post;
import com.example.tracky.community.posts.PostRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@Slf4j
@Import({LikeRepository.class, UserRepository.class, PostRepository.class})
@DataJpaTest
public class LikeRepositoryTest {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void count_by_post_id_test() {
        Integer postId = 1;

        Integer count = likeRepository.countByPostId(postId);
        log.debug(count.toString());
    }

    @Test
    void count_by_comment_id_test() {
        Integer commentId = 1;

        Integer count = likeRepository.countByCommentId(commentId);
        log.debug(count.toString());
    }

    @Test
    void find_by_user_id_and_post_id_test() {
        Integer userId = 3;
        Integer postId = 1;
        Optional<Like> like = likeRepository.findByUserIdAndPostId(userId, postId);
        if (like.isPresent()) {
            Like l = like.get();
            log.debug("👍 좋아요 있음 -> id: {}, userId: {}, postId: {}", l.getId(), l.getUser().getId(), l.getPost().getId());
        } else {
            log.debug("👎 좋아요 없음");
        }
    }

    @Test
    void save_test() {
        User user = userRepository.findById(1)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post post = postRepository.findById(2)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        Like like = Like.builder()
                .user(user)
                .post(post)
                .build();

        likeRepository.save(like);

        log.debug("결과확인===================");
        log.debug("like.id: {}", like.getId());
        log.debug("user.id: {}", like.getUser().getId());
        log.debug("post.id: {}", like.getPost().getId());
    }


    @Test
    void find_by_id_test() {
        Integer likeId = 3;

        Optional<Like> like = likeRepository.findById(likeId);
        if (like.isPresent()) {
            Like l = like.get();
            log.debug("👍 좋아요 있음 -> id: {}, userId: {}, postId: {}", l.getId(), l.getUser().getId(), l.getPost().getId());
        } else {
            log.debug("👎 좋아요 없음");
        }
    }

    @Test
    void delete_by_id_test() {
        Integer likeId = 3;

        likeRepository.deleteById(likeId);
        boolean exists = likeRepository.findById(likeId).isPresent();
        log.debug("삭제 후 Like 존재 여부: {}", exists); // false가 되어야 성공
    }

    @Test
    void delete_by_post_id_test() {
        Integer postId = 1;

        long beforeCount = likeRepository.countByPostId(postId);
        log.debug("삭제 전 like 개수: {}", beforeCount);

        likeRepository.deleteByPostId(postId);

        long afterCount = likeRepository.countByPostId(postId);
        log.debug("삭제 후 like 개수: {}", afterCount);
    }

    @Test
    void delete_by_comment_id_test() {
        Integer commentId = 1;

        long beforeCount = likeRepository.countByCommentId(commentId);
        log.debug("삭제 전 like 개수: {}", beforeCount);

        likeRepository.deleteByCommentId(commentId);

        long afterCount = likeRepository.countByCommentId(commentId);
        log.debug("삭제 후 like 개수: {}", afterCount);
    }

}
