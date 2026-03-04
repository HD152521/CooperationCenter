package com.cooperation.project.cooperationcenter.global.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //Authentication예외 상황일 때 로그인 페이지로 보내기 위함.
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String uri = request.getRequestURI();
        log.info("custom Authen path:{}",uri);
        if (uri.startsWith("/admin")) {
            response.sendRedirect("/admin/login");
        } else{
            response.sendRedirect("/member/login");
        }
    }
}