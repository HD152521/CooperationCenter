package com.cooperation.project.cooperationcenter.domain.school.exception.status;

import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SchoolErrorStatus implements BaseCode {

    SCHOOL_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4041", "학교를 찾을 수 없습니다."),
    SCHOOL_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4042", "게시판을 찾을 수 없습니다."),
    SCHOOL_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4043", "게시글을 찾을 수 없습니다."),
    FILE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4044", "파일 게시글을 찾을 수 없습니다."),
    INTRO_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4045", "소개글을 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4046", "일정을 찾을 수 없습니다."),
    COLLEGE_NOT_FOUND(HttpStatus.NOT_FOUND,"COLLEGE-0001","해당 학과를 찾을 수가 없습니다.");

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
