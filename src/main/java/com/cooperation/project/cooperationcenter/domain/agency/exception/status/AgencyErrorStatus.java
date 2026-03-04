package com.cooperation.project.cooperationcenter.domain.agency.exception.status;

import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AgencyErrorStatus implements BaseCode {

    AGENCY_NOT_FOUND(HttpStatus.BAD_REQUEST,"AGENCY-0001","해당 유학원은 가입 되지 않은 상태입니다. 확인 후에 다시 가입해주세요");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public Reason.ReasonDto getReasonHttpStatus() {
        return Reason.ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
