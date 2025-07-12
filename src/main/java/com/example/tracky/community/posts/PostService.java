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
import com.example.tracky.runrecord.pictures.Picture;
import com.example.tracky.runrecord.pictures.PictureRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final RunRecordRepository runRecordRepository;
    private final PictureRepository pictureRepository;
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final LikeService likeService;


    public List<PostResponse.ListDTO> getPosts(OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        List<Post> postsPS = postRepository.findAllJoinRunRecord();

        return postsPS.stream()
                .map(post -> {
                    Like like = likeRepository.findByUserIdAndPostId(userPS.getId(), post.getId()).orElse(null);
                    Integer likeCount = likeRepository.countByPostId(post.getId());
                    Integer commentCount = commentRepository.countByPostId(post.getId());
                    boolean isLiked = like != null;

                    return new PostResponse.ListDTO(
                            post,
                            post.getPostPictures(),
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

        // 사진 조회
        List<Picture> pictures = new ArrayList<>();
        if (reqDTO.getPictureIds() != null && !reqDTO.getPictureIds().isEmpty()) {
            pictures = pictureRepository.findAllById(reqDTO.getPictureIds());
        }

        // 게시글 엔티티 생성
        Post post = reqDTO.toEntity(userPS, runRecord, pictures);

        // 게시글 저장
        Post postPS = postRepository.save(post);

        // 응답 DTO 변환
        return new PostResponse.SaveDTO(postPS);
    }

    @Transactional
    public PostResponse.UpdateDTO update(PostRequest.UpdateDTO reqDTO, Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post postPS = postRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        if (!postPS.getUser().getId().equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }


        // RunRecord 변경 로직
        RunRecord runRecord = null;
        Integer reqRunRecordId = reqDTO.getRunRecordId(); // 요청에서 받은 러닝 ID
        RunRecord currentRunRecord = postPS.getRunRecord(); // 현재 저장된 러닝 기록

        if (currentRunRecord == null && reqRunRecordId != null) {
            // 러닝이 없었는데 새로 들어온 경우
            runRecord = runRecordRepository.findById(reqRunRecordId)
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));
        } else if (currentRunRecord != null && reqRunRecordId == null) {
            // 기존 러닝이 있었는데 요청에 없다면 제거
            runRecord = null;
        } else if (currentRunRecord != null && reqRunRecordId != null) {
            // 기존과 요청 둘 다 존재하는데 ID가 다르면 변경
            if (!currentRunRecord.getId().equals(reqRunRecordId)) {
                runRecord = runRecordRepository.findById(reqRunRecordId)
                        .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));
            } else {
                // 기존과 동일하다면 유지
                runRecord = currentRunRecord;
            }
        }

        // 사진 조회
        List<Picture> pictures = new ArrayList<>();
        if (reqDTO.getPictureIds() != null && !reqDTO.getPictureIds().isEmpty()) {
            pictures = pictureRepository.findAllById(reqDTO.getPictureIds());
        }

        postPS.update(reqDTO.getContent(), runRecord, pictures);

        return new PostResponse.UpdateDTO(postPS);
    }


    @Transactional
    public void delete(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
        likeService.deleteByPostId(id);

        postRepository.delete(post);
    }


    public PostResponse.DetailDTO getPostDetail(Integer postId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        Integer likeCount = likeRepository.countByPostId(postId);
        Integer commentCount = commentRepository.countByPostId(postId);
        Like like = likeRepository.findByUserIdAndPostId(userPS.getId(), post.getId()).orElse(null);
        boolean isLiked = like != null;

        List<PostPicture> pictures = post.getPostPictures();

        // ✅ 댓글 + 대댓글 조회
        CommentResponse.CommentsList commentsList = commentService.getCommentsWithReplies(postId, 1);

        return new PostResponse.DetailDTO(post, commentsList, pictures, likeCount, commentCount, isLiked, userPS);

    }

}
