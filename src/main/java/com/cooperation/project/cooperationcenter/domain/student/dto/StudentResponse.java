package com.cooperation.project.cooperationcenter.domain.student.dto;

import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public class StudentResponse {
    public record ListDto(
            Long studentId,
            String chineseName,
            String englishName,
            LocalDate birthDate,
            Gender gender,
            String studentEmail,
            String passportNumber,
            String examNumber,

            String agentWechat,
            String agentEmail,

            String emergencyContactNum,
            String memberName
    ){
        public static ListDto from(Student student){
            return new ListDto(
                    student.getId(),
                    student.getChineseName(),
                    student.getEnglishName(),
                    student.getBirthDate(),
                    student.getGender(),
                    student.getStudentEmail(),
                    student.getPassportNumber(),
                    student.getExamNumber(),
                    student.getAgentWechat(),
                    student.getAgentEmail(),
                    student.getEmergencyContactNum(),
                    student.getMember().getMemberName()
            );
        }
        public static List<ListDto> from(List<Student> students){
            return students.stream()
                    .map(ListDto::from)
                    .toList();
        }

        public static Page<ListDto> from(Page<Student> students){
            return students.map(ListDto::from);
        }

    }
}
