package kaulikeLion.Backend.assignment.controller;

import kaulikeLion.Backend.assignment.converter.AssignmentConverter;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Submission;
import kaulikeLion.Backend.global.api_payload.SuccessCode;
import kaulikeLion.Backend.assignment.service.AssignmentService;
import kaulikeLion.Backend.assignment.service.SubmissionService;
import kaulikeLion.Backend.global.api_payload.ApiResponse;
import kaulikeLion.Backend.oauth.domain.User;
import kaulikeLion.Backend.oauth.jwt.CustomUserDetails;
import kaulikeLion.Backend.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static kaulikeLion.Backend.assignment.dto.AssignmentRequestDto.*;
import static kaulikeLion.Backend.assignment.dto.AssignmentResponseDto.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/assignment")
public class AssignmentController {

    private final UserService userService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    // 과제 만들기
    @PostMapping("/create")
    public ApiResponse<SimpleAssignmentDto> create(
            @RequestBody AssignmentReqDto assignmentReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        Assignment assignment = assignmentService.createAssignment(assignmentReqDto, user);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_CREATED, AssignmentConverter.simpleAssignmentDto(assignment));
    }

    // 과제 목록 조회 - 누구나 접근 가능
    @GetMapping("/list")
    public ApiResponse<AssignmentListResDto> list(
            @RequestParam(name = "page") Integer page
    ){
        List<Assignment> assignmentsByUser = assignmentService.findAll(); // 모든 과제가 모든 인원에게 할당됨
        Page<Assignment> assignments = assignmentService.convertAssignmentToPage(assignmentsByUser, page, 5);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_LIST_VIEW_SUCCESS, AssignmentConverter.assignmentListResDto(assignments));
    }

    // 과제 상세 조회
    @GetMapping("/{id}")
    public ApiResponse<DetailAssignmentDto> detail(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        // 해당 게시글의 조회수 하나 올리기
        Assignment assignment = assignmentService.increaseViewCount(assignmentService.findById(id));

        // 과제 제출 목록 가져오기
        List<Submission> submissions = submissionService.findAllByAssignmentId(id);

        // 과제 파일 제출 목록 가져오기

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_DETAIL_VIEW_SUCCESS, AssignmentConverter.detailAssignmentDto(assignment, submissions));
    }

    // 과제 수정
    @PostMapping("/update/{id}")
    public ApiResponse<DetailAssignmentDto> update(
            @PathVariable(name = "id") Long id,
            @RequestBody DetailAssignmentReqDto detailAssignmentReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        Assignment assignment = assignmentService.updateAssignment(id, detailAssignmentReqDto);
        List<Submission> submissions = submissionService.findAllByAssignmentId(id);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_UPDATED, AssignmentConverter.detailAssignmentDto(assignment, submissions));
    }

    // 과제 삭제
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Integer> delete(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        assignmentService.deleteAssignment(id);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_DELETED, 1);
    }
}
