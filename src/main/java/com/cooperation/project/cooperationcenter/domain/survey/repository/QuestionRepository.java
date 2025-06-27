package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findQuestionsBySurvey(Survey survey);
    Question findQuestionById(Long id);
    Question findQuestionByQuestionId(String id);
    @Query(value = "SELECT * FROM question WHERE is_deleted = FALSE ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Question findTopQuestionByOrderByIdDescNative();

}
