package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.global.token.vo.TokenResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MemberResponse {
    public record LoginDto(
            String email,
            String memberName,
            boolean isAdmin){
        public static LoginDto from(Member member){
            return new LoginDto(
                    member.getEmail(),
                    member.getMemberName(),
                    member.getRole().equals(Member.Role.ADMIN)
            );
        }
    }


    //admin 홈 승인대기
    public record PendingDto(
            String memberName,
            String memberEmail,
            LocalDateTime createdAt

    ){
        public static PendingDto from(Member member){
            return new PendingDto(
                    member.getMemberName(),
                    member.getEmail(),
                    member.getCreatedAt()
            );
        }

        public static List<PendingDto> from(List<Member> member){
            return member.stream()
                    .map(PendingDto::from)
                    .collect(Collectors.toList());
        }
    }

    //admin 유저 페이지
    public record UserPageDto(
            long totalUser,
            long activeUser,
            long newUser,
            long pendingUser,
            Page<UserDto> users
    ){}

    public record UserDto(
            String userName,
            String userEmail,
            LocalDateTime userCreatedAt,
            LocalDate userApprovedAt,
            boolean isApproval
    ){
        public static UserDto from(Member member){
            return new UserDto(
                    member.getMemberName(),
                    member.getEmail(),
                    member.getCreatedAt(),
                    member.getApprovedDate(),
                    member.isApprovalSignup()
            );
        }

        public static List<UserDto> from(List<Member> members){
            return members.stream()
                    .map(UserDto::from)
                    .collect(Collectors.toList());
        }

        public static Page<UserDto> from(Page<Member> members){
            return null;
        }
    }

    public record DetailDto(
            String agencyName,
            String email,
            String businessNumber,
            LocalDateTime signupDate,
            String ownerTel,
            String ownerName,
            String address,
            String businessCertificationURL,
            String businessCertificationDownloadURL,
            String agencyTel,
            String agencyAddress,
            String businessCertificationType
    ){
        public static DetailDto from(Member member){
            Agency agency = member.getAgency();
            FileAttachment file = agency.getBusinessPicture();
            String type=null;
            String url = "/api/v1/file/";
            String viewUrl = "/api/v1/file/";
            if(file.getType()!=null){
                type = file.getType().toString();
                if(type.equals(FileAttachment.ContentType.IMG.getType())) viewUrl+="img/";
                else if(type.equals(FileAttachment.ContentType.FILE.getType())) viewUrl+="pdf/";
            }
            url+="member/"+agency.getBusinessPicture().getFileId();
            viewUrl+="member/"+agency.getBusinessPicture().getFileId();
            return new DetailDto(
                    agency.getAgencyName(),
                    member.getEmail(),
                    null,
                    member.getCreatedAt(),
                    member.getPhoneNumber(),
                    member.getMemberName(),
                    member.getAddress1(),
                    viewUrl,
                    url,
                    agency.getAgencyPhone(),
                    agency.getAgencyAddress1(),
                    type
            );
        }
    }
}
