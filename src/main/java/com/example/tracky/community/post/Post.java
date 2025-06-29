package com.example.tracky.community.post;

import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.User;
import com.example.tracky.community.post.comment.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "post_tb")
@Getter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 공유된 러닝 기록 (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_record_id")
    private RunRecord runRecord;

    // 게시글 내용 (텍스트)
    @Lob
    @Column(nullable = false)
    private String content;

    // 좋아요 수 (DB에 컬럼 없음, 직접 세팅 필요)
    @Transient
    private int likeCount;

    // 댓글 리스트 (1:N)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    // 생성일 자동 세팅
    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Post(User user, RunRecord runRecord, String content) {
        this.user = user;
        this.runRecord = runRecord;
        this.content = content;
    }

    // 좋아요 수 직접 세팅용 setter (Lombok 없으면 직접 만듦)
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}