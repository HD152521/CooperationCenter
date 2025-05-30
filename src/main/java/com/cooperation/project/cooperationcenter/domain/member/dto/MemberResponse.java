package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.cooperation.project.cooperationcenter.global.token.vo.TokenResponse;

public class MemberResponse {
    public record LoginDto(
            String email,
            String password){}
}
