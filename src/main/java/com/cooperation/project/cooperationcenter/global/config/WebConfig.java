package com.cooperation.project.cooperationcenter.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class WebConfig {


    //note web로그 찍는용도
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);     // IP, 세션ID, 사용자 이름
        filter.setIncludeQueryString(true);    // ?a=1&b=2
        filter.setIncludePayload(true);        // POST/PUT 페이로드
        filter.setMaxPayloadLength(10000);     // 최대 바디 길이 (byte)
        filter.setIncludeHeaders(false);       // true로 하면 헤더도 찍음
        return filter;
    }
}