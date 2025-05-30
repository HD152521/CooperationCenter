package com.cooperation.project.cooperationcenter.domain;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/main")
public class AdminMainController {

    @GetMapping("/page")
    public String mainPage(HttpSession session, Model model) {
        boolean isLogin = session.getAttribute("member") != null;
        if(!isLogin) return"redirect:/admin/login";

        model.addAttribute("pageTitle", "대시보드 홈");
        model.addAttribute("member", session.getAttribute("member"));
        log.info("{}",session.getAttribute("member"));
        return "admin/main";  // templates/index.html

    }
}
