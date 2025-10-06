
package com.cooperation.project.cooperationcenter.domain.member.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.service.MemberService;
import com.cooperation.project.cooperationcenter.domain.member.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberProfileController {

    private final ProfileService profileService;
    private final String profilePath = "homepage/user/member/";

    @RequestMapping("/profile")
    public String profile(@AuthenticationPrincipal MemberDetails memberDetails,
                          Model model,
                          @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
                              Pageable pageable) {
        Profile.ProfileDto profile = profileService.getProfileDto(memberDetails,pageable);
        model.addAttribute("profileDto", profile);
        log.info("memberDto:{}", profile.member().toString());
        log.info("memberDto:{}", profile.surveys().toString());
        return profilePath+"profile";
    }
}

