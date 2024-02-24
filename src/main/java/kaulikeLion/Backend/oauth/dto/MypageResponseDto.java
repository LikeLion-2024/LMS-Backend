package kaulikeLion.Backend.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MypageResponseDto {
    @Schema(description = "MyInfoDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyInfoDto{
        private Long id;
        private String email;
        private String groupname;
        private String nickname;
    }
}
