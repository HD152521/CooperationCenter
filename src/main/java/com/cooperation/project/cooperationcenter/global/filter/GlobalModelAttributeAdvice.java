package com.cooperation.project.cooperationcenter.global.filter;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberResponse;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.School;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalModelAttributeAdvice {

    private final MemberRepository memberRepository;
    private final SchoolFindService schoolFindService;

    public GlobalModelAttributeAdvice(MemberRepository memberRepository,SchoolFindService schoolFindService) {
        this.memberRepository = memberRepository;
        this.schoolFindService = schoolFindService;
    }

    @ModelAttribute("requestURI")
    public String setRequestUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/") ? null : uri;
    }

    @ModelAttribute("loginMember")
    public MemberResponse.LoginDto addLoginMember(HttpServletRequest request,@AuthenticationPrincipal MemberDetails memberDetails) {
        String uri = request.getRequestURI();
        if (memberDetails == null || uri.startsWith("/api/")) return null;
        String email = memberDetails.getUsername();
        Member member = memberRepository.findMemberByEmail(email).orElseThrow(
                ()->new BaseException(ErrorCode.MEMBER_NOT_FOUND)
        );
        if(!member.isAccept()) {
            throw new BaseException(ErrorCode.MEMBER_NOT_ACCEPTED);
        }
        return MemberResponse.LoginDto.from(member); // null일 수 있음
    }

    @ModelAttribute("tokenExpired")
    public boolean addTokenExpiredFlag(HttpServletRequest request) {
        Object tokenExpired = request.getAttribute("tokenExpired");
        return tokenExpired != null && (boolean) tokenExpired;
    }

    @ModelAttribute("schoolCategory")
    public List<SchoolResponse.SchoolDto> loadSchoolCatogory() {
        return schoolFindService.loadAllSchoolByDto();
    }

    @ModelAttribute("schoolBoards")
    public List<SchoolResponse.SchoolBoardDto> loadSchoolBoards(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/school")) return null;

        @SuppressWarnings("unchecked")
        Map<String, String> vars = (Map<String, String>)
                request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String englishName = (vars != null) ? vars.get("school") : null;
        Long nowId = null;
        if (vars != null && vars.get("boardId") != null) {
            nowId = Long.valueOf(vars.get("boardId"));
        }
//        String englishName = request.getRequestURI().split("/")[2];
        School school = schoolFindService.loadSchoolByEnglishName(englishName);
        return schoolFindService.loadBoardBySchoolByDto(school,nowId);
    }
}
