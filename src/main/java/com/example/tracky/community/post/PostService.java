package com.example.tracky.community.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<PostResponse.DTO> getPosts() {
        List<Post> posts = postRepository.findAll();
        return PostResponse.DTO.toPostResponseDTOs(posts);
    }
}
