package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE survey SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Survey extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String surveyId;

    private String owner;
    private String surveyTitle;
    private String surveyDescription;
    private int participantCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private int copyCnt;

    @OneToMany(mappedBy = "survey")
    private final List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private final List<QuestionOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private final List<SurveyLog> surveyLogs = new ArrayList<>();


    @Builder
    public Survey(String surveyTitle,String surveyDescription,String owner,LocalDate startDate, LocalDate endDate){
        this.surveyDescription = surveyDescription;
        this.surveyTitle = surveyTitle;
        this.participantCount = 0;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.surveyId = UUID.randomUUID().toString();
        this.copyCnt = 0;
    }

    public void setQuestion(Question question){
        if(this.questions.contains(question)) return;
        this.questions.add(question);
    }

    public void setSurveyLogs(SurveyLog log){
        if(this.surveyLogs.contains(log)) return;
        this.surveyLogs.add(log);
    }

    public void copyCntPlus(){
        this.copyCnt++;
    }

    public void removeQuestion(Question question){
        this.getQuestions().remove(question);
    }

    public void setOptions(QuestionOption option){
        if(this.options.contains(option)) return;
        this.options.add(option);
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", surveyTitle='" + surveyTitle + '\'' +
                ", surveyDescription='" + surveyDescription + '\'' +
                ", participantCount=" + participantCount +
                ", questions=" + questions +
                ", surveyLogs=" + surveyLogs +
                '}';
    }
}
