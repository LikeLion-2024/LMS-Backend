package kaulikeLion.Backend.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

public class AssignmentRequestDto {

    @Schema(description = "AssignmentReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssignmentReqDto {

        @Schema(description = "과제 비밀번호")
        private String assignmentPass;

        @Schema(description = "과제 제목")
        private String assignmentTitle;

        @Schema(description = "과제 내용")
        private String assignmentContents;

        @Schema(description = "과제 마감 기한")
        private LocalDateTime dueDateTime;
    }

    @Schema(description = "DetailAssignmentReqDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailAssignmentReqDto {
        @Schema(description = "과제 id")
        private Long id;

        @Schema(description = "과제 비밀번호")
        private String assignmentPass;

        @Schema(description = "과제 제목")
        private String assignmentTitle;

        @Schema(description = "과제 내용")
        private String assignmentContents;

        @Schema(description = "과제 마감 기한")
        private LocalDateTime dueDateTime;

        @Schema(description = "과제 사진 첨부 여부(첨부1, 미첨부0)")
        private int photoAttached;
    }

}
