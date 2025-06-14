package com.cooperation.project.cooperationcenter.domain.survey.dto;

import java.util.List;

public class SurveyRequest {
    public record SurveyDto(
            String description,
            String title,
            List<QuestionRequestDto> questions
    ){}
}
