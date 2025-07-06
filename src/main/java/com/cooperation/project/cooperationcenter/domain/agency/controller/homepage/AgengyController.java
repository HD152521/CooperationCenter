package com.cooperation.project.cooperationcenter.domain.agency.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.agency.service.homepage.AgencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/agency")
@Slf4j
public class AgengyController {

    private final AgencyService agencyService;

    private final String agencyPath = "homepage/user/agency";
    @RequestMapping("/list")
    public String agencyList(Model model){
        model.addAttribute("agencyDto",agencyService.getAgencyList());
        return agencyPath+"/agency-introduction";
    }
}

