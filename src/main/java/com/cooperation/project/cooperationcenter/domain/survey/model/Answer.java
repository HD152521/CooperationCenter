package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE answer SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int questionId;
    private String questionRealId;

    private QuestionType answerType;

    private String textAnswer;
    private String multiAnswer;
    private LocalDateTime dateAnswer;
    //todo File클래스 새로 만들어야함.
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    private SurveyLog surveyLog;

    @Builder
    public Answer(int questionId, String questionRealId, String textAnswer,String multiAnswer, LocalDateTime dateAnswer, String filePath,QuestionType answerType,SurveyLog surveyLog){
        this.questionId = questionId;
        this.questionRealId = questionRealId;
        this.textAnswer = textAnswer;
        this.multiAnswer = multiAnswer;
        this.dateAnswer = dateAnswer;
        this.filePath = filePath;
        this.answerType = answerType;
        this.surveyLog = surveyLog;
    }
}
