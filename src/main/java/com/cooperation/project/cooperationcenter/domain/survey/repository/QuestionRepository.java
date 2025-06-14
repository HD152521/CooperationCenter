package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findQuestionsBySurvey(Survey survey);
}
