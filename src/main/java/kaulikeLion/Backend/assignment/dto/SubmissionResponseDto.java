package kaulikeLion.Backend.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class SubmissionResponseDto {
    @Schema(description = "SimpleSubmissionDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleSubmissionDto{
        @Schema(description = "글 id")
        private Long id;

        @Schema(description = "글 작성자")
        private String submissionWriter;

        @Schema(description = "글 내용")
        private String submissionContents;

        @Schema(description = "글 생성 시각")
        private LocalDateTime submissionCreatedAt;

    }

    @Schema(description = "SubmissionListResDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmissionListResDto {

        @Schema(description = "글 리스트")
        private List<SimpleSubmissionDto> submissionList;

    }
}
