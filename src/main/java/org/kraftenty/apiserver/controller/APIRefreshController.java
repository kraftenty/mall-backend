package org.kraftenty.apiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.kraftenty.apiserver.util.CustomJWTException;
import org.kraftenty.apiserver.util.JWTUtil;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader, // access token 이 들어있는 헤더 (Bearer xxxxxxx...)
            String refreshToken                                // refresh token
    ) {
        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_STRING");
        }

        String accessToken = authHeader.substring(7); // Bearer 를 떼어냄

        // accessToken 의 만료 여부 확인
        if (!checkExpiredToken(accessToken)) { // 만료되지 않았다면
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken); // 다시 돌려보낸다
        }

        // refreshToken 검증 -> 새로운 accessToken, refreshToken 만들어서 보냄
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("refresh... claims: " + claims);
        String newAccessToken = JWTUtil.generateToken(claims, 10);
        String newRefreshToken = checkTime((Integer) claims.get("exp")) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;
        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

    }

    private boolean checkTime(Integer exp) {
        // JWT exp 를 날짜로 변환
        Date expDate = new Date((long) exp * (1000));

        // 현재 시간과의 차이 계산 - ms
        long gap = expDate.getTime() - System.currentTimeMillis();

        // 분 단위로 변환
        long leftMin = gap / (1000 * 60);

        // 1시간 미만으로 남았는지 반환
        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {
        try {
            JWTUtil.validateToken(token);
        } catch (CustomJWTException e) {
            if (e.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }
}
