package com.cooperation.project.cooperationcenter.domain.member.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
