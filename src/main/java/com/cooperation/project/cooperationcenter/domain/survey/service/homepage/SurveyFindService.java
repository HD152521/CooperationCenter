package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionOption;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.repository.QuestionOptionRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.QuestionRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyFindService {
    private final SurveyRepository surveyRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;

    public Survey getSurveyFromId(Long surveyId){
        try{
            return surveyRepository.findSurveyById(surveyId);
        }catch (Exception e){
            log.warn("getSurveyFormId Fail");
            return null;
        }
    }

    public Survey getSurveyFromId(String surveyId){
        try{
            return surveyRepository.findSurveyBySurveyId(surveyId);
        }catch (Exception e){
            log.warn("getSurveyForm survey Id Fail");
            return null;
        }
    }

    public List<Question> getQuestions(Survey survey){
        try{
            return questionRepository.findQuestionsBySurvey(survey);
        }catch(Exception e){
            log.warn("getQuestionBySurvey failed...");
            return null;
        }
    }

    public Question getQuestion(Long id){
        try{
            return questionRepository.findQuestionById(id);
        }catch (Exception e){
            log.warn("getQuestionById failed...");
            return null;
        }
    }

    public Question getQuestion(String id){
        try{
            return questionRepository.findQuestionByQuestionId(id);
        }catch (Exception e){
            log.warn("getQuestionById failed...");
            return null;
        }
    }

    public List<QuestionOption> getOptions(Survey survey){
        try{
            log.info("surveyId:{}",survey.getId());
            return questionOptionRepository.findQuestionOptionsBySurvey(survey);
        }catch (Exception e){
            log.warn("getOptionsBy survey and question failed...");
            return null;
        }
    }

    public List<SurveyResponseDto> getAllSurvey(){
        List<SurveyResponseDto> response = new ArrayList<>();
        List<Survey> surveys = getAllSurveyFromDB();
        for(Survey survey : surveys){
            LocalDate now = LocalDate.now();
            int daysLeft = (survey.getEndDate()==null) ? 0 : Period.between(now, survey.getEndDate()).getDays();
            boolean isBefore = survey.getStartDate() != null && now.isBefore(survey.getStartDate());
            response.add(
                    new SurveyResponseDto(
                            survey.getSurveyTitle(),
                            survey.getCreatedAt(),
                            survey.getParticipantCount(),
                            daysLeft,
                            survey.getSurveyId(),
                            isBefore
                    )
            );
        }
        log.info("response:{}",response.get(0).toString());
        return response;
    }

    public List<Survey> getAllSurveyFromDB(){
        try{
            return surveyRepository.findAll();
        }catch (Exception e){
            log.warn("get all survey failed...");
            return null;
        }
    }
}
