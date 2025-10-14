package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.domain.member.model.LoginLog;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record LoginLogDto(
        String memberName,
        String loginTime,
        String ipAddress,
        boolean success
) {
    public static LoginLogDto from(LoginLog log) {
        return new LoginLogDto(
                log.getMember().getMemberName(),
                log.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                log.getIpAddress(),
                log.isSuccess()
        );
    }

    public List<LoginLogDto> from(List<LoginLog> logs) {
        return logs.stream().map(LoginLogDto::from).toList();
    }

    public static Page<LoginLogDto> from(Page<LoginLog> page) {
        return page.map(LoginLogDto::from);
    }
}
