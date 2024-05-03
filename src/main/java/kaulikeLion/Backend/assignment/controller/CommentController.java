package kaulikeLion.Backend.assignment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kaulikeLion.Backend.assignment.domain.Comment;
import kaulikeLion.Backend.assignment.dto.CommentRequestDto;
import kaulikeLion.Backend.assignment.converter.CommentConverter;
import kaulikeLion.Backend.assignment.dto.CommentResponseDto;
import kaulikeLion.Backend.assignment.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @Operation(summary = "글 작성 메서드", description = "글을 작성하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SUBMISSION_2011", description = "글 생성이 완료되었습니다.")
    })
    @PostMapping("/create")
    public ApiResponse<Long> create(
            @RequestBody CommentRequestDto.CommentDto commentDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        // writer가 username으로 들어감
        Comment comment = commentService.createComment(commentDto, user);

        return ApiResponse.onSuccess(SuccessCode.COMMENT_CREATED, comment.getId());
    }

    @Operation(summary = "글 삭제 메서드", description = "글을 삭제하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT_2001", description = "글 삭제가 완료되었습니다.")
    })
    @DeleteMapping("/delete/{id}")
    public ApiResponse<CommentResponseDto.CommentListResDto> delete(
            @PathVariable(name = "id") Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        // 작성자만 삭제할 수 있음
        Long assignmentId = commentService.deleteComment(id, user);
        List<Comment> comments = commentService.findAllByAssignmentId(assignmentId);

        return ApiResponse.onSuccess(SuccessCode.COMMENT_DELETED, CommentConverter.commentListResDto(comments));
    }
}
