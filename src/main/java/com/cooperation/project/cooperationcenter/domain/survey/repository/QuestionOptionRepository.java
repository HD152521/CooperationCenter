package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionOption;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionType;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findQuestionOptionsBySurvey(Survey survey);
    QuestionOption findQuestionOptionById(Long id);

}
