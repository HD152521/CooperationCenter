package com.cooperation.project.cooperationcenter.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class MemberRequest {

    public record SignupDto(
            @NotNull String memberName,             // 실명
            @NotNull String email,                  // 이메일
            @NotNull String password,
            @NotNull String checkPassword,
            @NotNull String homePhoneNumber,
            @NotNull String phoneNumber,
            @NotNull String address1,
            @NotNull String address2,

            @NotNull String agencyName,
            @NotNull String agencyAddress1,
            @NotNull String agencyAddress2,

            MultipartFile agencyPicture,            // 업로드된 사진
            MultipartFile businessCertificate       // 업로드된 사업자등록증
    ){}

    public record LoginDto(
            String email,
            String password
    ){}
}
