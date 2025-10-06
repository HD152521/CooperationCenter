package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Profile{
    public record ProfileDto(
        MemberDto member,
        Page<SurveyDto> surveys,
        MemberFileDto businessCertification,
        MemberFileDto agencyPicture
    ){}

    public record MemberDto(
            String memberName,
            LocalDate birth,
            String email,
            String homePhoneNumber,
            String phoneNumber,
            String address1,
            String address2,
            String agencyOwner,
            String agencyName,
            String agencyPhone,
            String agencyRegion,
            String agencyEmail,
            String agencyAddress1,
            String agencyAddress2
    ){
        public static MemberDto from(Member member){
            return new MemberDto(
                    member.getMemberName(),
                    member.getBirth(),
                    member.getEmail(),
                    member.getHomePhoneNumber(),
                    member.getPhoneNumber(),
                    member.getAddress1(),
                    member.getAddress2(),
                    member.getAgencyOwner(),
                    member.getAgencyName(),
                    member.getAgencyPhone(),
                    member.getAgencyRegion().getLabel(),
                    member.getAgencyEmail(),
                    member.getAgencyAddress1(),
                    member.getAgencyAddress2()
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

        public static Page<SurveyDto> from(Page<SurveyLog> logs){
            return logs.map(SurveyDto::from);
        }
    }

    public record MemberFileDto(
            Long id,
            String fileName,
            String fileUrl,       // S3/서버 정적주소
            String fileViewUrl,       // S3/서버 정적주소
            String contentType,   // image/png, application/pdf
            double size,
            LocalDateTime uploadedAt
    ){
        public static MemberFileDto from(FileAttachment file){
            String url = "/api/v1/file/";
            String viewUrl = "/api/v1/file/";
            String contentType = "";
            if(file.getType()!=null){
                String type = file.getType().toString();
                if(type.equals(FileAttachment.ContentType.IMG.getType())){
                    viewUrl+="img/";
                    contentType = "image/png";
                }
                else if(type.equals(FileAttachment.ContentType.FILE.getType())){
                    viewUrl+="pdf/";
                    contentType = "application/pdf";
                }
            }
            url+="member/"+file.getFileId();
            viewUrl+="member/"+file.getFileId();

            return new MemberFileDto(
                    file.getId(),
                    file.getOriginalName(),
                    url,
                    viewUrl,
                    contentType,
                    file.getSize(),
                    file.getUpdatedAt()
            );
        }
    }

}
