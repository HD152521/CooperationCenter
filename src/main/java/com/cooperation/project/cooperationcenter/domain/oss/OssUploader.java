//
//package com.cooperation.project.cooperationcenter.domain.oss;
//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
//public class OssUploader {
//
//    private String endpoint = "https://oss-cn-beijing.aliyuncs.com"; // 버킷 리전 endpoint
//    private String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
//    private String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
//    private String bucketName = "cooperation-center";
//
//    @Value("${oss.bucket}") private String bucket;
//
//    public void uploadFile(String objectName, String filePath) {
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        ossClient.putObject(bucketName, objectName, new java.io.File(filePath));
//        ossClient.shutdown();
//    }
//
//    public String upload(String keyPrefix, MultipartFile file) {
//        try (InputStream in = file.getInputStream()) {
//            // 업로드될 키(버킷 내부 경로) 생성
//            String safeName = java.util.Objects.requireNonNullElse(file.getOriginalFilename(), "file");
//            safeName = safeName.replaceAll("[\\r\\n]", ""); // 안전 처리
//            String key = String.format("%s/%s_%s",
//                    keyPrefix, java.util.UUID.randomUUID(), safeName); // 예: uploads/uuid_name.png
//
//            // 메타데이터(길이/타입 지정 권장)
//            com.aliyun.oss.model.ObjectMetadata meta = new com.aliyun.oss.model.ObjectMetadata();
//            meta.setContentLength(file.getSize());
//            if (file.getContentType() != null) meta.setContentType(file.getContentType());
//            // 서버측 암호화가 필요하면:
//            // meta.setHeader(OSSHeaders.SERVER_SIDE_ENCRYPTION, "AES256");
//
//            oss.putObject(bucket, key, in, meta);
//            return key; // DB에는 이 key를 저장
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload to OSS", e);
//        }
//    }
//
//    public URL presignedGetUrl(String key, int minutes) {
//        java.util.Date exp = new java.util.Date(System.currentTimeMillis() + minutes * 60_000L);
//        com.aliyun.oss.model.GeneratePresignedUrlRequest req =
//                new com.aliyun.oss.model.GeneratePresignedUrlRequest(bucket, key, com.aliyun.oss.HttpMethod.GET);
//        req.setExpiration(exp);
//        return oss.generatePresignedUrl(req);
//    }
//}