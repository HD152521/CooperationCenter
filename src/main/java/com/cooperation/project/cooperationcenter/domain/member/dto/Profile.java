package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;

import java.time.LocalDate;
import java.util.List;

public class Profile{
    public record ProfileDto(
        MemberDto member,
        List<SurveyDto> surveys
    ){}

    public record MemberDto(
            String memberName,
            LocalDate birth,
            String email,
            String homePhoneNumber,
            String phoneNumber,
            String address,
            String agencyOwner,
            String agencyName,
            String agencyPhone,
            String agencyAddress
    ){
        public static MemberDto from(Member member){
            return new MemberDto(
                    member.getMemberName(),
                    member.getBirth(),
                    member.getEmail(),
                    member.getHomePhoneNumber(),
                    member.getPhoneNumber(),
                    member.getAddress1()+" "+member.getAddress2(),
                    member.getAgencyOwner(),
                    member.getAgencyName(),
                    member.getAgencyPhone(),
                    member.getAgencyAddress1()+" "+member.getAgencyAddress2()
            );
        }
    }

    public record SurveyDto(
            String title,
            String description,
            LocalDate submitTime,
            String logUrl
    ){
        public static SurveyDto from(SurveyLog log) {
            return new SurveyDto(
                    log.getSurvey().getSurveyTitle(),
                    log.getSurvey().getSurveyDescription(),
                    log.getCreatedAt().toLocalDate(),
                    "/survey/log/detail/" + log.getSurveyLogId()
            );
        }

        public static List<SurveyDto> from(List<SurveyLog> logs){
            return logs.stream().map(SurveyDto::from).toList();
        }
    }

}
