package kaulikeLion.Backend.oauth.converter;

import kaulikeLion.Backend.oauth.domain.User;
import kaulikeLion.Backend.oauth.dto.MypageResponseDto;

public class MypageConverter {
    public static MypageResponseDto.MyInfoDto myInfoDto(User user) {
        return MypageResponseDto.MyInfoDto.builder()
                .id(user.getId())
                .groupname(user.getGroupname())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}
