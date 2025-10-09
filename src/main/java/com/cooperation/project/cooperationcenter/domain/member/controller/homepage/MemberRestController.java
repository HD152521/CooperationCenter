package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;


import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.dto.UpdatePasswordDto;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberRestController.class);
    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<?> signup(
            @RequestPart("data") String data,
            @RequestPart(name = "agencyPicture", required = false) MultipartFile agencyPicture,
            @RequestPart(name = "businessCertificate", required = false) MultipartFile businessCertificate
    ) throws JsonProcessingException {
        try{
            memberService.signup(data,agencyPicture,businessCertificate);
            return BaseResponse.onSuccess("success");
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure(ErrorCode.BAD_REQUEST,null);
        }
    }

    @GetMapping("/check-id")
    public BaseResponse<?> checkDuplicateId(@RequestParam String username) {
        boolean isDuplicate = memberService.isUsernameTaken(username);
        log.info("response:{}",isDuplicate);
        return BaseResponse.onSuccess(isDuplicate);
    }

    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody MemberRequest.LoginDto request, HttpServletResponse response){
            memberService.login(request,response);
            log.info("loginSuccess");
            return BaseResponse.onSuccess("success");
    }

    @PostMapping("/logout")
    public BaseResponse<?> userLogout(HttpServletRequest request ,HttpServletResponse response){
        memberService.logout(request,response);
        return BaseResponse.onSuccess("log out success");
    }

    @PostMapping("/refresh")
    public BaseResponse<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("come refresh");
        return memberService.updateRefreshToken(request,response);
    }

    //fixme 확인하면 비밀번호 수정하는 HTML이어야함. 수정 필요
    @PostMapping("/reset/email")
    public BaseResponse<?> sendPasswordResetEmail(@RequestBody UpdatePasswordDto.CheckEmailDto request) throws Exception {
        log.info("request:{}",request.toString());
        memberService.sendEmail(request);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/reset/password")
    public BaseResponse<?> updatePassword(@RequestBody UpdatePasswordDto.PasswordCheckDto request) throws Exception {
        log.info("request:{}",request.toString());
        memberService.resetPassword(request);
        return BaseResponse.onSuccess("success");
    }

}
