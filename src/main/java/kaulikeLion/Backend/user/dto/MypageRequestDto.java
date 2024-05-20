package kaulikeLion.Backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MypageRequestDto {
    @Schema(description = "SimpleMypageReqDto")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleMypageReqDto{
        private String groupname;
        private String nickname;
    }
}
