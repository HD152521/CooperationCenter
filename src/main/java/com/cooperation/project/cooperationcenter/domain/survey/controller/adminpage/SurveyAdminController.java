package com.cooperation.project.cooperationcenter.domain.survey.controller.adminpage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class SurveyAdminController {

    private final SurveyFindService surveyFindService;

    @RequestMapping("/survey")
    public String surveyPage(Model model,
                             @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                             Pageable pageable){
        Page<SurveyResponseDto> surveys = surveyFindService.getFilteredSurveysActive(pageable,new SurveyRequest.LogFilterDto(null,null,null));
        model.addAttribute("surveys", surveys);
        return "/adminpage/user/survey/manageSurvey";
    }
}
