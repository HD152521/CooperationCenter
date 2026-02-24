package com.cooperation.project.cooperationcenter.domain.student.exception;

import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.BaseCode;

public class StudentHandler extends BaseException {
    public StudentHandler(BaseCode code){super(code);}
}
