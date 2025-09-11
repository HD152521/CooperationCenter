package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.*;
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
import org.springframework.web.bind.annotation.*;

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
    private final SurveyFolderService surveyFolderService;

    @RequestMapping("/make")
    public String makeForm(
            Model model,
            @RequestParam(value="folderId", required=true) String folderId
    ){
        model.addAttribute("folderId",folderId);
        return surveyPath+"/survey-make";
    }

    @RequestMapping("/list")
    public String surveyListUser(Model model,
                                 @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
                                 Pageable pageable,
                                 @ModelAttribute SurveyRequest.LogFilterDto condition,
                                 @RequestParam(value = "folderId", required = false) String folderId,
                                 Authentication authentication){
        log.info("condition:{}",condition);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("condition", condition);
        if((folderId==null) &&isAdmin) {
            List<SurveyFolderDto> surveyFolderDto = surveyFolderService.getSurveyFolderDtos();
            model.addAttribute("folders", surveyFolderDto);
            return surveyPath + "/survey-folder-list";
        }else{
            Page<SurveyResponseDto> surveys = surveyFindService.getFilteredSurveysActive(pageable, condition, folderId,isAdmin);
            model.addAttribute("surveys", surveys);
            if(isAdmin){
                model.addAttribute("folderId",folderId);
                return surveyPath + "/survey-list-admin";
            }
            return surveyPath + "/survey-list-user";
        }
    }

    @RequestMapping("/answer/{surveyId}")
    public String surveyAnswer(@PathVariable String surveyId, Model model){
        model.addAttribute("surveyId", surveyId);
        log.info("surveyId:{}",surveyId);
        return surveyPath+"/survey-answer";
    }

    //todo 밑으로는 관리자만

    @RequestMapping("/edit/{surveyId}")
    public String editSurvey(@PathVariable String surveyId, Model model){
        model.addAttribute("survey", SurveyEditDto.from(surveyId,surveyFindService));
        log.info("surveyId:{}",SurveyEditDto.from(surveyId,surveyFindService).toString());
        return surveyPath+"/survey-make";
    }

    @RequestMapping("/log/list/{surveyId}")
    public String getSurveyLog(@PathVariable String surveyId,Model model,
                               @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                               Pageable pageable){
        model.addAttribute("AnswerDto",surveyLogService.getAnswerLog(surveyId,pageable));
        return surveyPath+"/survey-answer-log";
    }

    @RequestMapping("/log/detail/{logId}")
    public String getSurveyLog(@PathVariable String logId,Model model,Authentication authentication){
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("answerLog",surveyLogService.getAnswerLogDetail(logId));
        model.addAttribute("isAdmin",isAdmin);
        AnswerResponse.AnswerLogDto response = surveyLogService.getAnswerLogDetail(logId);
        log.info("{}",response.toString());
        return surveyPath+"/survey-answer-detail";
    }
}
