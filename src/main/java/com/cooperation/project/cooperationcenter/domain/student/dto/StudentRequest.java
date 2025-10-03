package com.cooperation.project.cooperationcenter.domain.student.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class StudentRequest {

    public record MappingDto(
            String chineseName,
            String englishName,
            LocalDate birthDate,
            Gender gender,
            String studentEmail,
            String passportNumber,
            String examNumber,

            String agentWechat,
            String agentEmail,

            String emergencyContactNum
    ){}

    public record ConditionDto(
            String name,
            Gender gender,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthStart,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthEnd,
            String email,
            String passport,
            String exam,
            String surveyLogId,
            String surveyTitle,
            String agencyName
    ){}

}
