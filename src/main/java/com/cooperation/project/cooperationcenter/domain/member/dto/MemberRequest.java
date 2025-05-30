package com.cooperation.project.cooperationcenter.domain.member.dto;

public class MemberRequest {

    public record SignupDto(
            String fullName,
            String email,
            String password,
            String retypePassword,
            String terms
    ){}

    public record LoginDto(
            String email,
            String password,
            String terms
    ){}
}
