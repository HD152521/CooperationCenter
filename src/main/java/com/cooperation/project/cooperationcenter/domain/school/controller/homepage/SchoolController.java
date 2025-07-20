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
@RequestMapping("/school")
public class SchoolController {

    private final String schoolPath = "homepage/user/school/";

    @RequestMapping("/{school}/intro")
    public String schoolIntro(@PathVariable String school, Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-introduction";
    }

    @RequestMapping("/{school}/curriculum")
    public String schoolCurriculum(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-curriculum";
    }

    @RequestMapping("/{school}/board")
    public String schoolBoard(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-board";
    }

    @RequestMapping("/{school}/schedule")
    public String schoolSchedule(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-schedule";
    }

    @RequestMapping("/{school}/contact")
    public String schoolContact(@PathVariable String school,Model model){
        model.addAttribute("school",school);
        return schoolPath+school+"/school-contact";
    }

}
