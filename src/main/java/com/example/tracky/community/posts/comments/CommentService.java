package com.example.tracky.community.posts.comments;

import com.example.tracky.community.posts.Post;
import com.example.tracky.community.posts.PostRepository;
import com.example.tracky.user.User;
import com.example.tracky.user.UserRepository;
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

    public CommentResponse.CommentsList getCommentsWithReplies(Integer postId, Integer page) {

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
        List<CommentResponse.ParentDTO> parentDTOS = parentComments.stream()
                .map(parent -> new CommentResponse.ParentDTO(parent, replyMap.getOrDefault(parent.getId(), new ArrayList<>())))
                .toList();

        return new CommentResponse.CommentsList(page, totalCount, parentCount, parentDTOS);
    }

    public CommentResponse.SaveDTO save(CommentRequest.SaveDTO reqDTO, User user) {

        Post post = postRepository.findById(reqDTO.getPostId())
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        Comment parent = null;
        if (reqDTO.getParentId() != null) {
            parent = commentRepository.findById(reqDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("부모 댓글 없음"));
        }

        Comment comment = reqDTO.toEntity(user, post, parent);
        Comment commentPS = commentRepository.save(comment);

        return new CommentResponse.SaveDTO(commentPS);

    }

}
