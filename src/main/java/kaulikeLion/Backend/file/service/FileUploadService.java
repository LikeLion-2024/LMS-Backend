//package kaulikeLion.Backend.file.service;
//
//import kaulikeLion.Backend.assignment.domain.Assignment;
//import kaulikeLion.Backend.file.domain.File;
//import kaulikeLion.Backend.file.repository.FileRepository;
//import kaulikeLion.Backend.oauth.domain.User;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class FileUploadService { // 파일 업로드 & 저장
//
//    // 파일이 저장될 디렉토리 경로
//    private final String uploadDir = "/path/to/upload/directory";
//
//    @Autowired
//    private FileRepository fileRepository;
//
//    public void uploadFile(MultipartFile multipartFile, User user, Assignment assignment) throws IOException {
//        // 업로드할 디렉토리를 생성합니다.
//        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
//        Files.createDirectories(uploadPath);
//
//        // 파일의 원래 이름을 가져옵니다.
//        String originalFileName = multipartFile.getOriginalFilename();
//
//        // 파일의 확장자를 추출합니다.
//        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
//
//        // 저장될 파일의 고유한 이름 생성
//        String storedFileName = UUID.randomUUID().toString() + fileExtension;
//
//        // 파일을 서버의 파일 시스템에 저장합니다.
//        Path filePath = uploadPath.resolve(storedFileName);
//        Files.copy(multipartFile.getInputStream(), filePath);
//
//        // 파일 정보를 데이터베이스에 저장합니다.
//        fileRepository.save(
//                File.builder()
//                        .originalFileName(originalFileName)
//                        .storedFileName(storedFileName)
//                        .fileType(multipartFile.getContentType())
//                        .uploadedDateTime(LocalDateTime.now())
//                        .user(user)
//                        .assignment(assignment)
//                        .build()
//        );
//    }
//}
