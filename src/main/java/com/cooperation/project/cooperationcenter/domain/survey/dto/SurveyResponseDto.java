package com.cooperation.project.cooperationcenter.domain.survey.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SurveyResponseDto(
        String title,
        LocalDateTime createdAt,
        int participant,
        int lastDate
) {
}
