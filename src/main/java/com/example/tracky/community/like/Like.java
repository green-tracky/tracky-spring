package com.example.tracky.community.like;

import com.example.tracky.community.post.Post;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.sql.Timestamp;

@Entity
@Table(name = "like_tb")
@Getter
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 좋아요 대상 게시글 (null 불가)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // 좋아요 누른 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Timestamp createdAt;

    @Builder
    public Like(Post post, User user) {
        this.post = post;
        this.user = user;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
}