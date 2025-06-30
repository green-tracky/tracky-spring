package com.example.tracky.community.post;

import com.example.tracky.community.post.comment.CommentResponse;
import com.example.tracky.runrecord.RunRecordResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    @Getter
    public static class DTO {

        private final Integer id;
        private final String title;
        private final String content;
        private final Integer userId;
        private final RunRecordResponse.DetailDTO runRecord;
        private final List<CommentResponse.CommentResponseDTO> commentDTOs;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public DTO(Integer id, String title, String content, Integer userId,
                   RunRecordResponse.DetailDTO runRecord,
                   List<CommentResponse.CommentResponseDTO> commentDTOs,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.userId = userId;
            this.runRecord = runRecord;
            this.commentDTOs = commentDTOs;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        // ✅ static 메서드로 변경
        public static List<DTO> toPostResponseDTOs(List<Post> posts) {
            return posts.stream().map(post -> {
                RunRecordResponse.DetailDTO runRecordDTO = null;
                if (post.getRunRecord() != null) {
                    runRecordDTO = new RunRecordResponse.DetailDTO(post.getRunRecord());
                }

                List<CommentResponse.CommentResponseDTO> commentDTOs = post.getComments().stream()
                        .map(comment -> CommentResponse.CommentResponseDTO.fromEntity(comment))
                        .toList();

                return new DTO(
                        post.getId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getUser().getId(),
                        runRecordDTO,
                        commentDTOs,
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                );
            }).toList();
        }
    }
}
