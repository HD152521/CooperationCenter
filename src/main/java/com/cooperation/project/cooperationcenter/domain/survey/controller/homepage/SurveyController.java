package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyEditDto;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyAnswerService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;
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

    private final SurveyAnswerService surveyAnswerService;
    private final SurveyFindService surveyFindService;
    private final String surveyPath = "homepage/user/survey";

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

    @RequestMapping("/edit/{surveyId}")
    public String editSurvey(@PathVariable String surveyId, Model model){
        model.addAttribute("survey", SurveyEditDto.to(surveyId,surveyFindService));
        log.info("surveyId:{}",SurveyEditDto.to(surveyId,surveyFindService).toString());
        return surveyPath+"/survey-make";
    }

    @RequestMapping("/log/{surveyId}")
    public String getSurveyLog(@PathVariable String surveyId,Model model){
        model.addAttribute("AnswerDto",surveyAnswerService.getAnswerLog(surveyId));
        return surveyPath+"/survey-answer-log";
    }

    @RequestMapping("/log/{surveyId}/{logId}")
    public String getSurveyLog(@PathVariable String surveyId,@PathVariable String logId,Model model){
        model.addAttribute("answerLog",surveyAnswerService.getAnswerLogDetail(surveyId,logId));
        AnswerResponse.AnswerLogDto response = surveyAnswerService.getAnswerLogDetail(surveyId,logId);
        log.info("{}",response.toString());
        return surveyPath+"/survey-answer-detail";
    }

}
