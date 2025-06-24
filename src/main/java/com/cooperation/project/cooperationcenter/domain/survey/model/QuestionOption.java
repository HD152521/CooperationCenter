package com.cooperation.project.cooperationcenter.domain.survey.model;


import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE question_option SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class QuestionOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String optionText;
    private int nextQuestionId; //다음 questionid
    private String realNextQuestionId; //다음 questionid

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @Builder
    public QuestionOption(String text, int nextQuestionId,Question question,Survey survey,String realNextQuestionId){
        this.optionText = text;
        this.nextQuestionId = nextQuestionId;
        this.question = question;
        this. survey = survey;
        this.realNextQuestionId = realNextQuestionId;
    }

}
