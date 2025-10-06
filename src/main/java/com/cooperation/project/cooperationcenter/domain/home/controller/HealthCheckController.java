package com.cooperation.project.cooperationcenter.domain.home.controller;

import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/check")
public class HealthCheckController {

    @RequestMapping({"/health","/healthCheck"})
    public BaseResponse<?> healthCheck(){
        return BaseResponse.onSuccess("success");
    }
}
