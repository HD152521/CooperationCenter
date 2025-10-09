package com.cooperation.project.cooperationcenter.domain.oss;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.beust.jcommander.internal.Nullable;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class OssService {
    private final OSS oss;
    @Value("${oss.bucket}") private String bucket;

    public String upload(String keyPrefix, MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            // 업로드될 키(버킷 내부 경로) 생성
            String safeName = java.util.Objects.requireNonNullElse(file.getOriginalFilename(), "file");
            safeName = safeName.replaceAll("[\\r\\n]", ""); // 안전 처리
            String key = String.format("%s/%s_%s",
                    keyPrefix, java.util.UUID.randomUUID(), safeName); // 예: uploads/uuid_name.png

            // 메타데이터(길이/타입 지정 권장)
            com.aliyun.oss.model.ObjectMetadata meta = new com.aliyun.oss.model.ObjectMetadata();
            meta.setContentLength(file.getSize());
            if (file.getContentType() != null) meta.setContentType(file.getContentType());
//            meta.setHeader(OSSHeaders.SERVER_SIDE_ENCRYPTION, "AES256");
            meta.setHeader("x-oss-server-side-encryption", "AES256");

            oss.putObject(bucket, key, in, meta);
            return key; // DB에는 이 key를 저장
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to OSS", e);
        }
    }

    /** 사전서명 다운로드 URL(Private 버킷용) */
    public URL presignedGetUrl(String key, int minutes) {
        java.util.Date exp = new java.util.Date(System.currentTimeMillis() + minutes * 60_000L);
        com.aliyun.oss.model.GeneratePresignedUrlRequest req =
                new com.aliyun.oss.model.GeneratePresignedUrlRequest(bucket, key, com.aliyun.oss.HttpMethod.GET);
        req.setExpiration(exp);
        return oss.generatePresignedUrl(req);
    }

    public URL presignedGetUrl(String key,
                               int minutes,
                               boolean download,                 // true면 강제 다운로드, false면 인라인 뷰
                               @Nullable String fileName,        // 다운로드 파일명(옵션)
                               @Nullable String contentType) {   // 뷰 시 브라우저 힌트(옵션)
        Date exp = new Date(System.currentTimeMillis() + minutes * 60_000L);
        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucket, key, HttpMethod.GET);
        req.setExpiration(exp);

        ObjectMetadata md = oss.getObjectMetadata(bucket, key);
        log.info("CT={}", md.getContentType());
        log.info("CD={}", md.getContentDisposition());     // 여기서 attachment면 '객체 메타' 원인
        log.info("USER={}", md.getUserMetadata());         // x-oss-meta-... 강제다운로드 류 키가 있나 확인

        if (!download) {
            log.info("download안하는 URL반환하기");
            req.addQueryParameter("response-content-disposition", "inline");
        }

        // 다운로드 강제 + 파일명 지정
        if (download) {
            log.info("download하는 URL임");
            String cd = "attachment";
            if (fileName != null && !fileName.isBlank()) {
                // RFC 5987 방식까지 함께 넣어 브라우저 호환성↑
                String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                        .replaceAll("\\+", "%20");
                cd += "; filename=\"" + encoded + "\"; filename*=UTF-8''" + encoded;
            }
            req.addQueryParameter("response-content-disposition", cd);
//             if (contentType != null) req.addQueryParameter("response-content-type", contentType);
        }

        return oss.generatePresignedUrl(req);
    }

    public boolean isFileExist(FileAttachment file) {
        boolean result = oss.doesObjectExist(bucket, file.getPath());
        log.info("object isExist:{}",result);
        return result;
    }


    public OSSObject getObject(FileAttachment file){
        log.info("getting object...");
        return oss.getObject(bucket, file.getPath());
    }



}