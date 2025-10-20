package com.cooperation.project.cooperationcenter.domain.school.controller.adminpage;

import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class SchoolAdminController {

    private final SchoolFindService schoolFindService;

    @RequestMapping("/school")
    public String school(Model model){
        model.addAttribute("schoolDto",schoolFindService.getSchoolPage());
        return "adminpage/user/school/manageSchool";
    }
}
