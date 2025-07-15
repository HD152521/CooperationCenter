package com.cooperation.project.cooperationcenter.domain.member.controller;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class MemberAdminController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "adminpage/user/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpServletRequest request) {
        memberService.logout(request,response);
        return "redirect:/admin/login";
    }

    @RequestMapping("/user")
    public String manageUser(
            Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @ModelAttribute MemberRequest.UserFilterDto condition
    ){
        model.addAttribute("condition", condition);
        model.addAttribute("userDto", memberService.getMangeUserPage(condition,pageable));
        log.info("model data 주입 완료");
        return "adminpage/user/member/manageUser";
    }
}
