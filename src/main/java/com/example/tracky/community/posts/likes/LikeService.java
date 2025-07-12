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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeResponse.SaveDTO savePost(Integer postId, OAuthProfile sessionProfile) {

        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post postPS = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        Like likePS = likeRepository.save(toEntity(userPS, postPS));
        Integer likeCount = likeRepository.countByPostId(postId);
        return new LikeResponse.SaveDTO(likePS.getId(), likeCount);
    }

    private Like toEntity(User user, Post post) {
        return Like.builder()
                .user(user)
                .post(post)
                .build();
    }

    @Transactional
    public LikeResponse.SaveDTO saveComment(Integer commentId, OAuthProfile sessionProfile) {

        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Comment commentPS = commentRepository.findById(commentId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));

        Like likePS = likeRepository.save(toEntity(userPS, commentPS));
        Integer likeCount = likeRepository.countByCommentId(commentId);
        return new LikeResponse.SaveDTO(likePS.getId(), likeCount);
    }

    private Like toEntity(User user, Comment comment) {
        return Like.builder()
                .user(user)
                .comment(comment)
                .build();
    }

    @Transactional
    public LikeResponse.DeleteDTO delete(Integer id, OAuthProfile sessionProfile) {
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

        return new LikeResponse.DeleteDTO(likeCount);
    }

    @Transactional
    public void delete(Integer id) {

        likeRepository.deleteByPostId(id);

    }

}
