package com.cooperation.project.cooperationcenter.domain.home.controller;

import com.cooperation.project.cooperationcenter.domain.agency.service.homepage.AgencyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final AgencyService agencyService;

    @RequestMapping({"/", "/home"})
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("agencyDto", agencyService.getAgencyListForHome());
        return "/homepage/user/index";
    }
}
