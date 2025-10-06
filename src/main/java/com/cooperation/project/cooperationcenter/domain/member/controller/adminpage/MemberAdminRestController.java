package com.cooperation.project.cooperationcenter.domain.member.controller.adminpage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class MemberAdminRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberAdminRestController.class);
    private final MemberService memberService;

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody MemberRequest.LoginDto request, HttpServletResponse response) throws Exception{
        log.info("id:{}, pw:{}",request.email(),request.password());
        memberService.adminLogin(request,response);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/accept/{memberEmail}")
    public BaseResponse<?> acceptMember(@PathVariable String memberEmail){
        log.info("email:{}",memberEmail);
        try{
            memberService.acceptedMember(memberEmail);
            return BaseResponse.onSuccess("success");
        }catch (Exception e){
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @PostMapping("/pending/{memberEmail}")
    public BaseResponse<?> pendingMember(@PathVariable String memberEmail){
        log.info("email:{}",memberEmail);
        try{
            memberService.pendingMember(memberEmail);
            return BaseResponse.onSuccess("success");
        }catch (Exception e){
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @GetMapping("/detail/{memberEmail}")
    public BaseResponse<?> detailMember(@PathVariable String memberEmail){
        log.info("email:{}",memberEmail);
        try{
            return BaseResponse.onSuccess(memberService.detailMember(memberEmail));
        }catch (Exception e){
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }
}
