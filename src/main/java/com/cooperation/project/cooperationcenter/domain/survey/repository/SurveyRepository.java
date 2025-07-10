package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Survey findSurveyById(Long id);
    Survey findSurveyBySurveyId(String surveyId);
    Page<Survey> findAll(Pageable pageable);
    List<Survey> findAll();
    @Query("""
    SELECT s FROM Survey s
    WHERE (:title IS NULL OR s.surveyTitle LIKE %:title%)
      AND (:date IS NULL OR :date BETWEEN s.startDate AND s.endDate)
      AND (
           :status IS NULL OR :status = 'all' OR
           (:status = 'before' AND s.startDate > CURRENT_DATE) OR
           (:status = 'active' AND s.startDate <= CURRENT_DATE AND s.endDate >= CURRENT_DATE) OR
           (:status = 'ended' AND s.endDate < CURRENT_DATE)
       )
    """)
    Page<Survey> findByFilter(
            @Param("title") String title,
            @Param("date") LocalDate date,
            @Param("status") String status,
            Pageable pageable
    );
}
