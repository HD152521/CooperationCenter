package com.cooperation.project.cooperationcenter.domain.member.model;


import com.cooperation.project.cooperationcenter.domain.file.model.MemberFile;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "member")
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
    @NotNull private String Address1;
    @NotNull private String Address2;

    @NotNull private String agencyName;
    @NotNull private String agencyAddress1;
    @NotNull private String agencyAddress2;
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

    public enum Role{
        USER("USER"),
        ADMIN("ADMIN");

        Role(String role){}
        private String role;
    }

    @Builder
    public Member(){

        this.memberId = UUID.randomUUID().toString();
        this.role = Role.USER;
        this.isApprovalSignup = false;
    }
}
