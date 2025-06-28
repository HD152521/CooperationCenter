package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyLogRepository extends JpaRepository<SurveyLog,Long> {
    List<SurveyLog> findSurveysLogBySurvey(Survey survey);
    int countSurveyLogsBySurvey(Survey survey);

}
