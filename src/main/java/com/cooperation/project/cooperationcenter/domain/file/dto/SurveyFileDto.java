package com.cooperation.project.cooperationcenter.domain.file.dto;

import com.cooperation.project.cooperationcenter.domain.file.model.FileType;
import org.springframework.web.multipart.MultipartFile;

public record SurveyFileDto(
        MultipartFile file,
        String surveyId,
        FileType fileType
) {
}
