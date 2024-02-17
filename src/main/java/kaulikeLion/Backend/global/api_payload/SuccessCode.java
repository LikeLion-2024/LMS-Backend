package kaulikeLion.Backend.global.api_payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode {
    // Common
    OK(HttpStatus.OK, "COMMON_200", "Success"),
    CREATED(HttpStatus.CREATED, "COMMON_201", "Created"),

    // User
    USER_CREATED(HttpStatus.CREATED, "USER_201", "회원가입이 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_202", "로그아웃 되었습니다."),
    USER_REISSUE_SUCCESS(HttpStatus.OK, "USER_203", "토큰 재발급이 완료되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "USER_204", "회원탈퇴가 완료되었습니다."),

    //USER_PASSWORD_CHANGE_SUCCESS(HttpStatus.OK, "USER_205", "비밀번호가 변경되었습니다."),

    // Assignment
    ASSIGNMENT_CREATED(HttpStatus.CREATED, "ASSIGNMENT_201", "과제 생성이 완료되었습니다."),
    ASSIGNMENT_LIST_VIEW_SUCCESS(HttpStatus.OK, "ASSIGNMENT_202", "과제 목록 조회가 완료되었습니다."),
    ASSIGNMENT_DETAIL_VIEW_SUCCESS(HttpStatus.OK, "ASSIGNMENT_203", "과제 상세 조회가 완료되었습니다."),
    ASSIGNMENT_UPDATED(HttpStatus.OK, "ASSIGNMENT_204", "글 수정이 완료되었습니다."),
    ASSIGNMENT_DELETED(HttpStatus.OK, "ASSIGNMENT_205", "과제 삭제가 완료되었습니다."),

    // Submission
    SUBMISSION_CREATED(HttpStatus.CREATED, "SUBMISSION_201", "글 생성이 완료되었습니다."),
    SUBMISSION_DELETED(HttpStatus.OK, "SUBMISSION_202", "글 삭제가 완료되었습니다."),

    // File
    FILE_UPLOAD_SUCCESS(HttpStatus.OK, "FILE_200", "파일 업로드 완료되었습니다."),
    FILE_DELETE_SUCCESS(HttpStatus.OK, "FILE_201", "파일 삭제 완료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .httpStatus(this.httpStatus)
                .isSuccess(true)
                .code(this.code)
                .message(this.message)
                .build();
    }
}

