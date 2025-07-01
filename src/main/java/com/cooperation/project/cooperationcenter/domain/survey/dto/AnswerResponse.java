package com.cooperation.project.cooperationcenter.domain.survey.dto;

import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnswerResponse {
    public record AnswerDto(
            String surveyId,
            String surveyTitle,
            LocalDate surveyStartDate,
            LocalDate surveyEndDate,
            int participantCnt,
            double finishPercent,
            int avgSpendTime,
            List<LogDto> logs
    ){
      public static AnswerDto from(Survey survey, List<LogDto> dtos){
          if(dtos.isEmpty())
              return new AnswerDto(
                  survey.getSurveyId(),
                  survey.getSurveyTitle(),
                  survey.getStartDate(),
                  survey.getEndDate(),
                  0,
                  0,
                  0,
                  null
          );
          int finishCnt=0;
          int totalSpendTime=0;
          int total = dtos.size();
          for(LogDto log : dtos){
              if(log.finishStatus.equals("finish"))finishCnt++;
              totalSpendTime+=log.spendTime;
          }

          return new AnswerDto(
            survey.getSurveyId(),
                  survey.getSurveyTitle(),
            survey.getStartDate(),
            survey.getEndDate(),
            total,
            (double) finishCnt /total*100,
                  totalSpendTime/total,
                  dtos
          );
      }
    }

    public record LogDto(
            String memberName,
            String memberEmail,
            String submitTime,
            Long spendTime,
            String finishStatus,
            String logId

    ){
        public static LogDto from(SurveyLog surveyLog){
            long diffInSeconds = Duration.between(surveyLog.getStartTime(), surveyLog.getCreatedAt()).getSeconds();
            return new LogDto(
                    surveyLog.getMember().getMemberName(),
                    surveyLog.getMember().getEmail(),
                    surveyLog.getCreatedAt().toString(),
                    diffInSeconds,
                    "finish",
                    surveyLog.getSurveyLogId()
            );
        }

        public static List<LogDto> from(List<SurveyLog> surveyLogs){
            List<LogDto> dtos = new ArrayList<>();
            for(SurveyLog s : surveyLogs) dtos.add(from(s));
            return dtos;
        }
    }

    public record AnswerLogDto(
            String surveyId,
            String surveyTitle,
            LocalDate surveyStartDate,
            LocalDate surveyEndDate,
            List<QuestionDto> questions,
            List<AnswerDetailDto> answer,
            LogDto logDto
    ){
        public static AnswerLogDto from(Survey survey, SurveyLog surveyLog,List<Answer> answers){
            return new AnswerLogDto(
                    survey.getSurveyId(),
                    survey.getSurveyTitle(),
                    survey.getStartDate(),
                    survey.getEndDate(),
                    QuestionDto.to(survey),
                    AnswerDetailDto.from(answers),
                    LogDto.from(surveyLog)
            );
        }
    }

    public record AnswerDetailDto(
            String type,
            String answer
    ){
        public static List<AnswerDetailDto> from(List<Answer> answers){
            List<AnswerDetailDto> response = new ArrayList<>();
            for(Answer an : answers) response.add(AnswerDetailDto.from(an));
            return response;
        }

        public static AnswerDetailDto from(Answer answer){
            return new AnswerDetailDto(
                answer.getAnswerType().getType(),
                    answer.getAnswer()
            );
        }
    }
}
