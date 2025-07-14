package com.example.tracky.community.posts;

import com.example.tracky.community.posts.comments.Comment;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "post_tb")
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    // 공유된 러닝 기록 (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    private RunRecord runRecord;

    // 게시글 내용 (텍스트)
    @Column(length = 1000)
    private String content;

    // 댓글 리스트 (1:N)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // 생성일 자동 세팅
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Post(Integer id, User user, RunRecord runRecord, String content) {
        this.id = id;
        this.user = user;
        this.runRecord = runRecord;
        this.content = content;
    }

    public void update(PostRequest.UpdateDTO reqDTO) {
        this.content = Objects.requireNonNullElse(reqDTO.getContent(), this.content);
    }

    protected Post() {
    }

}