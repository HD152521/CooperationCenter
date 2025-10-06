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
            LocalDate endDate,
            String surveyType,
            String folderId,
            boolean isShare
    ){
        public static Survey toEntity(SurveyDto request){
            Survey.SurveyType type = Survey.SurveyType.getSruveyType(request.surveyType());
            return Survey.builder()
                    .surveyTitle(request.title)
                    .surveyDescription(request.description)
                    .startDate(request.startDate)
                    .endDate(request.endDate)
                    .surveyType(type)
                    .share(request.isShare())
                    .build();
        }
    }

    public record LogFilterDto(
            String text,
            String status,
            LocalDate date,
            Survey.SurveyType surveyType
    ){
        public LogFilterDto setStatus(){
            return new LogFilterDto(
                    text,"all",date,surveyType
            );
        }
    }
}
