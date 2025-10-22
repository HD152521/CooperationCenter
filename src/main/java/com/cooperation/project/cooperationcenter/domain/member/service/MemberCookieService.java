package com.cooperation.project.cooperationcenter.domain.member.service;

import com.cooperation.project.cooperationcenter.global.token.vo.AccessToken;
import com.cooperation.project.cooperationcenter.global.token.vo.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.cooperation.project.cooperationcenter.global.token.JwtProperties.*;

@Service
@RequiredArgsConstructor
public class MemberCookieService {

    public void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        // Access Token 쿠키
        addAccessTokenCookies(response, tokenResponse);
        addRefreshCookies(response, tokenResponse);
    }

    private void addAccessTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie accessCookie = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, tokenResponse.accessToken().token())
                .httpOnly(true)            // JS 접근 차단
//                .secure(true)              // HTTPS 전용
                .secure(false)
                .path("/")                 // 전체 경로에 대해 전송
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME) // 만료 시간
                .sameSite("None")        // CSRF 방어
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }
    private void addAccessTokenCookies(HttpServletResponse response, AccessToken accessToken) {
        ResponseCookie accessCookie = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, accessToken.token())
                .httpOnly(true)            // JS 접근 차단
                .secure(true)              // HTTPS 전용
                .path("/")                 // 전체 경로에 대해 전송
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME) // 만료 시간
                .sameSite("None")        // CSRF 방어
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    public void addAccessTokenCookies(HttpServletResponse response, String accessToken) {
        ResponseCookie accessCookie = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, accessToken)
                .httpOnly(true)            // JS 접근 차단
//                .secure(true)              // HTTPS 전용
                .secure(false)
                .path("/")                 // 전체 경로에 대해 전송
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME) // 만료 시간
                .sameSite("None")        // CSRF 방어
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    private void addRefreshCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        ResponseCookie refreshCookie = ResponseCookie.from(JWT_REFRESH_TOKEN_COOKIE_NAME, tokenResponse.refreshToken().token())
                .httpOnly(true)
//                .secure(true)
                .secure(false)
                .path("/")      // 리프레시 전용 엔드포인트에만 전송
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME)
                .sameSite("None")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

        public void deleteCookie(HttpServletResponse response, TokenResponse tokenResponse){
            expiredCookie(response,tokenResponse);
        }

        private void expiredCookie(HttpServletResponse response, TokenResponse tokenResponse){
            ResponseCookie deleteAccess = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, "")
                    .httpOnly(true)
//                    .secure(true)
                    .secure(false)
                    .path("/")                 // 로그인 시 지정한 path와 동일하게
                    .maxAge(0)                 // 즉시 만료
                    .sameSite("None")
                    .build();

            // 2) REFRESH_TOKEN 쿠키 삭제
            ResponseCookie deleteRefresh = ResponseCookie.from(JWT_REFRESH_TOKEN_COOKIE_NAME, "")
                    .httpOnly(true)
//                    .secure(true)
                    .secure(false)
                    .path("/")     // 로그인 시 지정한 path와 동일하게
                    .maxAge(0)
                    .sameSite("None")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, deleteAccess.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, deleteRefresh.toString());
        }

}

