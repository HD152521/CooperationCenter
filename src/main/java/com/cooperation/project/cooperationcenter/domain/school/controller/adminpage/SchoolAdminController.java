package com.cooperation.project.cooperationcenter.domain.school.controller.adminpage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class SchoolAdminController {
    @RequestMapping("/school")
    public String school(){

        return "/adminpage/user/school/manageSchool";
    }
}
