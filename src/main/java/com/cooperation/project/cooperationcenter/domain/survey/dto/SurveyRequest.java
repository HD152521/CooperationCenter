package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;

import java.time.LocalDate;
import java.util.List;

public class SurveyRequest {
    public record SurveyDto(
            String description,
            String title,
            List<QuestionDto> questions,
            LocalDate startDate,
            LocalDate endDate
    ){
        public static Survey toEntity(SurveyDto request){
            return Survey.builder()
                    .surveyTitle(request.title)
                    .surveyDescription(request.description)
                    .startDate(request.startDate)
                    .endDate(request.endDate)
                    .build();
        }
    }
}
