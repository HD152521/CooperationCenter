package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyLogRepository extends JpaRepository<SurveyLog,Long> {
    List<SurveyLog> findSurveysLogBySurvey(Survey survey);
    Page<SurveyLog> findSurveysLogBySurvey(Survey survey, Pageable pageable);
    int countSurveyLogsBySurvey(Survey survey);
    SurveyLog findSurveyLogBySurveyLogId(String logId);
    Page<SurveyLog> findSurveysLogByMember(Member member, Pageable pageable);

}
