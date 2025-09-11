package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
import com.cooperation.project.cooperationcenter.domain.survey.model.*;
import com.cooperation.project.cooperationcenter.domain.survey.repository.*;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyFindService {
    private final SurveyRepository surveyRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final SurveyLogRepository surveyLogRepository;
    private final AnswerRepository answerRepository;
    private final SurveyFolderRepository surveyFolderRepository;

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

    public Question getQuestion(Answer answer){
        try{
            return getQuestion(answer.getQuestionRealId());
        }catch (Exception e){
            log.warn("getQuestionByAnswer failed...");
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

    public List<QuestionOption> getOptions(Answer answer){
        try{
            Question quesion = getQuestion(answer);
            return questionOptionRepository.findQuestionOptionsByQuestion(quesion);
        }catch (Exception e){
            log.warn("getOptionsBy survey and question failed...");
            return null;
        }
    }

    public Page<SurveyResponseDto> getFilteredSurveysAll(Pageable pageable,SurveyRequest.LogFilterDto condition,SurveyFolder surveyFolder){
        if(condition.status()==null) condition = condition.setStatus();
        log.info("conditoin:{}",condition.toString());
        Page<Survey> surveys = getSurveyFromCondition(pageable,condition,surveyFolder);
        return surveys.map(survey -> {
            LocalDate now = LocalDate.now();
            int daysLeft = (survey.getEndDate() == null) ? 0 : (int) ChronoUnit.DAYS.between(now, survey.getEndDate());
            boolean isBefore = survey.getStartDate() != null && now.isBefore(survey.getStartDate()) && !now.equals(survey.getStartDate());

            return new SurveyResponseDto(
                    survey.getSurveyTitle(),
                    survey.getCreatedAt(),
                    survey.getParticipantCount(),
                    daysLeft,
                    survey.getSurveyId(),
                    isBefore,
                    survey.getStartDate(),
                    survey.getEndDate()
            );
        });
    }

    public Page<SurveyResponseDto> getFilteredSurveysAllIsActive(Pageable pageable,SurveyRequest.LogFilterDto condition,SurveyFolder surveyFolder){
        if (condition.status() == null) condition = condition.setStatus();
        Page<Survey> surveys = getSurveyFromCondition(pageable, condition,surveyFolder);

        LocalDate now = LocalDate.now();

        // stream 으로 바꿔서 종료된 설문 빼고 다시 PageImpl 로 감싸기
        List<SurveyResponseDto> filtered = surveys.stream()
                .filter(survey -> survey.getEndDate() == null || !survey.getEndDate().isBefore(now)) // 종료일이 오늘 이전이면 제외
                .map(survey -> {
                    int daysLeft = (survey.getEndDate() == null) ? 0 : (int) ChronoUnit.DAYS.between(now, survey.getEndDate());
                    boolean isBefore = survey.getStartDate() != null && now.isBefore(survey.getStartDate()) && !now.equals(survey.getStartDate());

                    return new SurveyResponseDto(
                            survey.getSurveyTitle(),
                            survey.getCreatedAt(),
                            survey.getParticipantCount(),
                            daysLeft,
                            survey.getSurveyId(),
                            isBefore,
                            survey.getStartDate(),
                            survey.getEndDate()
                    );
                })
                .toList();

        return new PageImpl<>(filtered, pageable, filtered.size());
    }

    public Page<SurveyResponseDto> getFilteredSurveysActive(Pageable pageable,SurveyRequest.LogFilterDto condition,String folderId,boolean isAdmin){
        SurveyFolder surveyFolder = surveyFolderRepository.findByFolderId(folderId).orElseGet(null);
        if(isAdmin) return getFilteredSurveysAll(pageable,condition,surveyFolder);
        else return getFilteredSurveysAllIsActive(pageable,condition,null);
    }


    public List<Survey> getAllSurveyFromDB(){
        try{
            return surveyRepository.findAll();
        }catch (Exception e){
            log.warn("get all survey failed...");
            return null;
        }
    }

    public Page<Survey> getAllSurveyFromDB(Pageable pageable){
        try{
            return surveyRepository.findAll(pageable);
        }catch (Exception e){
            log.warn("get all survey failed...");
            return null;
        }
    }

    public Page<Survey> getSurveyFromCondition(Pageable pageable, SurveyRequest.LogFilterDto condition,SurveyFolder surveyFolder){
        try{
            return surveyRepository.findByFilter(condition.text(),condition.date(), condition.status(),pageable,surveyFolder);
        }catch (Exception e){
            log.warn("get survey by conditon failed...");
            return null;
        }
    }

    public SurveyLog getSurveyLog(String logId){
        try{
            return surveyLogRepository.findSurveyLogBySurveyLogId(logId);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SurveyLog> getSurveyLogs(List<String> logIds){
        try{
            List<SurveyLog> response = new ArrayList<>();
            for(String id : logIds) response.add(getSurveyLog(id));
            if(response.isEmpty()) throw new NullPointerException();
            return response;
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SurveyLog> getSurveyLogs(Survey survey){
        try{
            return surveyLogRepository.findSurveysLogBySurvey(survey);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SurveyLog> getSurveyLogs(String surveyId){
        try{
            Survey survey = getSurveyFromId(surveyId);
            return surveyLogRepository.findSurveysLogBySurvey(survey);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public Page<SurveyLog> getSurveyLogs(Survey survey,Pageable pageable){
        try{
            return surveyLogRepository.findSurveysLogBySurvey(survey,pageable);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<Answer> getAnswer(SurveyLog surveyLog){
        try{
            return answerRepository.findAnswersBySurveyLog(surveyLog);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public String getAnswerFromMultiple(Answer answer){
        List<QuestionOption> options = getOptions(answer);
        log.info("options:{}",options.toString());
        if(answer.getAnswerType().equals(QuestionType.MULTIPLECHECK)){
            List<String> targetOptions = Arrays.stream(answer.getMultiAnswer().replaceAll("[\\[\\]]","").split(",\\s*"))
                    .map(s -> s.split("_",2)[1])
                    .toList();
            return String.join(",",targetOptions);
        }
        else if(answer.getAnswerType().equals(QuestionType.MULTIPLE)){
            return answer.getMultiAnswer().split("_",2)[1];
        }
        return answer.getAnswer();
    }

}
