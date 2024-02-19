package kaulikeLion.Backend.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "과제", description = "과제 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/assignment")
public class AssignmentController {

    private final UserService userService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    @Operation(summary = "과제 만들기 메서드", description = "과제를 만드는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2011", description = "과제 생성이 완료되었습니다.")
    })
    @PostMapping("/create")
    public ApiResponse<SimpleAssignmentDto> create(
            @RequestBody AssignmentReqDto assignmentReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        Assignment assignment = assignmentService.createAssignment(assignmentReqDto, user);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_CREATED, AssignmentConverter.simpleAssignmentDto(assignment));
    }

    @Operation(summary = "과제 목록 정보 조회 메서드", description = "과제 목록을 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2001", description = "과제 목록 조회가 완료되었습니다.")
    })
    @GetMapping("/list")
    public ApiResponse<AssignmentListResDto> list(
            @RequestParam(name = "page") Integer page
    ){
        List<Assignment> assignmentsByUser = assignmentService.findAll(); // 모든 과제가 모든 인원에게 할당됨
        Page<Assignment> assignments = assignmentService.convertAssignmentToPage(assignmentsByUser, page, 5);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_LIST_VIEW_SUCCESS, AssignmentConverter.assignmentListResDto(assignments));
    }

    @Operation(summary = "과제 상세 조회 메서드", description = "과제 상세 정보를 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2002", description = "과제 상세 조회가 완료되었습니다.")
    })
    @GetMapping("/{id}")
    public ApiResponse<DetailAssignmentDto> detail(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        // 해당 게시글의 조회수 +1
        Assignment assignment = assignmentService.increaseViewCount(assignmentService.findById(id));
        // 과제 제출 목록 가져오기
        List<Submission> submissions = submissionService.findAllByAssignmentId(id);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_DETAIL_VIEW_SUCCESS, AssignmentConverter.detailAssignmentDto(assignment, submissions));
    }

    @Operation(summary = "과제 수정 메서드", description = "과제 정보를 수정하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2003", description = "글 수정이 완료되었습니다.")
    })
    @PostMapping("/update/{id}")
    public ApiResponse<DetailAssignmentDto> update(
            @PathVariable(name = "id") Long id,
            @RequestBody DetailAssignmentReqDto detailAssignmentReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        Assignment assignment = assignmentService.updateAssignment(id, detailAssignmentReqDto);
        // 과제 제출 목록 가져오기
        List<Submission> submissions = submissionService.findAllByAssignmentId(id);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_UPDATED, AssignmentConverter.detailAssignmentDto(assignment, submissions));
    }

    @Operation(summary = "과제 삭제 메서드", description = "과제를 삭제하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2004", description = "과제 삭제가 완료되었습니다.")
    })
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Integer> delete(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        assignmentService.deleteAssignment(id);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_DELETED, 1);
    }
}
