package kaulikeLion.Backend.file;

import kaulikeLion.Backend.global.api_payload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final S3FileComponent fileComponent;

    // 버튼 하나로 멀티 파일 업로드 가능하도록
    @PostMapping(value = "/upload", consumes = "multipart/*") // multipart/form-data
    public List<String> upload(@RequestParam("parentId") String parentId,
                               @RequestParam("parentCode") String parentCode,
                               @RequestPart("files") MultipartFile[] files) throws IOException {
        return fileComponent.upload(files, parentCode + "/" + parentId); // 지정된 buket에 /submit/2024라는 디렉터리로 files를 업로드
    }

    // 객체 url을 전달받아 s3 파일 삭제 기능
    @DeleteMapping("/delete")
    public ApiResponse<?> delete(@RequestParam("filePath") String filePath) {
        return fileComponent.delete(filePath);
    }

    // 객체 url을 전달받아 s3 파일 다운로드 기능
    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> download(@RequestParam("fileUrl") String fileUrl) throws IOException {
        String filePath = fileUrl.substring(52);
        return fileComponent.download(filePath);
    }
}