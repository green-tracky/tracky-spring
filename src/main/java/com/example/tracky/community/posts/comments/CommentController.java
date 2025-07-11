package com.example.tracky.community.posts.comments;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class CommentController {

    private final CommentService commentService;
    private final HttpSession session;

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
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        CommentResponse.SaveDTO respDTO = commentService.save(reqDTO, sessionProfile);

        return Resp.ok(respDTO);
    }

    @PutMapping("/community/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> update(@PathVariable("postId") Integer postId, @PathVariable("commentId") Integer commentId, @RequestBody CommentRequest.UpdateDTO reqDTO) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        CommentResponse.UpdateDTO respDTO = commentService.update(reqDTO, commentId, sessionProfile);
        return Resp.ok(respDTO);
    }

}