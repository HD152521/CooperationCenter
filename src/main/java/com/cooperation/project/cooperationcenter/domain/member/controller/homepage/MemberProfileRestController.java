package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.dto.UpdatePasswordDto;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.domain.member.service.ProfileService;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
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

    @PatchMapping("/businessCert")
    public BaseResponse<?> updateBusinessCertificate(
            @RequestPart(name = "businessCertificate", required = false) MultipartFile file
            , @AuthenticationPrincipal MemberDetails memberDetails
    ){
        try{
            profileService.updateBussinessCert(file,memberDetails);
            return BaseResponse.onSuccess("success");
        }catch (BaseException e){
            log.warn(e.getCode().toString());
            return BaseResponse.onFailure(e.getCode(),null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return BaseResponse.onFailure("ERROR",e.getMessage().toString(),false);
        }
    }

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
