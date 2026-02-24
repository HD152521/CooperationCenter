package com.cooperation.project.cooperationcenter.domain.file.exception.status;

import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FileErrorStatus implements BaseCode {

    // ===== 요청/검증 =====
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "FILE-4001", "업로드할 파일이 존재하지 않습니다."),
    FILE_TYPE_INVALID(HttpStatus.BAD_REQUEST, "FILE-4002", "지원하지 않는 파일 타입입니다."),
    FILE_TARGET_INVALID(HttpStatus.BAD_REQUEST, "FILE-4003", "파일 대상 타입이 올바르지 않습니다."),
    FILE_SIZE_ERROR(HttpStatus.BAD_REQUEST, "FILE-4004", "파일 사이즈가 너무 큽니다."),

    // ===== 조회 =====
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-4041", "파일을 찾을 수 없습니다."),
    FILE_META_NOT_FOUND(HttpStatus.NOT_FOUND, "FILE-4042", "파일 메타데이터가 존재하지 않습니다."),

    // ===== 저장 =====
    FILE_SAVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-5001", "파일 저장에 실패했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-5002", "스토리지 업로드에 실패했습니다."),

    // ===== 스토리지 =====
    FILE_STORAGE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-5003", "스토리지에 파일이 존재하지 않습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-5004", "파일 삭제에 실패했습니다."),

    // ===== URL =====
    FILE_URL_GENERATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-5005", "파일 URL 생성에 실패했습니다.");

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
