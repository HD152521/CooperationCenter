package com.cooperation.project.cooperationcenter.domain.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("pageTitle", "대시보드 홈");
        return "index";  // templates/index.html
    }

    @GetMapping("/index3")
    public String index1(Model model) {
        model.addAttribute("pageTitle", "대시보드 홈");
        return "index3";  // templates/index.html
    }

    @GetMapping("/index2")
    public String index2(Model model) {
        model.addAttribute("pageTitle", "대시보드 홈");
        return "index2";  // templates/index.html
    }

    @GetMapping("/main")
    public String mainPage(Model model) {
        model.addAttribute("pageTitle", "대시보드 홈");
        return "main";  // templates/index.html
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "대시보드 홈");
        return "login";  // templates/index.html
    }

}
