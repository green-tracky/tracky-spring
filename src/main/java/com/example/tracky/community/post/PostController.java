package com.example.tracky.community.post;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s/api")
public class PostController {

    private final PostService postService;

    @GetMapping("/community/posts")
    public ResponseEntity<?> getPosts() {

        Integer userId = 1;

        User user = User.builder().id(userId).build();

        List<PostResponse.ListDTO> respDTOs = postService.getPosts(userId);
        return Resp.ok(respDTOs);
    }
}
