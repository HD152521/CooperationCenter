package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyAnswerService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyLogService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
@Slf4j
public class SurveyRestController {

    private final SurveySaveService surveySaveService;
    private final SurveyFindService surveyFindService;
    private final SurveyAnswerService surveyAnswerService;
    private final SurveyLogService surveyLogService;

    @PostMapping("/make")
    public void saveSurvey(@RequestBody SurveyRequest.SurveyDto request){
        log.info("[controller] {}",request.toString());
        surveySaveService.saveSurvey(request);
    }

    @GetMapping("/{surveyId}")
    public AnswerPageDto getSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        return surveySaveService.getSurveys(surveyId);
    }

    @GetMapping("/getAll")
    public List<SurveyResponseDto> getSurveyAll(){
        return surveyFindService.getAllSurvey();
    }

    @DeleteMapping("/{surveyId}")
    public BaseResponse<?> deleteSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.deleteSurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/copy/{surveyId}")
    public BaseResponse<?> copoSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.copySurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }

    @PatchMapping("/edit")
    public void editSurvey(@RequestBody SurveyEditDto request){
        log.info("[controller] getSurvey 진입 : {}",request.surveyId());
        //fixme 제목 안바뀜
        surveySaveService.editSurvey(request);
    }

    @PostMapping("/answer")
    public ResponseEntity<Void> receiveSurveyAnswer(
            @RequestPart("data") String data,
            HttpServletRequest request
    ) throws JsonProcessingException {
        log.info("[submit answer] dto:{}",data);
        try{
            surveyAnswerService.answerSurvey(data,request);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
        log.info("save answer");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/answer/{surveyId}")
    public AnswerResponse.AnswerDto getAnswerLog(@PathVariable String surveyId){
        AnswerResponse.AnswerDto result = surveyLogService.getAnswerLog(surveyId);
        log.info("result : {}",result.toString());
        return result;
    }

    @PostMapping("/log/csv")
    public ResponseEntity<Resource> extractCsv(@RequestBody LogCsv.RequestDto request){
        log.info("[enter extract csv]");
        return surveyLogService.extractCsv(request);
    }
}
