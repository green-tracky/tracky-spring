package com.example.tracky.community.post;

import com.example.tracky.community.like.Like;
import com.example.tracky.community.like.LikeRepository;
import com.example.tracky.community.post.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final LikeRepository likeRepository;

    private final CommentRepository commentRepository;

    public List<PostResponse.ListDTO> getList(Integer userId) {
        List<Post> postsPS = postRepository.findAllWithRunRecord();  // user + runRecord까지 fetch된 상태

        return postsPS.stream()
                .map(post -> {
                    Like like = likeRepository.findByUserIdAndPostId(userId, post.getId()).orElse(null);
                    Long likeCount = likeRepository.findByPostId(post.getId());
                    Long commentCount = commentRepository.findByPostId(post.getId());
                    boolean isLiked = like != null;

                    return new PostResponse.ListDTO(
                            post,
                            post.getRunRecord(),              // Post에서 runRecord 꺼내기
                            likeCount.intValue(),
                            commentCount.intValue(),
                            isLiked
                    );
                })
                .toList();
    }
}
