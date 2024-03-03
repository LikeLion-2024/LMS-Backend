package kaulikeLion.Backend.oauth.service;


import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import kaulikeLion.Backend.global.api_payload.ErrorCode;
import kaulikeLion.Backend.global.exception.GeneralException;
import kaulikeLion.Backend.oauth.domain.User;
import kaulikeLion.Backend.oauth.dto.MypageRequestDto;
import kaulikeLion.Backend.oauth.jwt.JwtDto;
import kaulikeLion.Backend.oauth.jwt.JwtTokenUtils;
import kaulikeLion.Backend.oauth.jwt.RefreshToken;
//import kaulikeLion.Backend.oauth.repository.RefreshTokenRedisRepository;
import kaulikeLion.Backend.oauth.repository.RefreshTokenRepository;
import kaulikeLion.Backend.oauth.repository.UserRepository;
import kaulikeLion.Backend.oauth.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    //private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsManager manager;
    private final JwtTokenUtils jwtTokenUtils;

    // 로그인 - OAuth2SuccessHandler

    // 로그아웃
    public void logout(HttpServletRequest request) {
        // 1. access token 찾아오기
        String accessToken = request.getHeader("Authorization").split(" ")[1];

        // 2. 리프레시 토큰을 username으로 찾아 삭제
        String username = jwtTokenUtils.parseClaims(accessToken).getSubject();
        log.info("access token에서 추출한 username : {}", username);
        if (refreshTokenRepository.existsById(username)) {
            refreshTokenRepository.deleteById(username);
            log.info("DB에서 리프레시 토큰 삭제 완료");
        } else {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }
    }

    // access, refresh 토큰 재발급
    public JwtDto reissue(HttpServletRequest request ) {
        // 1. Request에서 Refresh Token 추출
        String refreshTokenValue = request.getHeader("Authorization").split(" ")[1];

        // 2. DB에서 해당 Refresh Token을 찾음
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenValue)
                .orElseThrow(() -> new GeneralException(ErrorCode.WRONG_REFRESH_TOKEN));
        log.info("찾은 refresh token : {}", refreshToken);

        // 3. Refresh Token의 유효기간 확인 (생략)

        // 4. Refresh Token을 발급한 사용자 정보 로드
        UserDetails userDetails = manager.loadUserByUsername(refreshToken.getId());
        log.info("refresh token에서 추출한 username : {}", refreshToken.getId());

        // 5. 새로운 Access Token 및 Refresh Token 생성
        JwtDto jwt = jwtTokenUtils.generateToken(userDetails);
        log.info("reissue: refresh token 재발급 완료");

        // 6. Refresh Token 정보 업데이트 및 DB에 저장
        refreshToken.updateRefreshToken(jwt.getRefreshToken());
        Claims refreshTokenClaims = jwtTokenUtils.parseClaims(jwt.getRefreshToken());
        Long validPeriod = refreshTokenClaims.getExpiration().toInstant().getEpochSecond()
                - refreshTokenClaims.getIssuedAt().toInstant().getEpochSecond();
        refreshToken.updateTtl(validPeriod);
        refreshTokenRepository.save(refreshToken);
        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());

        // 7. DB에 새로운 리프레시 토큰이 정상적으로 저장되었는지 확인
        if (!refreshTokenRepository.existsById(refreshToken.getId())) {
            throw GeneralException.of(ErrorCode.WRONG_REFRESH_TOKEN);
        }

        return jwt;
    }

    // 회원 탈퇴
    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
        if (refreshTokenRepository.existsById(username)) {
            refreshTokenRepository.deleteById(username);
            log.info("DB에서 리프레시 토큰 삭제 완료");
        }
        userRepository.delete(user);
        log.info("{} 회원 탈퇴 완료", username);
    }

    // username으로 User찾기
    public User findUserByUserName(String userName){
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public User updateUser(MypageRequestDto.SimpleMypageReqDto simpleMypageReqDto, User user) {
        user.setGroupname(simpleMypageReqDto.getGroupname());
        user.setNickname(simpleMypageReqDto.getNickname());
        return userRepository.save(user);
    }
}
/* redis 사용 코드
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

    public User findUserByUserName(String userName){
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public User updateUser(MypageRequestDto.SimpleMypageReqDto simpleMypageReqDto, User user) {
        user.setGroupname(simpleMypageReqDto.getGroupname());
        user.setNickname(simpleMypageReqDto.getNickname());
        return userRepository.save(user);
    }
 */
