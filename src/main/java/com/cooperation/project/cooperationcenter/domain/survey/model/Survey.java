package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE survey SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Survey extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String owner;
    private String surveyTitle;
    private String surveyDescription;
    private int participantCount;

    @OneToMany(mappedBy = "survey")
    private final List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private final List<SurveyLog> surveyLogs = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private final List<QuestionOption> options = new ArrayList<>();

    @Builder
    public Survey(String surveyTitle,String surveyDescription,String owner){
        this.surveyDescription = surveyDescription;
        this.surveyTitle = surveyTitle;
        this.participantCount = 0;
        this.owner = owner;
    }

    public void setQuestion(Question question){
        if(this.questions.contains(question)) return;
        this.questions.add(question);
    }

    public void setSurveyLogs(SurveyLog log){
        if(this.surveyLogs.contains(log)) return;
        this.surveyLogs.add(log);
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
