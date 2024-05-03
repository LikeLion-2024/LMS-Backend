package kaulikeLion.Backend.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kaulikeLion.Backend.assignment.converter.AssignmentConverter;
import kaulikeLion.Backend.assignment.domain.Assignment;
import kaulikeLion.Backend.assignment.domain.Comment;
import kaulikeLion.Backend.global.api_payload.SuccessCode;
import kaulikeLion.Backend.assignment.service.AssignmentService;
import kaulikeLion.Backend.assignment.service.CommentService;
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
    private final CommentService commentService;

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
        // writer가 username으로 들어감
        Assignment assignment = assignmentService.createAssignment(assignmentReqDto, user);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_CREATED, AssignmentConverter.simpleAssignmentDto(assignment));
    }

    @Operation(summary = "과제 목록 정보 조회 메서드", description = "과제 목록을 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2001", description = "과제 목록 조회가 완료되었습니다.")
    })
    @GetMapping("/list") // 로그인 없이 열람 가능
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
    public ApiResponse<SimpleAssignmentDto> detail(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        // 해당 게시글의 조회수 +1
        Assignment assignment = assignmentService.increaseViewCount(assignmentService.findById(id));
        // 과제 제출 목록 가져오기
        List<Comment> comments = commentService.findAllByAssignmentId(id);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_DETAIL_VIEW_SUCCESS, AssignmentConverter.simpleAssignmentDto(assignment));
    }

    @Operation(summary = "과제 수정 메서드", description = "과제 정보를 수정하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2003", description = "글 수정이 완료되었습니다.")
    })
    @PostMapping("/update/{id}")
    public ApiResponse<SimpleAssignmentDto> update(
            @PathVariable(name = "id") Long id,
            @RequestBody DetailAssignmentReqDto detailAssignmentReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        // 비밀 번호 옳으면 수정 가능. 수정자가 writer로 바뀜
        // 비밀번호 틀리면 수정하려고 작성한 내용 초기화되고, 기존 내용 유지됨.
        Assignment assignment = assignmentService.updateAssignment(id, detailAssignmentReqDto, user);
        // 과제 제출 목록 가져오기
        List<Comment> comments = commentService.findAllByAssignmentId(id);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_UPDATED, AssignmentConverter.simpleAssignmentDto(assignment));
    }

    @Operation(summary = "과제 삭제 메서드", description = "과제를 삭제하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "ASSIGNMENT_2004", description = "과제 삭제가 완료되었습니다.")
    })
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> delete(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        // writer(작성자 or 수정자)인 사람만 삭제 가능
        assignmentService.deleteAssignment(id, user);

        return ApiResponse.onSuccess(SuccessCode.ASSIGNMENT_DELETED, "is deleted");
    }
}
