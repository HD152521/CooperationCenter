package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionOption;

import java.util.ArrayList;
import java.util.List;

public record OptionDto(
        int nextQuestion,
        String realNextQuestion,
        String text
){
    public static OptionDto to(QuestionOption questionOption) {
        return new OptionDto(
                questionOption.getNextQuestionId(),
                questionOption.getRealNextQuestionId(),
                questionOption.getOptionText()
        );
    }

    public static List<OptionDto> to(List<QuestionOption> questionOption) {
        List<OptionDto> dtos = new ArrayList<>();
        for(QuestionOption op : questionOption) {
            dtos.add(to(op));
        }
        return dtos;
    }
}
