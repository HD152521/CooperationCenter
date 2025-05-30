package com.cooperation.project.cooperationcenter.domain.member.controller;


import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);
    private final MemberService memberService;

    @PostMapping("/signup")
    public BaseResponse<?> signup(@ModelAttribute MemberRequest.SignupDto request) throws Exception{
        log.info("signup request: {}", request);
        return BaseResponse.onSuccess(memberService.signup(request));
    }
}
