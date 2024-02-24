package kaulikeLion.Backend.file.converter;

import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.file.dto.FileResponseDto;
import kaulikeLion.Backend.file.domain.File;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
public class FileConverter {
    public static File saveFile(String uploadFileUrl, Assignment assignment, User user) {
        return File.builder()
                .submitter(user.getNickname())
                .isDeleted(0)
                .fileUrl(uploadFileUrl)
                .user(user)
                .assignment(assignment)
                .build();
    }

    public static FileResponseDto.SimpleFileDto simpleFileDto(File file) {
        return FileResponseDto.SimpleFileDto.builder()
                .id(file.getId())
                .submitter(file.getSubmitter())
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
