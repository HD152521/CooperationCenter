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
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public String getAnswer(){
        if(QuestionType.isFile(this.answerType)) return filePath;
        else if(QuestionType.checkType(this.answerType)){
            if(answerType.equals(QuestionType.MULTIPLE)){
                return multiAnswer.split("_")[0];
            }
            else if(answerType.equals(QuestionType.MULTIPLECHECK)){
                return Arrays.stream(multiAnswer.replaceAll("[\\[\\]]", "").split(",\\s*"))
                        .map(s -> s.split("_")[0])
                        .collect(Collectors.joining(","));
            }
            return multiAnswer;
        }
        else if(QuestionType.isDate(this.answerType)) return dateAnswer.toString();
        else if(QuestionType.isText(this.answerType)) return textAnswer;
        return null;
    }

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
