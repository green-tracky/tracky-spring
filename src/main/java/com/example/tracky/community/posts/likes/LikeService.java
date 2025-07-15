package com.example.tracky.community.posts.likes;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.posts.Post;
import com.example.tracky.community.posts.PostRepository;
import com.example.tracky.community.posts.comments.Comment;
import com.example.tracky.community.posts.comments.CommentRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeResponse.SaveDTO likePost(Integer postId, OAuthProfile sessionProfile) {

        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post postPS = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        Like like = Like.builder()
                .user(userPS)
                .post(postPS)
                .comment(null)
                .build();

        Like likePS = likeRepository.save(like);

        Integer likeCount = likeRepository.countByPostId(postId);

        log.info("{}({})이 게시글{}를 좋아요합니다. ", userPS.getUsername(), userPS.getId(), postPS.getId());

        return new LikeResponse.SaveDTO(likePS.getId(), likeCount);
    }

    @Transactional
    public LikeResponse.SaveDTO likeComment(Integer commentId, OAuthProfile sessionProfile) {

        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Comment commentPS = commentRepository.findById(commentId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));

        Like like = Like.builder()
                .user(userPS)
                .comment(commentPS)
                .post(null)
                .build();

        Like likePS = likeRepository.save(like);

        Integer likeCount = likeRepository.countByCommentId(commentId);

        log.info("{}({})이 댓글 {}({})를 좋아요합니다. ", userPS.getUsername(), userPS.getId(), commentPS.getContent(), commentPS.getId());

        return new LikeResponse.SaveDTO(likePS.getId(), likeCount);
    }

    @Transactional
    public LikeResponse.DeleteDTO dislikePost(Integer id, OAuthProfile sessionProfile) {
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Like likePS = likeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.LIKE_NOT_FOUND));

        if (!likePS.getUser().getId().equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        Integer postId = likePS.getPost().getId();

        likeRepository.deleteById(id);

        Integer likeCount = likeRepository.countByPostId(postId);

        log.info("{}({})이 게시글{}번을 좋아요 취소했습니다.", userPS.getUsername(), userPS.getId(), postId);


        return new LikeResponse.DeleteDTO(likeCount);
    }

    @Transactional
    public LikeResponse.DeleteDTO dislikeComment(Integer id, OAuthProfile sessionProfile) {
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Like likePS = likeRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.LIKE_NOT_FOUND));

        if (!likePS.getUser().getId().equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        Integer commentId = likePS.getComment().getId();

        likeRepository.deleteById(id);

        Integer likeCount = likeRepository.countByCommentId(commentId);

        log.info("{}({})이 댓글{}번을 좋아요 취소했습니다.", userPS.getUsername(), userPS.getId(), commentId);


        return new LikeResponse.DeleteDTO(likeCount);
    }


    @Transactional
    public void deleteByPostId(Integer id) {

        likeRepository.deleteByPostId(id);

    }

    @Transactional
    public void deleteByCommentId(Integer id) {

        likeRepository.deleteByCommentId(id);

    }

}
