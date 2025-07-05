package com.cooperation.project.cooperationcenter.domain.survey.dto;

import java.util.List;

public record AnswerPageDto(
        String title,
        String description,
        List<QuestionDto> questions
) {}
