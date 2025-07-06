package com.cooperation.project.cooperationcenter.domain.agency.controller.homepage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/agency")
@Slf4j
public class AgengyController {

    private final String agencyPath = "homepage/user/agency";
    @RequestMapping("/list")
    public String agencyList(){
        return agencyPath+"/agency-introduction";
    }
}

