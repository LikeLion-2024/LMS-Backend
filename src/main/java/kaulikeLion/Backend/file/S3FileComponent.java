package kaulikeLion.Backend.file;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import kaulikeLion.Backend.global.api_payload.ApiResponse;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.api_payload.SuccessCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileComponent { // S3 연동 - 업로드, 삭제, 다운로드

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> upload(MultipartFile[] multipleFile, String dirName) throws IOException { // 객체 업로드
        List<String> listUrl = new ArrayList<>();
        for (MultipartFile mf : multipleFile) {
            String contentType = mf.getContentType(); // 파일의 확장자 추출
            if (ObjectUtils.isEmpty(contentType)) { // 확장자명이 존재하지 않을 경우 전체 취소 처리
                throw GeneralException.of(ErrorCode.INVALID_FILE_CONTENT_TYPE);
            }

            File uploadFile = convert(mf) // 파일 리스트 하나씩 업로드
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));

            // 날짜 추가
            String formatDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("/yyyy-MM-dd HH:mm"));
            String fileName = dirName + formatDate + uploadFile.getName();
            // put - S3로 업로드
            String uploadImageUrl = putS3(uploadFile, fileName);
            // 로컬 파일 삭제
            // removeFile(uploadFile);

            listUrl.add(uploadImageUrl);
        }
        return listUrl;
    }

    private Optional<File> convert(MultipartFile file) throws IOException { // 파일화
        File convertFile = new File(file.getOriginalFilename());
        file.transferTo(convertFile);
        return Optional.of(convertFile);
    }

    private String putS3(File uploadFile, String fileName) { // S3로 업로드
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeFile(File targetFile) { // 로컬파일 삭제
        if (targetFile.exists()) {
            if (targetFile.delete()) {
                log.info("파일이 삭제되었습니다.");
            } else {
                log.info("파일이 삭제되지 못했습니다.");
            }
        }
    }

    public ApiResponse<?> delete(String filePath) { // 객체 삭제  filePath : 폴더명/파일네임.파일확장자
        try {
            // S3에서 삭제
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, filePath));
            System.out.println(String.format("[%s] deletion complete", filePath));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return ApiResponse.onSuccess(SuccessCode.FILE_DELETE_SUCCESS, "delete file");
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