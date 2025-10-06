package com.cooperation.project.cooperationcenter.global.exception.codes;


import com.cooperation.project.cooperationcenter.global.exception.codes.reason.Reason;

public interface BaseCode {
    public Reason.ReasonDto getReasonHttpStatus();
}
