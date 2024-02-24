package kaulikeLion.Backend.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kaulikeLion.Backend.global.api_payload.ApiResponse;
import kaulikeLion.Backend.oauth.converter.MypageConverter;
import kaulikeLion.Backend.oauth.domain.User;
import kaulikeLion.Backend.oauth.dto.MypageRequestDto;
import kaulikeLion.Backend.oauth.dto.MypageResponseDto.MyInfoDto;
import kaulikeLion.Backend.oauth.jwt.CustomUserDetails;
import kaulikeLion.Backend.oauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import kaulikeLion.Backend.global.api_payload.SuccessCode;

@Tag(name = "마이페이지", description = "마이페이지 관련 api 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/me")
public class MypageController {

    private final UserService userService;

    @Operation(summary = "나의 정보 조회 메서드", description = "나의 정보를 조회하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MYPAGE_2001", description = "나의 정보 조회 성공")
    })
    @GetMapping("/info")
    public ApiResponse<MyInfoDto> findMyInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        return ApiResponse.onSuccess(SuccessCode.MYPAGE_INFO_VIEW_SUCCESS, MypageConverter.myInfoDto(user));
    }

    @Operation(summary = "내 정보 수정 메서드", description = "나의 정보를 수정하는 메서드입니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MYPAGE_2002", description = "내 정보 수정이 완료되었습니다.")
    })
    @PostMapping("/update")
    public ApiResponse<MyInfoDto> update(
            @RequestBody MypageRequestDto.SimpleMypageReqDto simpleMypageReqDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        User user = userService.findUserByUserName(customUserDetails.getUsername());
        User updated_user = userService.updateUser(simpleMypageReqDto, user);

        return ApiResponse.onSuccess(SuccessCode.MYPAGE_INFO_UPDATED, MypageConverter.myInfoDto(updated_user));
    }

}
