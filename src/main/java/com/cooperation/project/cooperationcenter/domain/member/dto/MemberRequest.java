package com.cooperation.project.cooperationcenter.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class MemberRequest {

    public record SignupDto(
            @NotNull String memberName,             // 실명
            @NotNull String email,                  // 이메일
            @NotNull String password,
            @NotNull String homePhoneNumber,
            @NotNull String phoneNumber,
            @NotNull String address1,
            @NotNull String address2,

            @NotNull String agencyName,
            @NotNull String agencyAddress1,
            @NotNull String agencyAddress2
    ){
        public SignupDto withEncodedPassword(String encodedPassword) {
            return new SignupDto(
                    this.memberName,
                    this.email,
                    encodedPassword, // 여기에 암호화된 비밀번호 주입
                    this.homePhoneNumber,
                    this.phoneNumber,
                    this.address1,
                    this.address2,
                    this.agencyName,
                    this.agencyAddress1,
                    this.agencyAddress2
            );
        }
    }

    public record LoginDto(
            String email,
            String password
    ){}
}
