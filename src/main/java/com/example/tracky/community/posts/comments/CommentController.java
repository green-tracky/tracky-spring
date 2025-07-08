package com.example.tracky.community.posts.comments;

import com.example.tracky._core.utils.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // GET /community/posts/{postId}/comments?page=2
    @GetMapping("/community/posts/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "1") int page // page 파라미터 없으면 기본값 1
    ) {
        List<CommentResponse.DTO> respDTO = commentService.getCommentsWithReplies(postId, page);
        return Resp.ok(respDTO);
    }
}