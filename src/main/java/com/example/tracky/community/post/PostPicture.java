package com.example.tracky.community.post;

import com.example.tracky.runrecord.picture.Picture;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Table(name = "post_picture_tb")
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

    protected PostPicture() {
    }

}
