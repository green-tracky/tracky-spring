package com.example.tracky.community.posts;

import com.example.tracky._core.error.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.posts.comments.CommentRepository;
import com.example.tracky.community.posts.likes.Like;
import com.example.tracky.community.posts.likes.LikeRepository;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.RunRecordRepository;
import com.example.tracky.runrecord.picture.Picture;
import com.example.tracky.runrecord.picture.PictureRepository;
import com.example.tracky.user.User;
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

    public List<PostResponse.ListDTO> getPosts(User user) {
        List<Post> postsPS = postRepository.findAllJoinRunRecord();

        return postsPS.stream()
                .map(post -> {
                    Like like = likeRepository.findByUserIdAndPostId(user.getId(), post.getId()).orElse(null);
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
    public PostResponse.SaveDTO save(PostRequest.SaveDTO reqDTO, User user) {

        // 러닝 조회
        RunRecord runRecord = null;
        if (reqDTO.getRunRecordId() != null) {
            runRecord = runRecordRepository.findById(reqDTO.getRunRecordId())
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));
        }

        // 사진 조회
        List<Picture> pictures = new ArrayList<>();
        if (reqDTO.getPicturesId() != null && !reqDTO.getPicturesId().isEmpty()) {
            pictures = pictureRepository.findAllById(reqDTO.getPicturesId());
        }

        // 게시글 엔티티 생성
        Post post = reqDTO.toEntity(user, runRecord, pictures);

        // 게시글 저장
        Post postPS = postRepository.save(post);

        // 응답 DTO 변환
        return new PostResponse.SaveDTO(postPS);
    }

    @Transactional
    public PostResponse.UpdateDTO update(PostRequest.UpdateDTO reqDTO, Integer id, User user) {
        Post postPS = postRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        if (!postPS.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }


        // RunRecord 변경 여부 확인 및 조회
        RunRecord runRecord = postPS.getRunRecord();
        if (!reqDTO.getRunRecordId().equals(postPS.getRunRecord().getId())) {
            runRecord = runRecordRepository.findById(reqDTO.getRunRecordId())
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.RUN_NOT_FOUND));
        }

        // 사진 조회
        List<Picture> pictures = new ArrayList<>();
        if (reqDTO.getPicturesId() != null && !reqDTO.getPicturesId().isEmpty()) {
            pictures = pictureRepository.findAllById(reqDTO.getPicturesId());
        }

        postPS.update(reqDTO.getContent(), runRecord, pictures);

        return new PostResponse.UpdateDTO(postPS);
    }

}
