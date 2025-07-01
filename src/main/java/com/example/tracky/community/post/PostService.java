package com.example.tracky.community.post;

import com.example.tracky.community.like.Like;
import com.example.tracky.community.like.LikeRepository;
import com.example.tracky.community.post.comment.CommentRepository;
import com.example.tracky.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final LikeRepository likeRepository;

    private final CommentRepository commentRepository;

    public List<PostResponse.ListDTO>
    getPosts(User user) {
        List<Post> postsPS = postRepository.findAllJoinRunRecord();

        return postsPS.stream()
                .map(post -> {
                    Like like = likeRepository.findByUserIdAndPostId(user.getId(), post.getId()).orElse(null);
                    Integer likeCount = likeRepository.countByPostId(post.getId());
                    Integer commentCount = commentRepository.countByPostId(post.getId());
                    boolean isLiked = like != null;

                    return new PostResponse.ListDTO(
                            post,
                            post.getRunRecord(),
                            likeCount,
                            commentCount,
                            isLiked
                    );
                })
                .toList();
    }
}
