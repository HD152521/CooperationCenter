package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerPageDto;
import com.cooperation.project.cooperationcenter.domain.survey.dto.QuestionDto;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
@Slf4j
public class SurveyRestController {

    private final SurveySaveService surveyService;

    @PostMapping("/make")
    public void saveSurvey(@RequestBody SurveyRequest.SurveyDto request){
        log.info("[controller] {}",request.toString());
        surveyService.saveSurvey(request);
    }

    @GetMapping("/{surveyId}")
    public AnswerPageDto getSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        return surveyService.getSurveys(surveyId);
    }

    @GetMapping("/getAll")
    public List<SurveyResponseDto> getSurveyAll(){
        return surveyService.getAllSurvey();
    }

    @DeleteMapping("/{surveyId}")
    public BaseResponse<?> deleteSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveyService.deleteSurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/copy/{surveyId}")
    public BaseResponse<?> copoSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveyService.copySurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }


}
