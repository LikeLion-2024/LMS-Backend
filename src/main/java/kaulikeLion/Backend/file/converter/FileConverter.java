package kaulikeLion.Backend.file.converter;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.file.dto.FileResponseDto;
import kaulikeLion.Backend.file.domain.File;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
public class FileConverter {
    public static File toFile(String uploadFileUrl, Assignment assignment) {
        return File.builder()
                .fileUrl(uploadFileUrl)
                .isDeleted(0)
                .assignment(assignment)
                .build();
    }

    public static FileResponseDto.SimpleFileDto simpleFileDto(File file) {
        return FileResponseDto.SimpleFileDto.builder()
                .id(file.getId())
                .isDeleted(file.getIsDeleted())
                .fileUrl(file.getFileUrl())
                .build();
    }

    public static FileResponseDto.FileListResDto fileListResDto(List<File> files){
        List<FileResponseDto.SimpleFileDto> fileDtos
                = files.stream().map(FileConverter::simpleFileDto).toList();

        return FileResponseDto.FileListResDto.builder()
                .fileList(fileDtos)
                .build();
    }
}
