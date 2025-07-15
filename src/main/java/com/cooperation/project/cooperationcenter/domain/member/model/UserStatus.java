package com.cooperation.project.cooperationcenter.domain.member.model;

public enum  UserStatus {
    PENDING,    // 승인 대기
    APPROVED,   // 승인 완료
    REJECTED,   // 승인 거절
    BANNED      // 차단됨
}
