package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;

import java.time.LocalDate;
import java.util.List;

public record SurveyEditDto (
        String description,
        String title,
        List<QuestionDto> questions,
        LocalDate startDate,
        LocalDate endDate,
        String surveyId
){
    public static SurveyEditDto to(String surveyid, SurveySaveService surveySaveService){
        Survey survey = surveySaveService.getSurveyFromId(surveyid);
        return new SurveyEditDto(
                survey.getSurveyDescription(),
                survey.getSurveyTitle(),
                QuestionDto.to(survey),
                survey.getStartDate(),
                survey.getEndDate(),
                surveyid
        );
    }
}

