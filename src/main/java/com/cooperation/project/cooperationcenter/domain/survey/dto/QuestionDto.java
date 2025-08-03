package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionOption;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record QuestionDto(
        String questionId,
        String type,
        String question,
        String description,
        List<OptionDto> options,
        int questionOrder,
        boolean isTemplate,
        String domainField
){
    public static List<QuestionDto> to(Survey survey){
        List<QuestionDto> dtos = new ArrayList<>();
        List<Question> questions = survey.getQuestions()
                .stream()
                .sorted(Comparator.comparing(Question::getQuestionOrder))
                .toList();
        for(Question q : questions){
            dtos.add(
                    new QuestionDto(
                            q.getQuestionId(),
                            q.getQuestionType().getType(),
                            q.getQuestion(),
                            q.getQuestionDescription(),
                            OptionDto.to(q.getOptions()),
                            q.getQuestionOrder(),
                            q.isTemplate(),
                            q.getDomainField()
                    )
            );
        }
        return dtos;
    }
}
