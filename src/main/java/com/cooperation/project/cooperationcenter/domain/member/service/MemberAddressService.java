package com.cooperation.project.cooperationcenter.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberAddressService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${tencent.map.suggestion-url}")
    private  String tencentApiUrl;
    @Value("${tencent.map.api-key}")
    private String tencentApiKey;


    public ResponseEntity<String> getMap(String keyword){
        try {
            // URL 인코딩 처리

            log.info("tencent address keyword : {}",keyword);

            String encodedKeyword = UriUtils.encode(keyword, StandardCharsets.UTF_8);
            String url = tencentApiUrl + "?keyword=" + encodedKeyword + "&key=" + tencentApiKey;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            return ResponseEntity.status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Tencent API 호출 실패: " + e.getMessage() + "\"}");
        }
    }

}
