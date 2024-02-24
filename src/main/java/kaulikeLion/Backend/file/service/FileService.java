package kaulikeLion.Backend.file.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.assignment.domain.ViewCount;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.file.converter.FileConverter;
import kaulikeLion.Backend.file.domain.File;
import kaulikeLion.Backend.file.repository.FileRepository;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService { // S3 연동 - 업로드, 삭제, 다운로드

    private final AmazonS3 amazonS3;
    private final FileRepository fileRepository;
    private final AssignmentRepository assignmentRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<File> findAllByAssignmentId(Long id){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));

        return fileRepository.findAllByAssignmentOrderByIdAsc(assignment);
    }

    public List<String> upload(MultipartFile[] multipleFile, String dirName, Long assignmentId, User user) throws IOException { // 객체 업로드
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));

        List<String> listUrl = new ArrayList<>();
        for (MultipartFile mf : multipleFile) {

            // 파일의 확장자 추출
            String contentType = mf.getContentType();
            if (ObjectUtils.isEmpty(contentType)) { // 확장자명이 존재하지 않을 경우 취소 처리
                throw GeneralException.of(ErrorCode.INVALID_FILE_CONTENT_TYPE);
            }

            // 파일 리스트 하나씩 업로드
            java.io.File uploadFile = convert(mf)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

            // 날짜, 시각 추가
            String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("/MM-dd-HH-mm"));
            String fileName = dirName + formatDate + uploadFile.getName();

            // put - S3로 업로드
            String uploadFileUrl = putS3(uploadFile, fileName);

            // 로컬 파일 삭제
            // removeFile(uploadFile);

            // db에 file 저장
            fileRepository.save(FileConverter.saveFile(uploadFileUrl, assignment, user));

            listUrl.add(uploadFileUrl);
        }
        return listUrl;
    }

    private Optional<java.io.File> convert(MultipartFile file) throws IOException { // 파일화
        java.io.File convertFile = new java.io.File(Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(convertFile);
        return Optional.of(convertFile);
    }

    private String putS3(java.io.File uploadFile, String fileName) { // S3로 업로드
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeFile(java.io.File targetFile) { // 로컬파일 삭제
        if (targetFile.exists()) {
            if (targetFile.delete()) {
                log.info("파일이 삭제되었습니다.");
            } else {
                log.info("파일이 삭제되지 못했습니다.");
            }
        }
    }

    public void delete(String filePath, User user) { // db에서는 일부로 삭제 안함
        try {
            // filePath -> URL
            String fileUrl = "https://liklion-lms.s3.ap-northeast-2.amazonaws.com/" + filePath;
            log.info("fileUrl: " + fileUrl);
            // URL로 파일 찾음
            File file = fileRepository.findByFileUrl(fileUrl);

            if(file != null) { // db에 파일이 존재하고
                // 작성자와 삭제하려는 자가 동일인이어야 삭제 가능
                log.info("Submitter: " + file.getSubmitter());
                log.info("Nickname: " + user.getNickname());
                if (Objects.equals(file.getSubmitter(), user.getNickname())) {
                    // S3에서 삭제 - repository와 상관없이 실행됨
                    amazonS3.deleteObject(new DeleteObjectRequest(bucket, filePath));
                    // isDeleted = 1로 변경. 동시에 삭제 시각 updated_at에 찍힘
                    file.setIsDeleted(1);
                    fileRepository.save(file);
                } else throw new GeneralException(ErrorCode.BAD_REQUEST);
            } else throw new FileNotFoundException("File not found with URL: " + fileUrl);

        } catch (SdkClientException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<byte[]> download(String fileUrl) throws IOException { // 객체 다운  fileUrl : 폴더명/파일네임.파일확장자
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, fileUrl));
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType(fileUrl));
        httpHeaders.setContentLength(bytes.length);
        String[] arr = fileUrl.split("/");
        String type = arr[arr.length - 1];
        String fileName = URLEncoder.encode(type, "UTF-8").replaceAll("\\+", "%20");
        httpHeaders.setContentDispositionFormData("attachment", fileName); // 파일이름 지정

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    private MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length - 1];
        switch (type) {
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}