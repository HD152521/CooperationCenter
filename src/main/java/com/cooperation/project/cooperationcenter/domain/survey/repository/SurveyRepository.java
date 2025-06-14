package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Survey findSurveyById(Long id);
}
