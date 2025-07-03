package com.example.tracky.community.post;

import com.example.tracky.runrecord.picture.Picture;
import jakarta.persistence.*;

@Entity
public class PostPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Picture picture;
}
