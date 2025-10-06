package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;

import java.time.LocalDate;
import java.util.List;

public record SurveyEditDto (
        String description,
        String title,
        List<QuestionDto> questions,
        LocalDate startDate,
        LocalDate endDate,
        String surveyType,
        String surveyId,
        boolean isShare,
        String folderId
){
    public static SurveyEditDto from(String surveyid, SurveyFindService surveyFindService){
        Survey survey = surveyFindService.getSurveyFromId(surveyid);
        return new SurveyEditDto(
                survey.getSurveyDescription(),
                survey.getSurveyTitle(),
                QuestionDto.to(survey),
                survey.getStartDate(),
                survey.getEndDate(),
                survey.getSurveyType().getType(),
                surveyid,
                survey.isShare(),
                survey.getSurveyFolder().getFolderId()
        );
    }
}

