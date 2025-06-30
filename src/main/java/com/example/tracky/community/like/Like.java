package com.example.tracky.community.like;

import com.example.tracky.community.post.Post;
import com.example.tracky.community.post.comment.Comment;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "like_tb")
@Getter
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 좋아요 대상 게시글 (null 불가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Post post;

    // 좋아요 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Comment comment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}