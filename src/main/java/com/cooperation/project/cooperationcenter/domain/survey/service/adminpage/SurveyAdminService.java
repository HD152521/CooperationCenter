package com.cooperation.project.cooperationcenter.domain.survey.service.adminpage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyAdminStatsDto;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyAdminService {

    private final SurveyRepository surveyRepository;

    public SurveyAdminStatsDto getAdminStats() {
        LocalDate today = LocalDate.now();
        long total = surveyRepository.count();
        long before = surveyRepository.countByStartDateAfter(today);
        long active = surveyRepository.countByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today);
        long completed = surveyRepository.countByEndDateBefore(today);
        return new SurveyAdminStatsDto(total, before, active, completed);
    }

}
