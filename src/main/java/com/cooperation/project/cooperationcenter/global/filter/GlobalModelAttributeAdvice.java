package com.cooperation.project.cooperationcenter.global.filter;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberResponse;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeAdvice {

    private final MemberRepository memberRepository;

    public GlobalModelAttributeAdvice(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @ModelAttribute("requestURI")
    public String setRequestUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/") ? null : uri;
    }

    @ModelAttribute("loginMember")
    public MemberResponse.LoginDto addLoginMember(@AuthenticationPrincipal MemberDetails memberDetails) {
        if (memberDetails == null) return null;
        String email = memberDetails.getUsername();
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(
                ()->new BaseException(ErrorCode.MEMBER_NOT_FOUND)
        );
        return MemberResponse.LoginDto.from(member); // null일 수 있음
    }
}
