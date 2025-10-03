package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyEditDto;
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
import java.util.Arrays;
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
    private boolean share;
    @Enumerated(EnumType.STRING) private SurveyType surveyType;

    @Getter
    public enum SurveyType{
        NORMAL("NORMAL"),
        STUDENT("STUDENT"),
        INVOICE("PROMOTION");

        private final String type;

        SurveyType(String type){this.type = type;}

        public static SurveyType getSruveyType(String type){
            return Arrays.stream(SurveyType.values())
                    .filter(t -> t.getType().equalsIgnoreCase(type))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid board type: " + type));
        }
    }

    @OneToMany(mappedBy = "survey")
    private final List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private final List<QuestionOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private final List<SurveyLog> surveyLogs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "survey_folder_id", nullable = false)
    private SurveyFolder surveyFolder;


    public void setSurveyFolder(SurveyFolder surveyFolder){
        this.surveyFolder = surveyFolder;
        surveyFolder.addSurvey(this);
    }


    @Builder
    public Survey(String surveyTitle,String surveyDescription,String owner,LocalDate startDate, LocalDate endDate,SurveyType surveyType,SurveyFolder surveyFolder,boolean share){
        this.surveyDescription = surveyDescription;
        this.surveyTitle = surveyTitle;
        this.participantCount = 0;
        this.owner = owner;
        this.startDate = startDate;
        this.endDate = endDate;
        this.surveyId = UUID.randomUUID().toString();
        this.surveyType = surveyType;
        this.surveyFolder = surveyFolder;
        this.copyCnt = 0;
        this.share = share;
    }

    public void setQuestion(Question question){
        if(this.questions.contains(question)) return;
        this.questions.add(question);
    }

    public void setSurveyLogs(SurveyLog log){
        if(this.surveyLogs.contains(log)) return;
        this.surveyLogs.add(log);
    }

    public void setParticipantCount(){
        this.participantCount++;
    }

    public void copyCntPlus(){
        this.copyCnt++;
    }

    public void removeQuestion(Question question){
        this.getQuestions().remove(question);
    }
    public void removeOption(QuestionOption option){
        this.getOptions().remove(option);
    }


    public void setOptions(QuestionOption option){
        if(this.options.contains(option)) return;
        this.options.add(option);
    }

    public void updateFromEditDto(SurveyEditDto dto) {
        this.surveyTitle = dto.title();
        this.surveyDescription = dto.description();
        this.startDate = dto.startDate();
        this.endDate = dto.endDate();
    }

    public void setByDto(SurveyEditDto dto){
        this.surveyDescription = dto.description();
        this.surveyTitle = dto.title();
        this.startDate = dto.startDate();
        this.endDate = dto.endDate();
        this.surveyType = SurveyType.getSruveyType(dto.surveyType());
        this.share = dto.isShare();
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
