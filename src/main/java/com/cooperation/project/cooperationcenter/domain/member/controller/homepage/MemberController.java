package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    String memberPath = "homepage/user";

    @RequestMapping("/signup")
    public String signup(){
        return memberPath+"/signup";
    }

    @RequestMapping("/login")
    public String login(){
        return memberPath+"/login";
    }

    @RequestMapping("/password/forgot")
    public String forgetPassword(){
        return memberPath+"/forgetPassword";
    }

    @RequestMapping("/password/reset")
    public String resetPassword(@RequestParam("token") String token, Model model){
        model.addAttribute("token", token);
        return memberPath+"/resetPassword";
    }


}
