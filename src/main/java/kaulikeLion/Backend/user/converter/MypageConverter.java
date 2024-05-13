package kaulikeLion.Backend.user.converter;

import kaulikeLion.Backend.user.domain.User;
import kaulikeLion.Backend.user.dto.MypageResponseDto;

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
