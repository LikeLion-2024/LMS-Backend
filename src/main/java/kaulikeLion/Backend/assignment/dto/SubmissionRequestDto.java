package kaulikeLion.Backend.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class SubmissionRequestDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubmissionDto {
        private Long assignmentId;
        private String submissionWriter;
        private String submissionContents;
        private LocalDateTime submissionCreatedAt;
    }
}