package kaulikeLion.Backend.oauth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TokenController {
    @GetMapping("/token")
    public String getToken(@RequestParam("access-token") String accessToken) {

        return "{\"access_token\": \"" + accessToken + "\", \"message\": \"Token received successfully\"}";
    }
}
