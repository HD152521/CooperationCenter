package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.QuestionRequestDto;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
@Slf4j
public class SurveyRestController {
    @PostMapping("/make")
    public void getSurvey(@RequestBody SurveyRequest.SurveyDto reqeust){
        log.info("[controller] {}",reqeust.toString());
    }



}
