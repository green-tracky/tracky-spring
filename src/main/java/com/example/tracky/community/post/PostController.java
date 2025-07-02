package com.example.tracky.community.post;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        List<PostResponse.ListDTO> respDTOs = postService.getPosts(user);
        return Resp.ok(respDTOs);
    }

    @PostMapping("/community/posts")
    public ResponseEntity<?> save(@Valid @RequestBody PostRequest.SaveDTO reqDTO) {
        Integer userId = 1;
        User user = User.builder().id(userId).build();

        PostResponse.SaveDTO respDTO = postService.save(reqDTO, user);
        return Resp.ok(respDTO);
    }
}
