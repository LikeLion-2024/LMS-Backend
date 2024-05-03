package kaulikeLion.Backend.submission.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.repository.AssignmentRepository;
import kaulikeLion.Backend.submission.converter.SubmissionConverter;
import kaulikeLion.Backend.submission.domain.Submission;
import kaulikeLion.Backend.submission.repository.SubmissionRepository;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.global.s3.AmazonS3Manager;
import kaulikeLion.Backend.oauth.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService { // S3 연동 - 업로드, 삭제, 다운로드

    private final AmazonS3 amazonS3;
    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final AmazonS3Manager amazonS3Manager;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<Submission> findAllByAssignmentId(Long id){
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> GeneralException.of(ErrorCode.ASSIGNMENT_NOT_FOUND));

        return submissionRepository.findAllByAssignmentOrderByIdAsc(assignment);
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
            java.io.File uploadFile = amazonS3Manager.convert(mf)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

            // 날짜, 시각 추가
            String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("/MM-dd-HH-mm"));
            String fileName = dirName + formatDate + uploadFile.getName();

            // put - S3로 업로드
            String uploadFileUrl = amazonS3Manager.putS3(uploadFile, fileName);

            // db에 file 저장
            submissionRepository.save(SubmissionConverter.saveSubmission(uploadFileUrl, assignment, user));

            listUrl.add(uploadFileUrl);
        }
        return listUrl;
    }

    public void delete(String filePath, User user) { // db에서는 일부로 삭제 안함
        try {
            // filePath -> URL
            String fileUrl = "https://liklion-lms.s3.ap-northeast-2.amazonaws.com/" + filePath;
            log.info("fileUrl: " + fileUrl);
            // URL로 파일 찾음
            Submission file = submissionRepository.findBySubmissionUrl(fileUrl);

            if(file != null) { // db에 파일이 존재하고
                // 작성자와 삭제하려는 자가 동일인이어야 삭제 가능
                if (Objects.equals(file.getSubmitter(), user.getNickname())) {
                    // S3에서 삭제 - repository와 상관없이 실행됨
                    amazonS3.deleteObject(new DeleteObjectRequest(bucket, filePath));
                    // isDeleted = 1로 변경. 동시에 삭제 시각 updated_at에 찍힘
                    file.setIsDeleted(1);
                    submissionRepository.save(file);
                } else throw new GeneralException(ErrorCode.BAD_REQUEST);
            } else throw new FileNotFoundException("File not found with URL: " + fileUrl);

        } catch (SdkClientException | FileNotFoundException e) {
            log.error("Error occurred while deleting object from S3", e);
            throw new RuntimeException("Failed to delete object from S3", e);
        }
    }
}