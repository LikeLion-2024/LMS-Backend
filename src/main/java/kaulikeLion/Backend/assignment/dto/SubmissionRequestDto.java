package kaulikeLion.Backend.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class SubmissionRequestDto {
    @Schema(description = "SubmissionDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmissionDto {
        @Schema(description = "글이 작성된 과제 id")
        private Long assignmentId;

        @Schema(description = "글 내용")
        private String submissionContents;
    }
}