package com.example.tracky.community.posts;

import com.example.tracky._core.constants.SessionKeys;
import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.kakaojwt.OAuthProfile;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class PostController {

    private final PostService postService;
    private final HttpSession session;

    @GetMapping("/community/posts")
    public ResponseEntity<?> getPosts() {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        List<PostResponse.ListDTO> respDTOs = postService.getPosts(sessionProfile);
        return Resp.ok(respDTOs);
    }

    @PostMapping("/community/posts")
    public ResponseEntity<?> save(@Valid @RequestBody PostRequest.SaveDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        PostResponse.SaveDTO respDTO = postService.save(reqDTO, sessionProfile);
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/community/posts/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        postService.delete(id, sessionProfile);
        return Resp.ok(null);
    }

    @PutMapping("/community/posts/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @Valid @RequestBody PostRequest.UpdateDTO reqDTO, Errors errors) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        PostResponse.UpdateDTO respDTO = postService.update(reqDTO, id, sessionProfile);

        return Resp.ok(respDTO);
    }

    @GetMapping("/community/posts/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable Integer id) {
        // 세션에서 유저 정보 꺼내기
        OAuthProfile sessionProfile = (OAuthProfile) session.getAttribute(SessionKeys.PROFILE);

        PostResponse.DetailDTO respDTO = postService.getPostDetail(id, sessionProfile);
        return Resp.ok(respDTO);
    }
}
