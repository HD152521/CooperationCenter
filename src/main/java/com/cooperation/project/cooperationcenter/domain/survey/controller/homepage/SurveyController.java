package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyEditDto;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyAnswerService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyLogService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/survey")
@Slf4j
public class SurveyController {

    private final SurveyAnswerService surveyAnswerService;
    private final SurveyFindService surveyFindService;
    private final SurveyLogService surveyLogService;
    private final String surveyPath = "homepage/user/survey";

    @RequestMapping("/make")
    public String makeForm(){
        return surveyPath+"/survey-make";
    }

    @RequestMapping("/list")
    public String surveyListUser(Model model,
                                 @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
                                 Pageable pageable,
                                 @ModelAttribute SurveyRequest.LogFilterDto condition,
                                 Authentication authentication){
        log.info("condition:{}",condition);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("condition", condition);
        if(isAdmin){
            Page<SurveyResponseDto> surveys = surveyFindService.getFilteredSurveysAll(pageable,condition);
            model.addAttribute("surveys", surveys);
            return surveyPath + "/survey-list-admin";
        }else{
            Page<SurveyResponseDto> surveys = surveyFindService.getFilteredSurveysActive(pageable,condition);
            model.addAttribute("surveys", surveys);
            return surveyPath + "/survey-list-user";
        }
    }

//    @RequestMapping("/list/filter")
//    public String surveyListUser(Model model,
//                                 @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
//                                 Pageable pageable,
//                                 @RequestBody(required = false) SurveyRequest.LogFilterDto request){
//        log.info("request:{}",request);
//        Page<SurveyResponseDto> surveys = surveyFindService.getAllSurvey(pageable,request);
//        model.addAttribute("surveys", surveys);
//        return surveyPath+"/survey-list-admin";
//    }

    @RequestMapping("/answer/{surveyId}")
    public String surveyAnswer(@PathVariable String surveyId, Model model){
        model.addAttribute("surveyId", surveyId);
        log.info("surveyId:{}",surveyId);
        return surveyPath+"/survey-answer";
    }

    //todo 밑으로는 관리자만

    @RequestMapping("/edit/{surveyId}")
    public String editSurvey(@PathVariable String surveyId, Model model){
        model.addAttribute("survey", SurveyEditDto.to(surveyId,surveyFindService));
        log.info("surveyId:{}",SurveyEditDto.to(surveyId,surveyFindService).toString());
        return surveyPath+"/survey-make";
    }

    @RequestMapping("/log/{surveyId}")
    public String getSurveyLog(@PathVariable String surveyId,Model model,
                               @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                               Pageable pageable){
        model.addAttribute("AnswerDto",surveyLogService.getAnswerLog(surveyId,pageable));
        return surveyPath+"/survey-answer-log";
    }

    @RequestMapping("/log/{surveyId}/{logId}")
    public String getSurveyLog(@PathVariable String surveyId,@PathVariable String logId,Model model){
        model.addAttribute("answerLog",surveyLogService.getAnswerLogDetail(surveyId,logId));
        AnswerResponse.AnswerLogDto response = surveyLogService.getAnswerLogDetail(surveyId,logId);
        log.info("{}",response.toString());
        return surveyPath+"/survey-answer-detail";
    }

}
