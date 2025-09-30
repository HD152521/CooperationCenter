package com.cooperation.project.cooperationcenter.domain.member.dto;

public record MemberAdminStatsDto(
        long total,
        long activeMember,
        long newMember,
        long wailMember
) {}
