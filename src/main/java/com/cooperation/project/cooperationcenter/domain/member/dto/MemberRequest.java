package com.cooperation.project.cooperationcenter.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class MemberRequest {

    public record SignupDto(
            @NotNull String memberName,             // 실명
            @NotNull String email,                  // 이메일
            @NotNull String password,
            @NotNull String birth,
            @NotNull String homePhoneNumber,
            @NotNull String phoneNumber,
            @NotNull String address1,
            @NotNull String address2,

            @NotNull String agencyOwner,
            @NotNull String agencyName,
            @NotNull String agencyAddress1,
            @NotNull String agencyAddress2,
            @NotNull String agencyPhone
    ){
        public SignupDto withEncodedPassword(String encodedPassword) {
            return new SignupDto(
                    this.memberName,
                    this.email,
                    encodedPassword, // 여기에 암호화된 비밀번호 주입,
                    this.birth,
                    this.homePhoneNumber,
                    this.phoneNumber,
                    this.address1,
                    this.address2,
                    this.agencyOwner,
                    this.agencyName,
                    this.agencyAddress1,
                    this.agencyAddress2,
                    this.agencyPhone
            );
        }
    }

    public record LoginDto(
            String email,
            String password
    ){}

    public record UserFilterDto(
            String keyword,
            String status,
            String date
    ){}
}
