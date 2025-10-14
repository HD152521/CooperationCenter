package com.cooperation.project.cooperationcenter.domain.member.service;

import com.cooperation.project.cooperationcenter.domain.member.dto.LoginLogDto;
import com.cooperation.project.cooperationcenter.domain.member.model.LoginLog;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.LoginLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    @Transactional
    public void recordLogin(Member member, boolean success, HttpServletRequest request) {
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        // 간단한 파싱 예시
        String deviceType = userAgent != null && userAgent.toLowerCase().contains("mobile")
                ? "MOBILE" : "DESKTOP";

        String platform = getPlatform(userAgent);

        LoginLog log = LoginLog.builder()
                .member(member)
                .ipAddress(ipAddress)
                .user_agent(userAgent)
                .deviceType(deviceType)
                .platform(platform)
                .success(success)
                .location("Unknown") // GeoIP API 연동 시 여기 채움
                .build();

        loginLogRepository.save(log);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 여러 IP일 경우 첫 번째 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0];
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    private String getPlatform(String userAgent) {
        if (userAgent == null) return "Unknown";
        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac")) return "Mac";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone") || userAgent.contains("ios")) return "iOS";

        return "Unknown";
    }

    public Page<LoginLogDto> getAllLogDtoByPage(Pageable pageable){
        Page<LoginLog> loginLogs = loginLogRepository.findAll(pageable);
        return LoginLogDto.from(loginLogs);
    }

}
