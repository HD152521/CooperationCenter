package com.cooperation.project.cooperationcenter.global.exception;

import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(BaseException.class)
//    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
//        Reason.ReasonDto reason = e.getErrorReasonHttpStatus();
//        log.info("baseResponse:{}",BaseResponse.onFailure(reason.getCode(), reason.getMessage(), null).toString());
//        return ResponseEntity
//                .status(reason.getHttpStatus())
//                .body(BaseResponse.onFailure(reason.getCode(), reason.getMessage(), null));
//    }
    @ExceptionHandler(BaseException.class)
    public BaseResponse<?> handleBaseException(BaseException e) {
        Reason.ReasonDto reason = e.getErrorReasonHttpStatus();
        log.info("baseResponse:{}",BaseResponse.onFailure(reason.getCode(), reason.getMessage(), null).toString());
        return BaseResponse.onFailure(reason.getCode(), reason.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<?>> handleOther(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.onFailure(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                        null
                ));
    }
}
