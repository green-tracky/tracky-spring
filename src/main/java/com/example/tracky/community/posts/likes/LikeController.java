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

    @PostMapping("/community/posts/{id}/likes")
    public ResponseEntity<?> save(@PathVariable("id") Integer id, @RequestBody LikeRequest.SaveDTO reqDTO) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.SaveDTO respDTO = likeService.save(reqDTO, sessionProfile);

        return Resp.ok(respDTO);
    }

    @DeleteMapping("/community/posts/{postId}/likes/{likeId}")
    public ResponseEntity<?> delete(@PathVariable("postId") Integer postId, @PathVariable("likeId") Integer likeId) {

        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        LikeResponse.DeleteDTO respDTO = likeService.delete(likeId, sessionProfile);

        return Resp.ok(respDTO);
    }
}
