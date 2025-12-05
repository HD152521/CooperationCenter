package com.cooperation.project.cooperationcenter.domain.member.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class MemberRequest {

    public record SignupMemberDto(
            @NotNull String memberName,             // 실명
            @NotNull String email,                  // 이메일
            @NotNull String password,
            @NotNull String birth,
            @NotNull String homePhoneNumber,
            @NotNull String phoneNumber,
            @NotNull String address1,
            @NotNull String address2,
            @NotNull boolean isExistingAgency
    ){
        public SignupMemberDto withEncodedPassword(String encodedPassword) {
            return new SignupMemberDto(
                    this.memberName,
                    this.email,
                    encodedPassword, // 여기에 암호화된 비밀번호 주입,
                    this.birth,
                    this.homePhoneNumber,
                    this.phoneNumber,
                    this.address1,
                    this.address2,
                    this.isExistingAgency
            );
        }

        public static SignupMemberDto from(String data){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(data, SignupMemberDto.class);
            }catch (Exception e){
                log.warn("member mapping중 오류 : {}",e.getMessage());
                return null;
            }
        }
    }

    public record SignupNewAgencyDto(
            @NotNull String agencyOwner,
            @NotNull String agencyName,
            @NotNull String agencyAddress1,
            @NotNull String agencyAddress2,
            @NotNull String agencyPhone,
            @NotNull String agencyRegion,
            @NotNull String agencyEmail
    ){
        public static SignupNewAgencyDto from(String data){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(data,SignupNewAgencyDto.class);
            }catch (Exception e){
                log.warn("member mapping중 오류 : {}",e.getMessage());
                return null;
            }
        }
    }

    public record SignupExistingAgencyDto(
            @NotNull String agencyName,
            @NotNull String agencyEmail
    ){
        public static SignupExistingAgencyDto from(String data){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(data,SignupExistingAgencyDto.class);
            }catch (Exception e){
                log.warn("member mapping중 오류 : {}",e.getMessage());
                return null;
            }
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
