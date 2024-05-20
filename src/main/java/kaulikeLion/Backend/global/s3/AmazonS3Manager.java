package kaulikeLion.Backend.global.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Optional<File> convert(MultipartFile file) throws IOException { // 파일로 변환 - 필요해..?
        File convertedFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + file.getOriginalFilename());
        file.transferTo(convertedFile);
        return Optional.of(convertedFile);
    }

    public String putS3(File uploadFile, String fileName) { // S3로 업로드
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public String generateFileName(MultipartFile file) { // 파일명 생성
        return UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
    }

    public void delete(String filePath) { // 잘 작동 안됨
        try {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, filePath);
            log.info(String.valueOf(deleteObjectRequest));
            amazonS3.deleteObject(deleteObjectRequest);
            log.info("Deleted object from S3 with key: {}", filePath);
        } catch (SdkClientException e) {
            log.error("Error occurred while deleting object from S3", e);
            throw new RuntimeException("Failed to delete object from S3", e);
        }
    }

    public void removeFile(java.io.File targetFile) { // 로컬파일 삭제
        if (targetFile.exists()) {
            if (targetFile.delete()) {
                log.info("파일이 삭제되었습니다.");
            } else {
                log.info("파일이 삭제되지 못했습니다.");
            }
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

    public MediaType contentType(String fileName) {
        String[] arr = fileName.split("\\.");
        String type = arr[arr.length - 1];
        return switch (type) {
            case "txt" -> MediaType.TEXT_PLAIN;
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg" -> MediaType.IMAGE_JPEG;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
