package com.example.tracky.community.posts.comments;

import com.example.tracky._core.enums.ErrorCodeEnum;
import com.example.tracky._core.error.ex.ExceptionApi403;
import com.example.tracky._core.error.ex.ExceptionApi404;
import com.example.tracky.community.posts.Post;
import com.example.tracky.community.posts.PostRepository;
import com.example.tracky.community.posts.likes.LikeService;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import com.example.tracky.user.utils.LoginIdUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeService likeService;

    public CommentResponse.CommentsList getCommentsWithReplies(Integer postId, Integer page) {

        postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        //한 페이지의 부모댓글과 자식댓글 수 합계
        Integer totalCount = commentRepository.countTotalCommentsInPage(postId, page);

        //부모댓글의 총 개수, 이걸로 totalPage 구함
        Integer parentCount = commentRepository.countParentComments(postId);

        // 1. 댓글(부모) 페이징 조회
        List<Comment> parentComments = commentRepository.findParentComments(postId, page);

        // 2. 부모 ID 목록 추출
        List<Integer> parentIds = parentComments.stream()
                .map(comment -> comment.getId())
                .toList();

        // 3. 대댓글 조회
        List<Comment> childComments = commentRepository.findChildCommentsByParentIds(parentIds);

        // 4. 대댓글을 parentId 기준으로 묶기
        Map<Integer, List<Comment>> replyMap = childComments.stream()
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        // 5. DTO로 변환
        List<CommentResponse.ParentDTO> parentDTOs = parentComments.stream()
                .map(parent -> new CommentResponse.ParentDTO(parent, replyMap.getOrDefault(parent.getId(), new ArrayList<>())))
                .toList();

        return new CommentResponse.CommentsList(page, totalCount, parentCount, parentDTOs);
    }

    public CommentResponse.UpdateDTO update(CommentRequest.UpdateDTO reqDTO, Integer commentId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Comment commentPS = commentRepository.findById(commentId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));

        if (!commentPS.getUser().getId().equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        commentPS.update(reqDTO.getContent());
        return new CommentResponse.UpdateDTO(commentPS);
    }


    public CommentResponse.SaveDTO save(CommentRequest.SaveDTO reqDTO, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post post = postRepository.findById(reqDTO.getPostId())
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        Comment parent = null;
        if (reqDTO.getParentId() != null) {
            parent = commentRepository.findById(reqDTO.getParentId())
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));
        }

        Comment comment = reqDTO.toEntity(userPS, post, parent);
        Comment commentPS = commentRepository.save(comment);

        return new CommentResponse.SaveDTO(commentPS);

    }

    @Transactional
    public void delete(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userPS.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }

        likeService.deleteByCommentId(id);

        commentRepository.delete(comment);
    }

}
