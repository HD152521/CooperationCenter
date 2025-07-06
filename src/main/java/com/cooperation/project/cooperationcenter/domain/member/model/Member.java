package com.cooperation.project.cooperationcenter.domain.member.model;


import com.cooperation.project.cooperationcenter.domain.file.model.MemberFile;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "member")
@Builder
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Member extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull private String memberName;     //실명
    @NotNull private String email;          //이메일
    @NotNull private String password;
    @NotNull private String homePhoneNumber;
    @NotNull private String phoneNumber;
    @NotNull private String address1;
    @NotNull private String address2;

    @NotNull private String agencyName;
    @NotNull private String agencyAddress1;
    @NotNull private String agencyAddress2;
    @NotNull private String agencyPhone;
//    @NotNull private enum agencyRegion;  fixme 추가해야함.

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "agency_picture_id")
    private MemberFile agencyPicture;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "business_certificate_id")
    private MemberFile businessCertificate;


    @NotNull private String memberId;
    @NotNull private Role role;
    @NotNull private boolean isApprovalSignup;
    @OneToMany(mappedBy = "member")
    private List<SurveyLog> surveyLogs = new ArrayList<>();

    public void accept(){
        this.isApprovalSignup = true;
    }

    public enum Role {
        USER("USER"),
        ADMIN("ADMIN");

        private final String role;

        Role(String role) {
            this.role = role;
        }
    }

    public static Member fromDto(
            MemberRequest.SignupDto dto,
            MemberFile agencyPicture,
            MemberFile businessCertificate
    ) {
        return Member.builder()
                .memberName(dto.memberName())
                .email(dto.email())
                .password(dto.password())
                .homePhoneNumber(dto.homePhoneNumber())
                .phoneNumber(dto.phoneNumber())
                .address1(dto.address1())
                .address2(dto.address2())
                .agencyName(dto.agencyName())
                .agencyAddress1(dto.agencyAddress1())
                .agencyAddress2(dto.agencyAddress2())
                .agencyPicture(agencyPicture)
                .businessCertificate(businessCertificate)
                .agencyPhone(dto.agencyPhone())

                .memberId(UUID.randomUUID().toString())
                .role(Role.USER) // 기본값
                .isApprovalSignup(false) // 가입 승인 대기 상태
                .build();
    }

}
