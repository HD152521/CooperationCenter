package com.cooperation.project.cooperationcenter.domain.survey.dto;

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
            String finishStatus
    ){
        public static LogDto from(SurveyLog surveyLog){
            long diffInSeconds = Duration.between(surveyLog.getStartTime(), surveyLog.getCreatedAt()).getSeconds();
            return new LogDto(
                    surveyLog.getMember().getMemberName(),
                    surveyLog.getMember().getEmail(),
                    surveyLog.getCreatedAt().toString(),
                    diffInSeconds,
                    "finish"
            );
        }

        public static List<LogDto> from(List<SurveyLog> surveyLogs){
            List<LogDto> dtos = new ArrayList<>();
            for(SurveyLog s : surveyLogs) dtos.add(from(s));
            return dtos;
        }
    }
}
