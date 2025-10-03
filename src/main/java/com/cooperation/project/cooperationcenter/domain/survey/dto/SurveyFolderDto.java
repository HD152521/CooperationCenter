package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyFolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record SurveyFolderDto(
        String folderId,
        String displayName,
        String storedName,
        LocalDateTime createdAt
) {
    public static SurveyFolderDto from(SurveyFolder surveyFolder) {
        return new SurveyFolderDto(
                surveyFolder.getFolderId(),
                surveyFolder.getDisplayName(),
                surveyFolder.getStoredName(),
                surveyFolder.getCreatedAt()
        );
    }

    public static List<SurveyFolderDto> from(List<SurveyFolder> surveyFolders) {
        return surveyFolders.stream()
                .map(SurveyFolderDto::from)
                .toList();
    }
}
