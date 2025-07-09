package com.example.tracky.community.posts.comments;

import com.example.tracky._core.utils.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class CommentController {

    private final CommentService commentService;

    // GET /community/posts/{postId}/comments?page=2
    @GetMapping("/community/posts/{postId}/comments")
    public ResponseEntity<?> getComments(
            @PathVariable Integer postId,
            @RequestParam(defaultValue = "1") Integer page // page 파라미터 없으면 기본값 1
    ) {
        List<CommentResponse.DTO> respDTO = commentService.getCommentsWithReplies(postId, page);
        return Resp.ok(respDTO);
    }
}