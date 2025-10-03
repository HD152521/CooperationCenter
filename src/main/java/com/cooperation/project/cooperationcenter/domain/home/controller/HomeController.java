package com.cooperation.project.cooperationcenter.domain.home.controller;

import com.cooperation.project.cooperationcenter.domain.agency.service.homepage.AgencyService;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
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
    private final MemberService memberService;
    private final SchoolFindService schoolFindService;

    @RequestMapping({"/", "/home"})
    public String home(Model model, HttpServletRequest request) {
        model.addAttribute("agencyDto", agencyService.getAgencyListForHome());
        model.addAttribute("schoolDto", schoolFindService.loadAllSchoolByHomeDto());
        return "/homepage/user/index";
    }

    @RequestMapping({"/admin/home", "/admin"})
    public String adminHome(Model model, HttpServletRequest request) {
        model.addAttribute("pendingDto", memberService.getPendingList());
        log.info("pendingDto:{}",memberService.getPendingList().toString());
        return "/adminpage/user/index";
    }
}
