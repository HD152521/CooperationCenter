package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/survey")
@Slf4j
public class SurveyController {

    String surveyPath = "homepage/user/survey";

    @RequestMapping("/make")
    public String makeForm(){
        return surveyPath+"/survey-make";
    }

    @RequestMapping("/list")
    public String surveyListUser(){
            return surveyPath+"/survey-list-admin";
    }

    @RequestMapping("/answer/{surveyId}")
    public String surveyAnswer(@PathVariable String surveyId, Model model){
        model.addAttribute("surveyId", surveyId);
        log.info("surveyId:{}",surveyId);
        return surveyPath+"/survey-answer";
    }

}
