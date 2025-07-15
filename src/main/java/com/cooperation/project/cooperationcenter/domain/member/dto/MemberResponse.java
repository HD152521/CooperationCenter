package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.global.token.vo.TokenResponse;
import org.springframework.data.domain.Page;

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
            LocalDateTime userLastLogin,
            boolean isApproval
    ){
        public static UserDto from(Member member){
            return new UserDto(
                    member.getMemberName(),
                    member.getEmail(),
                    member.getCreatedAt(),
                    null,
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
            String businessCertificationURl,
            String agencyTel,
            String agencyAddress
    ){
        public static DetailDto from(Member member){
            return new DetailDto(
                    member.getAgencyName(),
                    member.getEmail(),
                    null,
                    member.getCreatedAt(),
                    member.getPhoneNumber(),
                    member.getMemberName(),
                    member.getAddress1(),
                    "/api/v1/file/img/"+member.getBusinessCertificate().getFileId(),
                    member.getAgencyPhone(),
                    member.getAgencyAddress1()
            );
        }
    }
}
