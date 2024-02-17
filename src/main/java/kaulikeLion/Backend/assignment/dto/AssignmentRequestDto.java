package kaulikeLion.Backend.assignment.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor // 기본 생성자
public class AssignmentRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssignmentReqDto {
        private String assignmentWriter;
        private String assignmentPass;
        private String assignmentTitle;
        private String assignmentContents;
        private Long assignmentHits;
        private LocalDateTime dueDateTime;

        private LocalDateTime assignmentCreatedAt;

        private int photoAttached; // 사진 첨부 여부(첨부 1, 미첨부 0)
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailAssignmentReqDto {
        private Long id;
        private String assignmentWriter;
        private String assignmentPass;
        private String assignmentTitle;
        private String assignmentContents;
        private LocalDateTime dueDateTime;

        private LocalDateTime assignmentUpdatedAt;

        private int photoAttached; // 사진 첨부 여부(첨부 1, 미첨부 0)
    }

}
