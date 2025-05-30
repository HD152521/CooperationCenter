package com.cooperation.project.cooperationcenter.domain.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/excep")
public class ExceptionController {

    @GetMapping("/server")
    public String serverError(Model model){
        model.addAttribute("pageTitle", "대시보드 홈");
        return "exception/500none";
    }


}
