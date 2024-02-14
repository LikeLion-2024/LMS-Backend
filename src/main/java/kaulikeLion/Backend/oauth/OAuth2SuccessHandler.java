package kaulikeLion.Backend.oauth;

import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kaulikeLion.Backend.oauth.jwt.CustomUserDetails;
import kaulikeLion.Backend.oauth.jwt.JwtDto;
import kaulikeLion.Backend.oauth.jwt.JwtTokenUtils;
import kaulikeLion.Backend.oauth.jwt.RefreshToken;
import kaulikeLion.Backend.oauth.repository.RefreshTokenRedisRepository;
import kaulikeLion.Backend.oauth.utils.IpUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
// OAuth2 통신이 성공적으로 끝났을 때 사용됨
// ID Provider에게 받은 정보를 바탕으로 JWT를 발급
// + 클라이언트가 저장할 수 있도록 특정 URL로 리다이렉트
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenUtils tokenUtils;
    private final UserDetailsManager userDetailsManager;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public OAuth2SuccessHandler(
            JwtTokenUtils tokenUtils,
            UserDetailsManager userDetailsManager,
            RefreshTokenRedisRepository refreshTokenRedisRepository
    ) {
        this.tokenUtils = tokenUtils;
        this.userDetailsManager = userDetailsManager;
        this.refreshTokenRedisRepository = refreshTokenRedisRepository;
    }

    @Override
    // 인증 성공시 호출되는 메소드
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        // OAuth2UserServiceImpl에서 반환한 DefaultOAuth2User가 저장
        OAuth2User oAuth2User
                = (OAuth2User) authentication.getPrincipal();
        // 소셜 로그인을 한 새로운 사용자를 우리의 UserEntity로 전환
        String email = oAuth2User.getAttribute("email");
        String nickname = oAuth2User.getAttribute("nickname");
        String provider = oAuth2User.getAttribute("provider");
        String username
                = String.format("{%s}%s", provider, email.split("@")[0]);
        String providerId = oAuth2User.getAttribute("id").toString();

        // 처음으로 소셜 로그인한 사용자를 데이터베이스에 등록
        if (!userDetailsManager.userExists(username)) { //1. 최초 로그인인지 확인
            userDetailsManager.createUser(CustomUserDetails.builder()
                    .username(username)
                    .password(providerId)
                    .email(email)
                    .nickname(nickname)
                    .provider(provider)
                    .providerId(providerId)
                    .build());
        }

        // JWT 생성 - access & refresh
        UserDetails details
                = userDetailsManager.loadUserByUsername(username);
        JwtDto jwt = tokenUtils.generateToken(details); //2. access, refresh token 생성 및 발급
        log.info("accessToken: {}", jwt.getAccessToken());
        log.info("refreshToken: {} ", jwt.getRefreshToken());

        // 유효기간 초단위 설정 후 redis에 refresh token save
        Claims refreshTokenClaims = tokenUtils.parseClaims(jwt.getRefreshToken());
        Long validPeriod
                = refreshTokenClaims.getExpiration().toInstant().getEpochSecond()
                - refreshTokenClaims.getIssuedAt().toInstant().getEpochSecond();

        // 사용자의 리프레시 토큰을 업데이트
        Optional<RefreshToken> existingToken = refreshTokenRedisRepository.findById(username);
        if (existingToken.isPresent()) {
            refreshTokenRedisRepository.deleteById(username);
        }

        refreshTokenRedisRepository.save(
                RefreshToken.builder()
                        .id(username)
                        .ip(IpUtil.getClientIp(request))
                        .ttl(validPeriod)
                        .refreshToken(jwt.getRefreshToken())
                        .build()
        );

        // 목적지 URL 설정 - 토큰 던짐
        String targetUrl = String.format(
//                "http://domain/oauth/callback?access-token=%s&refresh-token=%s", jwt.getAccessToken(), jwt.getRefreshToken()
                "http://localhost:8080/token?access-token=%s&refresh-token=%s", jwt.getAccessToken(), jwt.getRefreshToken()
        );
        // 실제 Redirect 응답 생성
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
