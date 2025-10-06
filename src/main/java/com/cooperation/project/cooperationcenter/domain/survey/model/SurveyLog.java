package com.cooperation.project.cooperationcenter.domain.survey.model;


import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE surveyLog SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SurveyLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String surveyLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @OneToMany(mappedBy = "surveyLog")
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "surveyLog")
    private List<Student> students = new ArrayList<>();

    private LocalDateTime startTime;

    public void addAnswer(List<Answer> answers){
        this.answers.addAll(answers);
    }

    public void addAnswer(Answer answer){
        this.answers.add(answer);
    }

    @Builder
    public SurveyLog(Member member, Survey survey,LocalDateTime startTime) {
        this.member = member;
        this.survey = survey;
        this.startTime = startTime;
        this.surveyLogId =UUID.randomUUID().toString();
    }

}
