package com.cooperation.project.cooperationcenter.domain.member.controller;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberAdminService;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class MemberAdminRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberAdminRestController.class);
    private final MemberAdminService memberAdminService;

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody MemberRequest.LoginDto request, HttpServletResponse response, HttpSession session) throws Exception{
        log.info("id:{}, pw:{}",request.email(),request.password());
        return BaseResponse.onSuccess(memberAdminService.login(request,response,session));
    }

}
