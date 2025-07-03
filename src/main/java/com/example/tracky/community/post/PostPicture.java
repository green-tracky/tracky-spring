package com.example.tracky.community.post;

import com.example.tracky.runrecord.picture.Picture;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
public class PostPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Picture picture;

    @Builder
    public PostPicture(Integer id, Post post, Picture picture) {
        this.id = id;
        this.post = post;
        this.picture = picture;
    }

    private PostPicture() {
    }
}
