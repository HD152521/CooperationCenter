package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Survey findSurveyById(Long id);
    Survey findSurveyBySurveyId(String surveyId);
    Page<Survey> findAll(Pageable pageable);
    List<Survey> findAll();
}
