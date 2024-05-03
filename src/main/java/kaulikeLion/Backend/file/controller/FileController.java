package kaulikeLion.Backend.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kaulikeLion.Backend.file.converter.FileConverter;
import kaulikeLion.Backend.file.dto.FileResponseDto;
import kaulikeLion.Backend.file.service.FileService;
import kaulikeLion.Backend.file.domain.File;
import kaulikeLion.Backend.global.api_payload.ApiResponse;
import kaulikeLion.Backend.global.api_payload.SuccessCode;
import kaulikeLion.Backend.global.s3.AmazonS3Manager;
import kaulikeLion.Backend.oauth.domain.User;
import kaulikeLion.Backend.oauth.jwt.CustomUserDetails;
import kaulikeLion.Backend.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Tag(name = "파일", description = "파일 관련 api 입니다.")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final UserService userService;
    private final AmazonS3Manager amazonS3Manager;


    @Operation(summary = "다중 파일 업로드 메서드", description = "파일 형태인 과제를 제출하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FILE_2001", description = "파일 업로드가 완료되었습니다.")
    })
    @PostMapping(value = "/upload", consumes = "multipart/*") // multipart/form-data
    public ApiResponse<FileResponseDto.FileListResDto> upload(
            @RequestParam("assignmentId") Long assignmentId,
            @RequestPart("files") MultipartFile[] files,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws IOException {
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        // 제출자 이름이 db에 저장됨
        fileService.upload(files, "submission", assignmentId, user); // 지정된 buket에 /submission라는 디렉터리로 files를 업로드
        List<File> fileList = fileService.findAllByAssignmentId(assignmentId);

        return ApiResponse.onSuccess(SuccessCode.FILE_UPLOAD_SUCCESS, FileConverter.fileListResDto(fileList));
    }

    @Operation(summary = "파일 목록 조회 메서드", description = "특정 과제에 제출된 파일들을 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FILE_2002", description = "파일 리스트 조회가 완료되었습니다.")
    })
    @GetMapping("/list")
    public ApiResponse<FileResponseDto.FileListResDto> list(
            @RequestParam("assignmentId") Long assignmentId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<File> fileList = fileService.findAllByAssignmentId(assignmentId);

        return ApiResponse.onSuccess(SuccessCode.FILE_LIST_VIEW_SUCCESS, FileConverter.fileListResDto(fileList));
    }

    @Operation(summary = "단일 파일 삭제 메서드", description = "단일 과제(파일)를 삭제하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FILE_2003", description = "파일 삭제가 완료되었습니다.")
    })
    @DeleteMapping("/delete")
    public ApiResponse<String> delete(
            @RequestParam("filePath") String filePath,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        // submitter 당사자만 삭제 가능
        fileService.delete(filePath, user);

        return ApiResponse.onSuccess(SuccessCode.FILE_DELETE_SUCCESS, "file deleted");
    }

    @Operation(summary = "단일 파일 다운로드 메서드", description = "단일 과제(파일)을 다운로드하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2011", description = "과제 생성이 완료되었습니다.")
    })
    @GetMapping(value = "/download")
    public ResponseEntity<byte[]> download(@RequestParam("fileUrl") String fileUrl,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) throws IOException {
        String filePath = fileUrl.substring(52);

        return amazonS3Manager.download(filePath); // 리턴 url == 다운로드 링크
    }
}