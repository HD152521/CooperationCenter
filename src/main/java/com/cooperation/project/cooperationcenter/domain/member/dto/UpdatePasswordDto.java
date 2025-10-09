package com.cooperation.project.cooperationcenter.domain.member.dto;

public class UpdatePasswordDto {
    public record CheckEmailDto(
            String email,
            String name
    ){}

    public record PasswordCheckDto(
            String newPassword,
            String resetToken
    ){}
}
