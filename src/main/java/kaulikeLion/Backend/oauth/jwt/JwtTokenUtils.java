package kaulikeLion.Backend.oauth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Component
// JWT 관련 기능들을 넣어두기 위한 기능성 클래스
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;
    private final int accessExpirationTime;
    private final int refreshExpirationTime;

    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.accessExpirationTime}") int accessExpirationTime,
            @Value("${jwt.refreshExpirationTime}") int refreshExpirationTime
    )
    {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.signingKey).build();
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }

    // 주어진 사용자 정보를 바탕으로 JWT를 문자열로 생성
    public JwtDto generateToken(UserDetails userDetails) {
        log.info("\"{}\" jwt 발급", userDetails.getUsername());
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        // Claims: JWT에 담기는 정보의 단위를 Claim이라 부른다.
        //         Claims는 Claim들을 담기위한 Map의 상속 interface
        Claims accessTokenClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(accessExpirationTime)));
        String accessToken = Jwts.builder()
                .setClaims(accessTokenClaims)
                .claim("authorities", authorities)
                .signWith(signingKey)
                .compact();

        Claims refreshTokenClaims = Jwts.claims()
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshExpirationTime)));
        String refreshToken = Jwts.builder()
                .setClaims(refreshTokenClaims)
                .signWith(signingKey)
                .compact();

        return JwtDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT가 유효한지 판단
    // jjwt 라이브러리에서는 JWT를 해석하는 과정에서 유효하지 않으면 예외가 발생
    public boolean validate(String token) {
        try {
            // 정당한 JWT면 true,
            // parseClaimsJws: 암호화된 JWT를 해석하기 위한 메소드
            jwtParser.parseClaimsJws(token);
            return true;
            // 정당하지 않은 JWT면 false
        } catch (Exception e) {
            log.warn("invalid jwt: {}", e.getClass());
            return false;
        }
    }

    // JWT를 인자로 받고, 그 JWT를 해석해서
    // 사용자 정보를 회수하는 메소드
    public Claims parseClaims(String token) {
        log.info("jwt parsing : {}", jwtParser.parseClaimsJws(token).getBody());
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }
}
