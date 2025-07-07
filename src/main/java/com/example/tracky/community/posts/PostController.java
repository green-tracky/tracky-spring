package com.example.tracky.community.posts;

import com.example.tracky._core.utils.Resp;
import com.example.tracky.user.User;
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
    public ResponseEntity<?> save(@RequestBody PostRequest.SaveDTO reqDTO) {
        Integer userId = 1;
        User user = User.builder().id(userId).build();

        PostResponse.SaveDTO respDTO = postService.save(reqDTO, user);
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/community/posts/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        postService.delete(id);
        return Resp.ok(null);
    }

    @PutMapping("/community/posts/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody PostRequest.UpdateDTO reqDTO) {
        Integer userId = 1;
        User user = User.builder().id(userId).build();

        PostResponse.UpdateDTO respDTO = postService.update(reqDTO, id, user);

        return Resp.ok(respDTO);
    }

    @GetMapping("/community/posts/{id}")
    public ResponseEntity<?> getPostDetail(@PathVariable int id) {

        Integer userId = 1;

        User user = User.builder().id(userId).build();

        PostResponse.DetailDTO respDTO = postService.getPostDetail(id, user);
        return Resp.ok(respDTO);
    }
}
