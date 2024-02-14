package kaulikeLion.Backend.oauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TokenController {

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> tokenPagge(
            @RequestParam(name = "access-token") String accessToken,
            @RequestParam(name = "refresh-token") String refreshToken
    ) {
        // 결과 데이터를 Map에 담아 반환
        Map<String, String> responseData = new HashMap<>();
        responseData.put("accessToken", accessToken);
        responseData.put("refreshToken", refreshToken);

        // JSON 형태로 응답
        return ResponseEntity.ok(responseData);
    }
}
