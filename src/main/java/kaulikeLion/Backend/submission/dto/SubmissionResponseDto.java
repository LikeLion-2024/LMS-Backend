package kaulikeLion.Backend.submission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

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
    public static class SimpleSubmissionDto {
        @Schema(description = "파일 id")
        private Long id;

        @Schema(description = "제출자")
        private String submitter;

        @Schema(description = "파일 삭제 유무")
        private Integer isDeleted;

        @Schema(description = "파일 url")
        private String submissionUrl;
    }

    @Schema(description = "SubmissionListResDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubmissionListResDto {

        @Schema(description = "파일 리스트")
        private List<SubmissionResponseDto.SimpleSubmissionDto> submissionList;
    }
}
