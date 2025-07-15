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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeService likeService;

    public CommentResponse.CommentsList getCommentsWithReplies(Integer postId, Integer page) {

        // 해당 게시글이 존재하는지 확인
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

        // 4. DTO로 변환
        List<CommentResponse.ParentDTO> parentDTOs = parentComments.stream()
                .map(parent -> new CommentResponse.ParentDTO(parent))
                .toList();

        log.info("대댓글을 조회합니다.");

        return new CommentResponse.CommentsList(page, totalCount, parentCount, parentDTOs);
    }

    public CommentResponse.UpdateDTO update(CommentRequest.UpdateDTO reqDTO, Integer commentId, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Comment commentPS = commentRepository.findById(commentId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));

        checkAccess(userPS, commentPS);

        commentPS.update(reqDTO);

        commentRepository.save(commentPS);

        log.info("{}({})이 댓글({})을 수정합니다.", userPS.getUsername(), userPS.getId(), commentPS.getId());

        return new CommentResponse.UpdateDTO(commentPS);
    }


    public CommentResponse.SaveDTO save(Integer postId, CommentRequest.SaveDTO reqDTO, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Post postPS = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.POST_NOT_FOUND));

        Comment parentPS = null;
        if (reqDTO.getParentId() != null) {
            parentPS = commentRepository.findById(reqDTO.getParentId())
                    .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));
        }

        Comment comment = reqDTO.toEntity(userPS, postPS, parentPS);
        Comment commentPS = commentRepository.save(comment);

        log.info("{}({})이 댓글({})을 저장합니다.", userPS.getUsername(), userPS.getId(), commentPS.getId());

        return new CommentResponse.SaveDTO(commentPS);

    }

    @Transactional
    public void delete(Integer id, OAuthProfile sessionProfile) {
        // 사용자 조회
        User userPS = userRepository.findByLoginId(LoginIdUtil.makeLoginId(sessionProfile))
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.USER_NOT_FOUND));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ExceptionApi404(ErrorCodeEnum.COMMENT_NOT_FOUND));

        checkAccess(userPS, comment);

        likeService.deleteByCommentId(id);

        commentRepository.delete(comment);

        log.info("{}({})이 댓글({})을 삭제합니다", userPS.getUsername(), userPS.getId(), comment.getId());
    }

    /**
     * 댓글에 대한 사용자의 접근 권한을 확인합니다.
     * 권한이 없을 경우 ExceptionApi403 예외를 발생시킵니다.
     *
     * @param user    현재 로그인한 사용자
     * @param comment
     */
    private void checkAccess(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ExceptionApi403(ErrorCodeEnum.ACCESS_DENIED);
        }
    }

}
