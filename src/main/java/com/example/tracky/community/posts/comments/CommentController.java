package com.example.tracky.community.posts.comments;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.community.posts.PostRequest;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        if (page == null || page < 1) {
            page = 1;
        }

        CommentResponse.CommentsList respDTO = commentService.getCommentsWithReplies(postId, page);
        return Resp.ok(respDTO);
    }

    @PostMapping("/community/posts/comments")
    public ResponseEntity<?> save(@RequestBody CommentRequest.SaveDTO reqDTO) {
        Integer userId = 1;
        User user = User.builder().id(userId).build();

        CommentResponse.SaveDTO respDTO = commentService.save(reqDTO, user);
        return Resp.ok(respDTO);
    }

    @PutMapping("/community/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> update(@PathVariable("postId") Integer postId, @PathVariable("commentId") Integer commentId, @RequestBody PostRequest.UpdateDTO reqDTO) {
        Integer userId = 3;
        User user = User.builder().id(userId).build();

        CommentResponse.UpdateDTO respDTO = commentService.update(reqDTO, commentId, user);

        return Resp.ok(respDTO);
    }

}