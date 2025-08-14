package com.cooperation.project.cooperationcenter.domain.student.dto;

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

}
