package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.global.token.vo.TokenResponse;

public class MemberResponse {
    public record LoginDto(
            String email,
            String memberName){
        public static LoginDto from(Member member){
            return new LoginDto(
                    member.getEmail(),
                    member.getMemberName()
            );
        }
    }
}
