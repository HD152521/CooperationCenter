package com.cooperation.project.cooperationcenter.domain.oss;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class OssController {
    private final OssService ossService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "uploads") String prefix) {

        String key = ossService.upload(prefix, file);   // 로컬 저장 없이 바로 업로드
        URL url = ossService.presignedGetUrl(key, 15);  // 15분짜리 다운로드 URL
        return ResponseEntity.ok(Map.of("key", key, "url", url.toString()));
    }
}