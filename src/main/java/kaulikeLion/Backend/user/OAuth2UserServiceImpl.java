package kaulikeLion.Backend.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//사용자가 정상적인 인증 이후 사용자 데이터를 조회하고 처리
@Slf4j
@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    // 1. DefaultOAuth2UserService 객체를 성공정보를 바탕으로 만듦
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 2. 생성된 Service 객체로부터 user 받음
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 3. 받은 User로부터 user 정보를 받음
        String registrationId = userRequest
                .getClientRegistration() // application.yaml에 등록한 id
                .getRegistrationId();
        String nameAttribute = "";
        // 4. SuccessHandler가 사용할 수 있도록 데이터를 다시 정리해줌
        Map<String, Object> attributes = new HashMap<>();

        // Naver 로직
        if (registrationId.equals("naver")) {
            attributes.put("provider", "naver");

            // 받은 사용자 데이터를 정리 - attributes 사용
            Map<String, Object> responseMap = oAuth2User.getAttribute("response");
            attributes.put("id", responseMap.get("id"));
            attributes.put("email", responseMap.get("email"));
            nameAttribute = "email";
        }
        log.info(attributes.toString());

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes,
                nameAttribute
        );
    }
}
