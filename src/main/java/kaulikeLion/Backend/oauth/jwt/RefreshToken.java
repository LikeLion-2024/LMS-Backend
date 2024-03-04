package kaulikeLion.Backend.oauth.jwt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Collection;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
//@RedisHash(value = "refresh",  timeToLive = 1000L)
public class RefreshToken {
    @Id
    private String id;

    private String ip;

    @ElementCollection
    private Collection<String> authorities; // 권한을 문자열 형태로 저장
//    private Collection<? extends GrantedAuthority> authorities;

    private String refreshToken;

    private Long ttl;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateTtl(Long ttl) {
        this.ttl = ttl;
    }
}