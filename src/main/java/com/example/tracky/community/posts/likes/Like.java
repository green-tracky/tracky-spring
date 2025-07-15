package com.example.tracky.community.posts.likes;

import com.example.tracky.community.posts.Post;
import com.example.tracky.community.posts.comments.Comment;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "like_tb")
@Getter
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    // 좋아요 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Like(Integer id, Post post, User user, Comment comment) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.comment = comment;
    }

    protected Like() {
    }
}