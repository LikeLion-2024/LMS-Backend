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
    USER_CREATED(HttpStatus.CREATED, "USER_2011", "회원가입 & 로그인이 완료되었습니다."),
    USER_LOGOUT_SUCCESS(HttpStatus.OK, "USER_2001", "로그아웃 되었습니다."),
    USER_REISSUE_SUCCESS(HttpStatus.OK, "USER_2002", "토큰 재발급이 완료되었습니다."),
    USER_DELETE_SUCCESS(HttpStatus.OK, "USER_2003", "회원탈퇴가 완료되었습니다."),

    // Mypage
    MYPAGE_INFO_VIEW_SUCCESS(HttpStatus.OK, "MYPAGE_2001", "나의 정보 조회가 완료되었습니다."),
    MYPAGE_INFO_UPDATED(HttpStatus.OK, "MYPAGE_2002", "나의 정보 수정이 완료되었습니다."),

    // Assignment
    ASSIGNMENT_CREATED(HttpStatus.CREATED, "ASSIGNMENT_2011", "과제 생성이 완료되었습니다."),
    ASSIGNMENT_LIST_VIEW_SUCCESS(HttpStatus.OK, "ASSIGNMENT_2001", "과제 목록 조회가 완료되었습니다."),
    ASSIGNMENT_DETAIL_VIEW_SUCCESS(HttpStatus.OK, "ASSIGNMENT_2002", "과제 상세 조회가 완료되었습니다."),
    ASSIGNMENT_UPDATED(HttpStatus.OK, "ASSIGNMENT_2003", "글 수정이 완료되었습니다."),
    ASSIGNMENT_DELETED(HttpStatus.OK, "ASSIGNMENT_2004", "과제 삭제가 완료되었습니다."),

    // Comment
    COMMENT_CREATED(HttpStatus.CREATED, "SUBMISSION_2011", "글 생성이 완료되었습니다."),
    COMMENT_DELETED(HttpStatus.OK, "SUBMISSION_2001", "글 삭제가 완료되었습니다."),

    // Submission
    SUBMISSION_UPLOAD_SUCCESS(HttpStatus.OK, "SUBMISSION_2001", "파일 업로드가 완료되었습니다."),
    SUBMISSION_LIST_VIEW_SUCCESS(HttpStatus.OK, "SUBMISSION_2002", "파일 리스트 조회가 완료되었습니다."),
    SUBMISSION_DELETE_SUCCESS(HttpStatus.OK, "SUBMISSION_2003", "파일 삭제가 완료되었습니다."),
    SUBMISSION_DOWNLOAD_SUCCESS(HttpStatus.OK, "SUBMISSION_2004", "파일 다운로드가 완료되었습니다."),

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

