package com.example.tracky.community.posts.comments;

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

    public List<CommentResponse.DTO> getCommentsWithReplies(Long postId, int page) {
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
        return parentComments.stream()
                .map(parent -> new CommentResponse.DTO(parent, replyMap.getOrDefault(parent.getId(), new ArrayList<>())))
                .toList();
    }
}
