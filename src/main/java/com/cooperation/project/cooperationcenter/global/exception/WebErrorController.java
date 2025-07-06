package com.cooperation.project.cooperationcenter.global.exception;

import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestController
@Slf4j
public class WebErrorController implements ErrorController {

    @RequestMapping("/error")
    public BaseResponse<?> handleError(HttpServletRequest request) throws NoHandlerFoundException {
        Object status = request.getAttribute("jakarta.servlet.error.status_code");
        log.info(request.getRequestURI());
        log.info("status:{}",status );
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                throw new NoHandlerFoundException("GET", request.getRequestURI(), HttpHeaders.EMPTY);
            }
        }
        return BaseResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), null);
    }

}