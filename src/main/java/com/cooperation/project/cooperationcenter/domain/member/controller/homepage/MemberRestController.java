package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;


import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.dto.UpdatePasswordDto;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "회원가입",
            description = """
        일반 회원 및 유학원 회원 가입을 처리합니다.
        회원 정보와 유학원 정보, 선택적으로 첨부 파일을 함께 등록합니다.
        """
    )
    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<?> signup(
            @RequestPart("memberData") String memberData,
            @RequestPart("agencyData") String agencyData,
            @RequestPart(name = "agencyPicture", required = false) MultipartFile agencyPicture,
            @RequestPart(name = "businessCertificate", required = false) MultipartFile businessCertificate
    ) throws JsonProcessingException {
        try{
            memberService.signup(memberData,agencyData,agencyPicture,businessCertificate);
            return BaseResponse.onSuccess("success");
        }catch (BaseException e){
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }

    @Operation(
            summary = "ID중복확인",
            description = """
        회원가입시 가입하려는 사용자의 ID 중복 여부를 판단
        """
    )
    @GetMapping("/check-id")
    public BaseResponse<?> checkDuplicateId(@RequestParam String username) {
        try{
            return BaseResponse.onSuccess(memberService.isUsernameTaken(username));
        }catch (BaseException e){
            return BaseResponse.onFailure(e.getCode(),false);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }

    @Operation(
            summary = "로그인",
            description = """
        이메일과 비밀번호를 이용해 로그인을 수행합니다.
        인증 성공 시 AccessToken 및 RefreshToken을 반환합니다.
        """
    )
    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody MemberRequest.LoginDto requestDto, HttpServletResponse response,HttpServletRequest request){
        try{
            memberService.login(requestDto,response,request);
            log.info("loginSuccess");
            return BaseResponse.onSuccess("success");
        }catch (BaseException e){
            return BaseResponse.onFailure(e.getCode(),false);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }

    }

    @Operation(
            summary = "로그아웃",
            description = """
        현재 로그인된 사용자의 RefreshToken을 만료 처리합니다.
        """
    )
    @PostMapping("/logout")
    public BaseResponse<?> userLogout(HttpServletRequest request ,HttpServletResponse response){
        try {
            memberService.logout(request, response);
            return BaseResponse.onSuccess("log out success");
        }catch (BaseException e){
            return BaseResponse.onFailure(e.getCode(),false);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }

    @Operation(
            summary = "토큰 재발급",
            description = """
        만료된 AccessToken을 RefreshToken을 통해 재발급합니다.
        """
    )
    @PostMapping("/refresh")
    public BaseResponse<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("come refresh");
        return memberService.updateRefreshToken(request,response);
    }

    //fixme 확인하면 비밀번호 수정하는 HTML이어야함. 수정 필요
    @Operation(
            summary = "비밀번호 재설정 이메일 전송",
            description = """
        비밀번호 재설정을 위한 인증 이메일을 발송합니다.
        """
    )
    @PostMapping("/reset/email")
    public BaseResponse<?> sendPasswordResetEmail(@RequestBody UpdatePasswordDto.CheckEmailDto request) throws Exception {
        log.info("request:{}",request.toString());
        memberService.sendEmail(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "비밀번호 재설정",
            description = """
        이메일로 전달받은 인증 토큰을 이용해 비밀번호를 재설정합니다.
        """
    )
    @PostMapping("/reset/password")
    public BaseResponse<?> updatePassword(@RequestBody UpdatePasswordDto.PasswordCheckDto request) throws Exception {
        log.info("request:{}",request.toString());
        memberService.resetPassword(request);
        return BaseResponse.onSuccess("success");
    }

}
