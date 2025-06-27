package com.cooperation.project.cooperationcenter.domain.survey.dto;

import java.util.List;

public class AnswerRequest {
    public record Dto(
            String surveyId,
            List<AnswerDto> answers
    ){}

    public record AnswerDto(
        int questionId,
        String type,
        Object answer,
        String questionRealId
    ){}
}
