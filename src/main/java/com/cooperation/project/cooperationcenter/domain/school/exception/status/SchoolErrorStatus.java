package com.cooperation.project.cooperationcenter.domain.school.exception.status;

import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SchoolErrorStatus implements BaseCode {

    SCHOOL_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4001", "학교를 찾을 수 없습니다."),
    SCHOOL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"SCHOOL-4002","해당 학교는 이미 존재합니다."),

    SCHOOL_BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4101", "게시판을 찾을 수 없습니다."),
    SCHOOL_BOARD_TYPE_MATCH_ERROR(HttpStatus.NOT_FOUND, "SCHOOL-4102", "해당 타입의 School Board를 찾을 수 없습니다."),
    SCHOOL_INTRO_BOARD_TYPE_ERROR(HttpStatus.NOT_FOUND, "SCHOOL-4103", "해당 타입은 소개 페이지 타입이 아닙니다."),

    SCHOOL_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4201", "게시글을 찾을 수 없습니다."),
    FILE_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4211", "파일 게시글을 찾을 수 없습니다."),
    INTRO_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4221", "소개글을 찾을 수 없습니다."),
    INTRO_BOARD_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "SCHOOL-4222", "해당 학교는 소개글이 이미 존재합니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL-4231", "일정을 찾을 수 없습니다."),
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
