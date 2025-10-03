package com.cooperation.project.cooperationcenter.domain.file.dto;

import org.springframework.web.multipart.MultipartFile;

public record FileAttachmentDto(
        MultipartFile file,
        String type,
        String postId,
        String memberId,
        String surveyId
) {
}
