package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;

import java.util.List;

public class SurveyRequest {
    public record SurveyDto(
            String description,
            String title,
            List<QuestionDto> questions
    ){
        public static Survey toEntity(SurveyDto request){
            return Survey.builder()
                    .surveyTitle(request.title)
                    .surveyDescription(request.description)
                    .build();
        }
    }
}
