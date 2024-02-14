//package kaulikeLion.Backend.file.controller;
//
//import kaulikeLion.Backend.assignment.dto.AssignmentDto;
//import kaulikeLion.Backend.global.api_payload.SuccessCode;
//import kaulikeLion.Backend.assignment.domain.Assignment;
//import kaulikeLion.Backend.assignment.service.AssignmentService;
//import kaulikeLion.Backend.file.service.FileUploadService;
//import kaulikeLion.Backend.global.api_payload.ApiResponse;
//import kaulikeLion.Backend.oauth.domain.User;
//import kaulikeLion.Backend.oauth.jwt.CustomUserDetails;
//import kaulikeLion.Backend.oauth.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/file")
//public class FileController {
//
//    private final FileUploadService fileUploadService;
//    private final AssignmentService assignmentService;
//    private final UserService userService;
//
//    // 파일 업로드
//    @PostMapping("/upload")
//    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file,
//                                          @RequestParam("assignmentId") Long assignmentId,
//                                          @AuthenticationPrincipal CustomUserDetails customUserDetails
//    ) throws IOException {
//        User user = userService.findUserByUserName(customUserDetails.getUsername());
//        AssignmentDto assignmentDto = assignmentService.findById(assignmentId);
//
//        // 파일 업로드 서비스 호출
//        fileUploadService.uploadFile(file, user, assignment);
//
//        return ApiResponse.onSuccess(SuccessCode.FILE_UPLOAD_SUCCESS, "uploaded");
//    }
//}