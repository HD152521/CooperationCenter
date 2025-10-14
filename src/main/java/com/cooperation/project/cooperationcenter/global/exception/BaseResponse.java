package com.cooperation.project.cooperationcenter.global.exception;

import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import com.cooperation.project.cooperationcenter.global.exception.codes.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    /**
     * 요청 성공 시 응답 생성
     */

    public static<T> BaseResponse<T> onSuccess(T result){
        return new BaseResponse<>(true, SuccessCode.SUCCESS.getCode(), SuccessCode.SUCCESS.getMessage(), result);
    }

    public static <T> BaseResponse<T> of(SuccessCode code, T result){
        return new BaseResponse<>(true, code.getCode() , code.getMessage(), result);
    }

    /**
     * 요청 실패 시 응답 생성
     */
    public static <T> BaseResponse<T> onFailure(String code, String message, T data) {
        return new BaseResponse<>(false, code, message, data);
    }

    public static <T> BaseResponse<T> onFailure(ErrorCode errorCode, T data) {
        return new BaseResponse<>(false, errorCode.getCode(), errorCode.getMessage(), data);
    }

    public static <T> BaseResponse<T> onFailure(BaseCode baseCode, T data) {
        Reason.ReasonDto reason = baseCode.getReasonHttpStatus();
        return new BaseResponse<>(false, reason.getCode(), reason.getMessage(), data);
    }

}