package kaulikeLion.Backend.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
public class FileResponseDto {
    @Schema(description = "SimpleFileDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleFileDto {
        @Schema(description = "파일 id")
        private Long id;

        @Schema(description = "파일 url")
        private String fileUrl;

        @Schema(description = "파일 삭제 유무")
        private Integer isDeleted;
    }

    @Schema(description = "FileListResDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileListResDto {

        @Schema(description = "파일 리스트")
        private List<FileResponseDto.SimpleFileDto> fileList;
    }
}
