package com.cooperation.project.cooperationcenter.domain.survey.dto;

import java.util.List;

public record QuestionRequestDto(
        String type,
        String question,
        String description,
        List<String> options
){}
