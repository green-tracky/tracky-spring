package com.example.tracky.community.posts;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.posts.comments.CommentRepository;
import com.example.tracky.community.posts.comments.CommentResponse;
import com.example.tracky.community.posts.comments.CommentService;
import com.example.tracky.community.posts.likes.Like;
import com.example.tracky.community.posts.likes.LikeRepository;
import com.example.tracky.community.posts.likes.LikeService;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final RunRecordRepository runRecordRepository;
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final LikeService likeService;


    public List<PostResponse.ListDTO> getPosts(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        List<Post> postsPS = postRepository.findAllJoinRunRecord();

        log.info("{}({})이 게시글을 조회합니다.", userPS.getUsername(), userPS.getId());

        return postsPS.stream()
                .map(post -> {
                    Like like = likeRepository.findByUserIdAndPostId(userPS.getId(), post.getId()).orElse(null);
                    Integer likeCount = likeRepository.countByPostId(post.getId());
                    Integer commentCount = commentRepository.countByPostId(post.getId());
                    boolean isLiked = like != null;

                    return new PostResponse.ListDTO(
                            post,
                            likeCount,
                            commentCount,
                            isLiked
                    );
                })
                .toList();
    }

    @Transactional
    public PostResponse.SaveDTO save(PostRequest.SaveDTO reqDTO, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 러닝 조회
        RunRecord runRecord = null;
        if (reqDTO.getRunRecordId() != null) {
            runRecord = runRecordRepository.findById(reqDTO.getRunRecordId())
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));
        }

        // 게시글 엔티티 생성
        Post post = reqDTO.toEntity(userPS, runRecord);

        // 게시글 저장
        Post postPS = postRepository.save(post);


        log.info("{}({})이 게시글{}번을 저장합니다.", userPS.getUsername(), userPS.getId(), postPS.getId());
        // 응답 DTO 변환
        return new PostResponse.SaveDTO(postPS);
    }

    @Transactional
    public PostResponse.UpdateDTO update(PostRequest.UpdateDTO reqDTO, Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 게시글 조회
        Post postPS = postRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        // 권한 체크
        checkAccess(userPS, postPS);

        postPS.update(reqDTO);

        // updatedAt 적용
        userRepository.save(userPS);

        log.info("{}({})이 게시글{}을 수정합니다.", userPS.getUsername(), userPS.getId(), postPS.getId());

        return new PostResponse.UpdateDTO(postPS);
    }


    @Transactional
    public void delete(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 게시글 조회
        Post postPS = postRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        // 권한 체크
        checkAccess(userPS, postPS);

        // 좋아요 삭제
        likeService.deleteByPostId(id);

        log.info("{}({})이 게시글{}을 삭제했습니다.", userPS.getUsername(), userPS.getId(), postPS.getId());

        postRepository.delete(postPS);
    }


    public PostResponse.DetailDTO getPostDetail(Integer postId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        Integer likeCount = likeRepository.countByPostId(postId);
        Integer commentCount = commentRepository.countByPostId(postId);
        Like like = likeRepository.findByUserIdAndPostId(userPS.getId(), post.getId()).orElse(null);
        boolean isLiked = like != null;

        // ✅ 댓글 + 대댓글 조회
        CommentResponse.CommentsList commentsList = commentService.getCommentsWithReplies(postId, 1);

        log.info("{}({})이 {}을 상세보기합니다.", userPS.getUsername(), userPS.getId(), post.getId());

        return new PostResponse.DetailDTO(post, commentsList, likeCount, commentCount, isLiked, userPS);
    }

    /**
     * 게시물에 대한 사용자의 접근 권한을 확인합니다.
     * 권한이 없을 경우 ExceptionApi403 예외를 발생시킵니다.
     *
     * @param user 현재 로그인한 사용자
     * @param post
     */
    private void checkAccess(User user, Post post) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

}
