package com.example.tracky.community.posts;

import com.example.tracky.runrecord.pictures.Picture;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "post_picture_tb")
public class PostPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Picture picture;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public PostPicture(Integer id, Post post, Picture picture) {
        this.id = id;
        this.post = post;
        this.picture = picture;
    }

    protected PostPicture() {
    }

}
