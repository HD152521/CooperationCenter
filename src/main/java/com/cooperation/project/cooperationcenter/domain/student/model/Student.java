package com.cooperation.project.cooperationcenter.domain.student.model;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.student.dto.Gender;
import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "student")
@Builder
@SQLDelete(sql = "UPDATE student SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Student extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chineseName;
    private String englishName;
    private LocalDate birthDate;
    private Gender gender;
    private String studentEmail;
    private String passportNumber;
    private String examNumber;

    private String agentWechat;
    private String agentEmail;

    private String emergencyContactNum;

    @OneToOne
    private Member member;
    @OneToOne
    private SurveyLog surveyLog;

    public Student from(StudentRequest.MappingDto dto){
        return Student.builder()
                .chineseName(dto.chineseName())
                .englishName(dto.englishName())
                .birthDate(dto.birthDate())
                .gender(dto.gender())
                .studentEmail(dto.studentEmail())
                .passportNumber(dto.passportNumber())
                .examNumber(dto.examNumber())
                .agentWechat(dto.agentWechat())
                .agentEmail(dto.agentEmail())
                .emergencyContactNum(dto.emergencyContactNum())
                .build();
    }


}
