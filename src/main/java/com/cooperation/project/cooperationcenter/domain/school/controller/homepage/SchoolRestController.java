package com.cooperation.project.cooperationcenter.domain.school.controller.homepage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/school")
public class SchoolRestController {

    @RequestMapping("/{school}/post")
    public String schoolPosts(@PathVariable String school){
        return null;
    }


}


