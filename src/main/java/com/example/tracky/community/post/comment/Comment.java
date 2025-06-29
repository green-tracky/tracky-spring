package com.example.tracky.community.post.comment;

import com.example.tracky.community.post.Post;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "comment_tb")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    private Timestamp createdAt;

    // 좋아요 수는 계산된 값으로, 실제 DB 필드는 아님
    @Transient
    private Long likeCount;

    @Builder
    public Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}