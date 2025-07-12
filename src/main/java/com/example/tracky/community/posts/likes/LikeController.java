package com.example.tracky.community.posts.likes;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s/api")
public class LikeController {

    private final LikeService likeService;
    private final HttpSession session;

    @PostMapping("/community/posts/{postId}/likes")
    public ResponseEntity<?> savePost(@PathVariable("postId") Integer postId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.SaveDTO respDTO = likeService.savePost(postId, sessionProfile);

        return Resp.ok(respDTO);
    }

    @PostMapping("/community/comments/{commentId}/likes")
    public ResponseEntity<?> saveComment(@PathVariable("commentId") Integer commentId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.SaveDTO respDTO = likeService.saveComment(commentId, sessionProfile);

        return Resp.ok(respDTO);
    }

    @DeleteMapping("/community/posts/{postId}/likes/{likeId}")
    public ResponseEntity<?> delete(@PathVariable("postId") Integer postId, @PathVariable("likeId") Integer likeId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.DeleteDTO respDTO = likeService.delete(likeId, sessionProfile);

        return Resp.ok(respDTO);
    }

}
