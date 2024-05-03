package kaulikeLion.Backend.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class CommentResponseDto {
    @Schema(description = "SimpleCommentDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleCommentDto{
        @Schema(description = "글 id")
        private Long id;

        @Schema(description = "글 작성자")
        private String commentWriter;

        @Schema(description = "글 내용")
        private String commentContents;

        @Schema(description = "글 생성 시각")
        private LocalDateTime commentCreatedAt;

    }

    @Schema(description = "CommentListResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentListResDto {

        @Schema(description = "글 리스트")
        private List<SimpleCommentDto> commentList;

    }
}
