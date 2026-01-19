package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.domain.member.service.ProfileService;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@Slf4j
public class MemberProfileRestController {

    public final ProfileService profileService;
    private final MemberService memberService;

    @Operation(
            summary = "회원 기본 정보 수정",
            description = """
        회원 이름, 연락처, 주소 등 기본 정보를 수정합니다.
        """
    )
    @PatchMapping("/member")
    public BaseResponse<?> updateMemberInfo(@RequestBody Profile.MemberDto request, @AuthenticationPrincipal MemberDetails memberDetails){
        log.info("request:{}",request.toString());
        try{
            profileService.updateMember(request,memberDetails);
            return BaseResponse.onSuccess("success");
        }catch (BaseException e){
            log.warn(e.getCode().toString());
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }

    @Operation(
            summary = "유학원 정보 수정",
            description = """
        유학원 이름, 지역, 연락처 정보를 수정합니다.
        """
    )
    @PatchMapping("/agency")
    public BaseResponse<?> updateAgencyInfo(@RequestBody Profile.AgencyDto request, @AuthenticationPrincipal MemberDetails memberDetails){
        log.info("request:{}",request.toString());
        try{
            profileService.updateAgency(request,memberDetails);
            return BaseResponse.onSuccess("success");
        }catch (BaseException e){
            log.warn(e.getCode().toString());
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }

    @Operation(
            summary = "사업자 등록증 수정",
            description = """
        사업자 등록증 파일을 업로드하여 변경합니다.
        """
    )
    @PatchMapping("/businessCert")
    public BaseResponse<?> updateBusinessCertificate(
            @RequestPart(name = "businessCertificate", required = false) MultipartFile file
            , @AuthenticationPrincipal MemberDetails memberDetails
    ){
        try{
            profileService.updateBusinessCert(file,memberDetails);
            return BaseResponse.onSuccess("success");
        }catch (BaseException e){
            log.warn(e.getCode().toString());
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }

    @Operation(
            summary = "유학원 프로필 이미지 수정",
            description = """
        유학원 프로필 이미지를 업로드 및 변경합니다.
        """
    )
    @PatchMapping("/agencyPicture")
    public BaseResponse<?> updateAgencyPicture(
            @RequestPart(name = "agencyPicture", required = false) MultipartFile file
            , @AuthenticationPrincipal MemberDetails memberDetails
    ){
        try{
            profileService.updateAgencyPicture(file,memberDetails);
            return BaseResponse.onSuccess("success");
        }catch (BaseException e){
            log.warn(e.getCode().toString());
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }
}
