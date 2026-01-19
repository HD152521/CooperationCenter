package com.cooperation.project.cooperationcenter.domain.member.exception.status;

import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorStatus implements BaseCode {
    // ===== 조회 =====
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-4041", "존재하지 않는 회원입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "LOGIN-0000", "이메일이 잘못됨"),
    PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "LOGIN-0001", "잘못된 비밀번호입니다."),
    MEMBER_AGENCY_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER-4043","소속된 기관 정보가 존재하지 않습니다."),
    // ===== 생성 / 중복 =====
    MEMBER_ALREADY_EXIST(HttpStatus.CONFLICT, "MEMBER-4091", "이미 존재하는 회원입니다."),
    MEMBER_ALREADY_ACCEPTED_EMAIL(HttpStatus.CONFLICT, "MEMBER-4092", "해당 이메일로 승인된 계정이 이미 존재합니다."),

    // ===== 상태 =====
    MEMBER_NOT_ACCEPTED(HttpStatus.FORBIDDEN, "MEMBER-4031", "아직 계정이 활성화되지 않았습니다."),
    MEMBER_STATUS_INVALID(HttpStatus.FORBIDDEN,"MEMBER-4033","비활성화된 계정입니다."),
    // ===== 권한 =====
    MEMBER_NOT_ADMIN(HttpStatus.FORBIDDEN, "MEMBER-4032", "관리자 권한이 필요합니다."),

    // ===== 서버 =====
    MEMBER_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MEMBER-5001", "회원 저장 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public Reason.ReasonDto getReasonHttpStatus() {
        return Reason.ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }

}
