package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.domain.survey.dto.OptionDto;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE question SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Survey survey;

    @OneToMany(mappedBy = "question")
    private List<QuestionOption> options = new ArrayList<>();
    private QuestionType questionType;
    private String question;
    private String questionDescription;
    private boolean isNecessary;
    private boolean isOption;
    private int questionOrder;
    private String domainField;
    private boolean template;

    @Builder
    public Question(QuestionType questionType, String questionDescription, boolean isNecessary, Survey survey,String question,int questionOrder,String domainField,boolean template
                    ){
        this.questionType = questionType;
        this.question = question;
        this.questionDescription = questionDescription;
        this.isNecessary = isNecessary;
        this.survey = survey;
        this.isOption = QuestionType.checkType(questionType);
        this.questionId = UUID.randomUUID().toString();
        this.questionOrder = questionOrder;
        this.domainField = domainField;
        this.template = template;
    }

    public void setOptions(QuestionOption option) {
        if(this.options.contains(option)) return;
        this.options.add(option);
    }

    public void removeOption(QuestionOption option){
        this.getOptions().remove(option);
    }

    public List<String> getOptionString(){
        if(!this.isOption) return null;
        List<String> options = new ArrayList<>();
        for(QuestionOption op : this.getOptions()) options.add(op.getOptionText());
        return options;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", survey=" + survey +
                ", questionType=" + questionType +
                ", questionDescription='" + questionDescription + '\'' +
                ", isNecessary=" + isNecessary +
                '}';
    }
}
