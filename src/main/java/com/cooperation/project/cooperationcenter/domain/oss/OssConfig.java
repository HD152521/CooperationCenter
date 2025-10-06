package com.cooperation.project.cooperationcenter.domain.oss;

import com.aliyun.oss.OSS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {
    @Value("${oss.endpoint}") String endpoint;
    @Value("${oss.accessKeyId}") String keyId;
    @Value("${oss.accessKeySecret}") String keySecret;

    @Bean(destroyMethod = "shutdown")
    public OSS ossClient() {
        return new com.aliyun.oss.OSSClientBuilder().build(endpoint, keyId, keySecret);
    }
}