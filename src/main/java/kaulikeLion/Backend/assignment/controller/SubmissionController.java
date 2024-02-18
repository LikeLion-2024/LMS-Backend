package kaulikeLion.Backend.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.assignment.dto.SubmissionResponseDto.SubmissionListResDto;
import kaulikeLion.Backend.assignment.dto.SubmissionRequestDto.SubmissionDto;
import kaulikeLion.Backend.assignment.converter.SubmissionConverter;
import kaulikeLion.Backend.assignment.service.SubmissionService;
import kaulikeLion.Backend.global.api_payload.ApiResponse;
import kaulikeLion.Backend.global.api_payload.SuccessCode;
import kaulikeLion.Backend.oauth.domain.User;
import kaulikeLion.Backend.oauth.service.UserService;
import kaulikeLion.Backend.oauth.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "글", description = "글 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/submission")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final UserService userService;

    // 글 만들기
    @Operation(summary = "글 작성 메서드", description = "글을 작성하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBMISSION_2011", description = "글 생성이 완료되었습니다.")
    })
    @PostMapping("/create")
    public ApiResponse<SubmissionListResDto> create(
            @RequestBody SubmissionDto submissionDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        Submission submission = submissionService.createSubmission(submissionDto, user);
        List<Submission> submissions = submissionService.findAllByAssignmentId(submission.getAssignment().getId());

        return ApiResponse.onSuccess(SuccessCode.SUBMISSION_CREATED,SubmissionConverter.submissionListResDto(submissions));
    }

    // 글 삭제
    @Operation(summary = "글 삭제 메서드", description = "글을 삭제하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBMISSION_2001", description = "글 삭제가 완료되었습니다.")
    })
    @DeleteMapping("/delete/{id}")
    public ApiResponse<SubmissionListResDto> delete(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        Long assignmentId = submissionService.deleteSubmission(id);
        List<Submission> submissions = submissionService.findAllByAssignmentId(assignmentId);

        return ApiResponse.onSuccess(SuccessCode.SUBMISSION_DELETED,SubmissionConverter.submissionListResDto(submissions));
    }

}
