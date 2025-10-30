package com.cooperation.project.cooperationcenter.domain.file.dto;

import com.cooperation.project.cooperationcenter.domain.file.model.MemberFileType;
import com.cooperation.project.cooperationcenter.domain.file.model.SurveyFileType;
import org.springframework.web.multipart.MultipartFile;

public record MemberFileDto(
        MultipartFile file,
        MemberFileType memberFileType
) {
}
