package com.cooperation.project.cooperationcenter.domain.member.controller.adminpage;

import com.cooperation.project.cooperationcenter.domain.member.dto.LoginLogDto;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.model.LoginLog;
import com.cooperation.project.cooperationcenter.domain.member.service.LoginLogService;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class MemberAdminRestController {

    private static final Logger log = LoggerFactory.getLogger(MemberAdminRestController.class);
    private final MemberService memberService;
    private final LoginLogService loginLogService;

    @Operation(
            summary = "관리자 로그인",
            description = """
        관리자 계정으로 로그인합니다.
        """
    )
    @PostMapping("/login")
    public BaseResponse<?> login(@RequestBody MemberRequest.LoginDto request, HttpServletResponse response) throws Exception{
        log.info("id:{}, pw:{}",request.email(),request.password());
        memberService.adminLogin(request,response);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "관리자 로그인 기록 조회",
            description = """
        관리자 로그인 이력을 페이지네이션 형태로 조회합니다.
        """
    )
    @GetMapping("/login/log")
    public BaseResponse<?> loginPage(
            @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return BaseResponse.onSuccess(loginLogService.getAllLogDtoByPage(pageable));
    }

    @Operation(
            summary = "회원 가입 승인",
            description = """
        관리자 권한으로 회원 가입을 승인합니다.
        """
    )
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

    @Operation(
            summary = "회원 승인 대기 처리",
            description = """
        특정 회원을 승인 대기 상태로 변경합니다.
        """
    )
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

    @Operation(
            summary = "회원 상세 조회",
            description = """
        이메일을 기준으로 회원 상세 정보를 조회합니다.
        """
    )
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
