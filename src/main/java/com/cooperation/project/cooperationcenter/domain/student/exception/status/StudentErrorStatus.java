package com.cooperation.project.cooperationcenter.domain.student.exception.status;

import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StudentErrorStatus implements BaseCode {

    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDENT-4041", "학생 정보를 찾을 수 없습니다."),
    STUDENT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STUDENT-5001", "학생 저장에 실패했습니다."),

    STUDENT_SURVEY_ANSWER_EMPTY(HttpStatus.BAD_REQUEST, "STUDENT-4001", "설문 답변이 존재하지 않습니다."),
    STUDENT_SURVEY_LOG_NOT_FOUND(HttpStatus.BAD_REQUEST, "STUDENT-4002", "설문 로그를 찾을 수 없습니다."),
    STUDENT_MAPPING_FAILED(HttpStatus.BAD_REQUEST, "STUDENT-4003", "학생 정보 매핑에 실패했습니다."),

    STUDENT_EXCEL_EXPORT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STUDENT-5002", "학생 엑셀 파일 생성에 실패했습니다.");

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
