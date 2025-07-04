package com.example.tracky.community.posts;

import com.example.tracky.community.posts.comments.Comment;
import com.example.tracky.runrecord.RunRecord;
import com.example.tracky.runrecord.pictures.Picture;
import com.example.tracky.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post_tb")
@Getter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PostPicture> postPictures = new ArrayList<>();

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

    public void update(String content, RunRecord runRecord, List<Picture> pictures) {
        this.content = content;
        this.runRecord = runRecord;

        // 기존 postPictures 비우기
        this.postPictures.clear();

        // 새로 PostPicture 생성해서 넣기
        if (pictures != null && !pictures.isEmpty()) {
            for (Picture picture : pictures) {
                PostPicture postPicture = PostPicture.builder()
                        .post(this)
                        .picture(picture)
                        .build();
                this.postPictures.add(postPicture);
            }
        }
    }

}