package com.cooperation.project.cooperationcenter.domain.survey.dto;

public record SurveyAdminStatsDto(
        long total,
        long before,
        long active,
        long completed
) { }
