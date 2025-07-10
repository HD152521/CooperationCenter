package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class SurveyRequest {

    private final SurveySaveService surveySaveService;

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

    public record LogFilterDto(
            String text,
            String status,
            LocalDate date
    ){
        public LogFilterDto setStatus(){
            return new LogFilterDto(
                    text,"active",date
            );
        }
    }
}
