package kaulikeLion.Backend.oauth.service;


import jakarta.servlet.http.HttpServletRequest;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.oauth.domain.User;
import kaulikeLion.Backend.oauth.jwt.JwtTokenUtils;
import kaulikeLion.Backend.oauth.repository.RefreshTokenRedisRepository;
import kaulikeLion.Backend.oauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;

    // 로그인 - OAuth2SuccessHandler

    // 로그아웃
    public void logout(HttpServletRequest request) {
        // 1. 레디스에 해당 토큰 있는 지 확인
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        // 2. 리프레시 토큰을 username으로 찾아 삭제
        String username = jwtTokenUtils.parseClaims(accessToken).getSubject();
        log.info("access token에서 추출한 username : {}", username);
        if (refreshTokenRedisRepository.existsById(username)) {
            refreshTokenRedisRepository.deleteById(username);
            log.info("레디스에서 리프레시 토큰 삭제 완료");
        } else {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }
    }
    // 회원 탈퇴
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        if (refreshTokenRedisRepository.existsById(username)) {
            refreshTokenRedisRepository.deleteById(username);
            log.info("레디스에서 리프레시 토큰 삭제 완료");
        }
        userRepository.delete(user);
        log.info("{} 회원 탈퇴 완료", username);
    }

    /*
    // access, refresh 토큰 재발급
    public JwtDto reissue(HttpServletRequest request ) {
        // 1. 레디스에 해당 토큰 있는 지 확인
        RefreshToken refreshToken = refreshTokenRedisRepository
                .findByRefreshToken(request.getHeader("Authorization").split(" ")[1])
                .orElseThrow(() -> new GeneralException(ErrorCode.WRONG_REFRESH_TOKEN));

        // 2. 리프레시 토큰을 발급한 IP와 동일한 IP에서 온 요청인지 확인 (생략가능)
        if (!IpUtil.getClientIp(request).equals(refreshToken.getIp())) {
            throw new GeneralException(ErrorCode.IP_NOT_MATCHED);
        }

        // 3. 리프레시 토큰에서 username 찾기
        String username = refreshToken.getId();
        log.info("refresh token에서 추출한 username : {}", username);
        // 4. userdetails 불러오기
        UserDetails userDetails = manager.loadUserByUsername(username);

        log.info("reissue: refresh token 재발급 완료");
        JwtDto jwtDto = jwtTokenUtils.generateToken(userDetails);
        refreshToken.updateRefreshToken(jwtDto.getRefreshToken());
        // 유효기간 초단위 설정 후 redis에 timeToLive 설정
        Claims refreshTokenClaims = jwtTokenUtils.parseClaims(jwtDto.getRefreshToken());
        Long validPeriod
                = refreshTokenClaims.getExpiration().toInstant().getEpochSecond()
                - refreshTokenClaims.getIssuedAt().toInstant().getEpochSecond();
        refreshToken.updateTtl(validPeriod);
        refreshTokenRedisRepository.save(refreshToken);
        return jwtDto;
    }
*/
    public User findUserByUserName(String userName){
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
}
