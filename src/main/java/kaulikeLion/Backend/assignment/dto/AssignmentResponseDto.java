package kaulikeLion.Backend.assignment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class AssignmentResponseDto {
    @Schema(description = "SimpleAssignmentDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleAssignmentDto{
        @Schema(description = "과제 id")
        private Long id;

        @Schema(description = "과제 작성자")
        private String assignmentWriter;

        @Schema(description = "과제 수정 비밀번호")
        private String assignmentPass;

        @Schema(description = "과제 제목")
        private String assignmentTitle;

        @Schema(description = "과제 내용")
        private String assignmentContents;

        @Schema(description = "과제 조회수")
        private Long assignmentHits;

        @Schema(description = "과제 마감 기한")
        private LocalDateTime dueDateTime;

        @Schema(description = "과제 생성 시각")
        private LocalDateTime assignmentCreatedAt;

        @Schema(description = "첨부 사진")
        private String assignmentImage;
    }

    @Schema(description = "AssignmentResponseDto")
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssignmentListResDto {
        @Schema(description = "마지막 페이지가 맞으면 참, 아니면 거짓")
        private Boolean isLast;

        @Schema(description = "첫번째 페이지가 맞으면 참, 아니면 거짓")
        private Boolean isFirst;

        @Schema(description = "총 페이지 수")
        private Integer totalPage;

        @Schema(description = "총 Assignment 수")
        private Long totalElements;

        @Schema(description = "AssignmentList의 크기")
        private Integer listSize;

        @Schema(description = "과제 리스트, 한 번 요청에 5개씩 전달함")
        private List<SimpleAssignmentDto> assignmentList;

    }

}
