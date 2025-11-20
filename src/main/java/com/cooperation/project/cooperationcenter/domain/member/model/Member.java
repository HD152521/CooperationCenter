package com.cooperation.project.cooperationcenter.domain.member.model;


import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.member.dto.AgencyRegion;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyFolder;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
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
    @NotNull private LocalDate birth;
    @NotNull private boolean existingAgency;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "agency_id")
    private Agency agency;

    @NotNull private String memberId;
    @NotNull private Role role;
    @Column(name = "is_approval_signup") @NotNull private boolean approvalSignup;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private LocalDate approvedDate;

    @OneToMany(mappedBy = "member")
    private List<SurveyLog> surveyLogs = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<SurveyFolder> folders = new ArrayList<>();

    public void accept(){
        this.approvalSignup = true;
        this.status = UserStatus.APPROVED;
        this.approvedDate = LocalDate.now();
    }

    public void pending(){
        this.approvalSignup = false;
        this.status = UserStatus.PENDING;
        this.approvedDate = null;
    }

    public boolean isAccept(){
        return (this.status.equals(UserStatus.APPROVED))||isApprovalSignup();
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void setAgency(Agency agency){
        this.agency = agency;
    }

    public enum Role {
        USER("USER"),
        ADMIN("ADMIN");

        private final String role;

        Role(String role) {
            this.role = role;
        }

        public boolean isAdmin(){
            return this.equals(Role.ADMIN);
        }
    }

    public static Member fromDto(
            MemberRequest.SignupMemberDto dto,
            Agency agency,
            String uuid
    ) {
        return Member.builder()
                .memberName(dto.memberName())
                .email(dto.email())
                .password(dto.password())
                .homePhoneNumber(dto.homePhoneNumber())
                .phoneNumber(dto.phoneNumber())
                .address1(dto.address1())
                .address2(dto.address2())
                .birth(LocalDate.parse(dto.birth()))
                .agency(agency)
                .memberId(uuid)
                .role(Role.USER) // 기본값
                .approvalSignup(false) // 가입 승인 대기 상태
                .status(UserStatus.PENDING)
                .build();
    }

    public void updateMember(Profile.MemberDto dto){
        this.memberName = dto.memberName();
        this.birth = dto.birth();
        this.email = dto.email();
        this.phoneNumber = dto.phoneNumber();
        this.homePhoneNumber = dto.homePhoneNumber();
        this.address1 = dto.address1();
        this.address2 = dto.address2();
    }

}
