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
    public ResponseEntity<?> likePost(@PathVariable("postId") Integer postId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.SaveDTO respDTO = likeService.likePost(postId, sessionProfile);

        return Resp.ok(respDTO);
    }

    @PostMapping("/community/comments/{commentId}/likes")
    public ResponseEntity<?> likeComment(@PathVariable("commentId") Integer commentId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.SaveDTO respDTO = likeService.likeComment(commentId, sessionProfile);

        return Resp.ok(respDTO);
    }

    @DeleteMapping("/community/posts/{postId}/likes/{likeId}")
    public ResponseEntity<?> dislikePost(@PathVariable("likeId") Integer likeId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.DeleteDTO respDTO = likeService.dislikePost(likeId, sessionProfile);

        return Resp.ok(respDTO);
    }

    @DeleteMapping("/community/comments/{commentId}/likes/{likeId}")
    public ResponseEntity<?> dislikeComment(@PathVariable("likeId") Integer likeId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.DeleteDTO respDTO = likeService.dislikeComment(likeId, sessionProfile);

        return Resp.ok(respDTO);
    }

}
