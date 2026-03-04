package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
import com.cooperation.project.cooperationcenter.domain.survey.model.*;
import com.cooperation.project.cooperationcenter.domain.survey.repository.*;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public Survey getSurveyFromId(Long surveyId) {
        return requireNonNull(surveyRepository.findSurveyById(surveyId), ErrorCode.NOT_FOUND_ERROR);
    }

    public Survey getSurveyFromId(String surveyId) {
        return requireNonNull(surveyRepository.findSurveyBySurveyId(surveyId), ErrorCode.NOT_FOUND_ERROR);
    }

    public List<Question> getQuestions(Survey survey) {
        if (survey == null) {
            throw new BaseException(ErrorCode.BAD_REQUEST_ERROR);
        }
        List<Question> questions = questionRepository.findQuestionsBySurvey(survey);
        return questions == null ? Collections.emptyList() : questions;
    }

    public Question getQuestion(Long id) {
        return requireNonNull(questionRepository.findQuestionById(id), ErrorCode.NOT_FOUND_ERROR);
    }

    public Question getQuestion(String id) {
        return requireNonNull(questionRepository.findQuestionByQuestionId(id), ErrorCode.NOT_FOUND_ERROR);
    }

    public Question getQuestion(Answer answer) {
        if (answer == null) {
            throw new BaseException(ErrorCode.BAD_REQUEST_ERROR);
        }
        return getQuestion(answer.getQuestionRealId());
    }

    public List<QuestionOption> getOptions(Survey survey) {
        if (survey == null) {
            throw new BaseException(ErrorCode.BAD_REQUEST_ERROR);
        }
        log.info("surveyId:{}", survey.getId());
        List<QuestionOption> options = questionOptionRepository.findQuestionOptionsBySurvey(survey);
        return options == null ? Collections.emptyList() : options;
    }

    public List<QuestionOption> getOptions(Answer answer) {
        Question question = getQuestion(answer);
        List<QuestionOption> options = questionOptionRepository.findQuestionOptionsByQuestion(question);
        return options == null ? Collections.emptyList() : options;
    }

    public Page<SurveyResponseDto> getFilteredSurveysAll(Pageable pageable, SurveyRequest.LogFilterDto condition, SurveyFolder surveyFolder) {
        if (condition.status() == null) condition = condition.setStatus();
        log.info("conditoin:{}", condition.toString());
        Page<Survey> surveys = getSurveyFromCondition(pageable, condition, surveyFolder);
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

    public Page<SurveyResponseDto> getFilteredSurveysAllIsActive(Pageable pageable, SurveyRequest.LogFilterDto condition, SurveyFolder surveyFolder) {
        if (condition.status() == null) condition = condition.setStatus();
        Page<Survey> surveys = getSurveyFromCondition(pageable, condition, surveyFolder);

        LocalDate now = LocalDate.now();

        List<SurveyResponseDto> filtered = surveys.stream()
                .filter(survey -> survey.getEndDate() == null || !survey.getEndDate().isBefore(now) && survey.isShare())
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

    public Page<SurveyResponseDto> getFilteredSurveysActive(Pageable pageable, SurveyRequest.LogFilterDto condition, String folderId, boolean isAdmin) {
        SurveyFolder surveyFolder = null;
        if (isAdmin && folderId != null && !folderId.isBlank()) {
            surveyFolder = surveyFolderRepository.findByFolderId(folderId)
                    .orElseThrow(() -> new BaseException(ErrorCode.BAD_REQUEST_ERROR));
        }

        if (isAdmin) return getFilteredSurveysAll(pageable, condition, surveyFolder);
        else return getFilteredSurveysAllIsActive(pageable, condition, null);
    }


    public List<Survey> getAllSurveyFromDB() {
        List<Survey> surveys = surveyRepository.findAll();
        return surveys == null ? Collections.emptyList() : surveys;
    }

    public Page<Survey> getAllSurveyFromDB(Pageable pageable) {
        return surveyRepository.findAll(pageable);
    }

    public Page<Survey> getSurveyFromCondition(Pageable pageable, SurveyRequest.LogFilterDto condition, SurveyFolder surveyFolder) {
        return surveyRepository.findByFilter(condition.text(), condition.date(), condition.status(), condition.surveyType(), pageable, surveyFolder);
    }

    public List<SurveyLog> findAllSurveyLog() {
        List<SurveyLog> logs = surveyLogRepository.findTop7ByOrderByCreatedAtDesc();
        return logs == null ? Collections.emptyList() : logs;
    }

    public SurveyLog getSurveyLog(String logId) {
        return requireNonNull(surveyLogRepository.findSurveyLogBySurveyLogId(logId), ErrorCode.NOT_FOUND_ERROR);
    }

    public List<SurveyLog> getSurveyLogs(List<String> logIds) {
        List<SurveyLog> response = new ArrayList<>();
        for (String id : logIds) response.add(getSurveyLog(id));
        if (response.isEmpty()) throw new BaseException(ErrorCode.NOT_FOUND_ERROR);
        return response;
    }

    public List<SurveyLog> getSurveyLogs(Survey survey) {
        if (survey == null) {
            throw new BaseException(ErrorCode.BAD_REQUEST_ERROR);
        }
        List<SurveyLog> logs = surveyLogRepository.findSurveysLogBySurvey(survey);
        return logs == null ? Collections.emptyList() : logs;
    }

    public List<SurveyLog> getSurveyLogs(String surveyId) {
        Survey survey = getSurveyFromId(surveyId);
        List<SurveyLog> logs = surveyLogRepository.findSurveysLogBySurvey(survey);
        return logs == null ? Collections.emptyList() : logs;
    }

    public Page<SurveyLog> getSurveyLogs(Member member, Pageable pageable) {
        if (member == null) {
            throw new BaseException(ErrorCode.BAD_REQUEST_ERROR);
        }
        return surveyLogRepository.findSurveysLogByMember(member, pageable);
    }

    public Page<SurveyLog> getSurveyLogs(Survey survey, Pageable pageable) {
        if (survey == null) {
            throw new BaseException(ErrorCode.BAD_REQUEST_ERROR);
        }
        return surveyLogRepository.findSurveysLogBySurvey(survey, pageable);
    }

    public List<Answer> getAnswer(SurveyLog surveyLog) {
        if (surveyLog == null) {
            throw new BaseException(ErrorCode.BAD_REQUEST_ERROR);
        }
        List<Answer> answers = answerRepository.findAnswersBySurveyLog(surveyLog);
        return answers == null ? Collections.emptyList() : answers;
    }

    public String getAnswerFromMultiple(Answer answer) {
        List<QuestionOption> options = getOptions(answer);
        log.info("options:{}", options.toString());
        if (answer.getAnswerType().equals(QuestionType.MULTIPLECHECK)) {
            List<String> targetOptions = Arrays.stream(answer.getMultiAnswer().replaceAll("[\\[\\]]", "").split(",\\s*"))
                    .map(s -> s.split("_", 2)[1])
                    .toList();
            return String.join(",", targetOptions);
        } else if (answer.getAnswerType().equals(QuestionType.MULTIPLE)) {
            return answer.getMultiAnswer().split("_", 2)[1];
        }
        return answer.getAnswer();
    }

    private <T> T requireNonNull(T value, ErrorCode errorCode) {
        if (value == null) {
            throw new BaseException(errorCode);
        }
        return value;
    }
}