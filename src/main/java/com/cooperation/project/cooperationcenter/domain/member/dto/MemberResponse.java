package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.global.token.vo.TokenResponse;

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
}
