package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.dto.UpdatePasswordDto;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.domain.member.service.ProfileService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
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
        profileService.updateMember(request,memberDetails);
        return BaseResponse.onSuccess("success");
    }

    @PatchMapping("/agency")
    public BaseResponse<?> updateAgencyInfo(@RequestBody Profile.MemberDto request, @AuthenticationPrincipal MemberDetails memberDetails){
        log.info("request:{}",request.toString());
        profileService.updateAgency(request,memberDetails);
        return BaseResponse.onSuccess("success");
    }

    @PatchMapping("/businessCert")
    public BaseResponse<?> updateBusinessCertificate(
            @RequestPart(name = "businessCertificate", required = false) MultipartFile file
            , @AuthenticationPrincipal MemberDetails memberDetails
    ){
        profileService.updateBussinessCert(file,memberDetails);
        return BaseResponse.onSuccess("success");
    }

    @PatchMapping("/agencyPicture")
    public BaseResponse<?> updateAgencyPicture(
            @RequestPart(name = "agencyPicture", required = false) MultipartFile file
            , @AuthenticationPrincipal MemberDetails memberDetails
    ){
        profileService.updateAgencyPicture(file,memberDetails);
        return BaseResponse.onSuccess("success");
    }
}
