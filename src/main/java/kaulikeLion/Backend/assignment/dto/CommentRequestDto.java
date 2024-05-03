package kaulikeLion.Backend.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class CommentRequestDto {
    @Schema(description = "CommentDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentDto {
        @Schema(description = "글이 작성된 과제 id")
        private Long assignmentId;

        @Schema(description = "글 내용")
        private String commentContents;
    }
}