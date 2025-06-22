package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionOption;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;

import java.util.ArrayList;
import java.util.List;

public record QuestionDto(
        String type,
        String question,
        String description,
        List<String> options,
        boolean required
){
    public static List<QuestionDto> to(Survey survey){
        List<QuestionDto> dtos = new ArrayList<>();
        List<Question> questions = survey.getQuestions();
        for(Question q : questions){
            List<String> op = q.getOptionString();
            dtos.add(
                    new QuestionDto(
                            q.getQuestionType().getType(),
                            q.getQuestion(),
                            q.getQuestionDescription(),
                            op,
                            q.isNecessary()
                    )
            );
        }
        return dtos;
    }
}
