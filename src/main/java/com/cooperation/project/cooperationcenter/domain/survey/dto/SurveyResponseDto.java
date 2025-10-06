package com.cooperation.project.cooperationcenter.domain.survey.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SurveyResponseDto(
        String title,
        LocalDateTime createdAt,
        int participant,
        int lastDate,
        String surveyId,
        boolean isBefore,
        LocalDate startDate,
        LocalDate endDate
) {
}
