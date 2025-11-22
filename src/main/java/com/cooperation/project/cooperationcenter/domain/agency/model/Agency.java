package com.cooperation.project.cooperationcenter.domain.agency.model;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.member.dto.AgencyRegion;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberRequest;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "agency")
@Builder
@SQLDelete(sql = "UPDATE agency SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Agency extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String agencyName;
    @NotNull private String agencyAddress1;
    @NotNull private String agencyAddress2;
    @NotNull private String agencyPhone;
    @NotNull private String agencyOwner;
    @NotNull private AgencyRegion agencyRegion;
    @NotNull private String agencyEmail;
    @NotNull boolean share;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "agency_picture_id")
    private FileAttachment agencyPicture;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "business_picture_id")
    private FileAttachment businessPicture;

    @OneToMany(mappedBy = "agency")
    private List<Member> member = new ArrayList<>();

    @OneToMany(mappedBy = "agency")
    private List<Student> students = new ArrayList<>();

    public void updateAgencyPicture(FileAttachment file){
        this.agencyPicture = file;
    }
    public void updateBusinessCertificate(FileAttachment file) {this.businessPicture = file;}
    public void updateFiles(FileAttachment agencyPicture,FileAttachment businessCertificate){
        this.agencyPicture = agencyPicture;
        this.businessPicture = businessCertificate;
    }

    public void addMember(Member member){
        this.member.add(member);
    }

    public void removeMember(Member member){
        this.member.remove(member);
    }

    public void setShare(){
        this.share = !this.share;
    }

    public static Agency fromDto(
            MemberRequest.SignupNewAgencyDto dto
    ) {
        AgencyRegion region = AgencyRegion.fromLabel(dto.agencyRegion());
        return Agency.builder()
                .agencyName(dto.agencyName())
                .agencyAddress1(dto.agencyAddress1())
                .agencyAddress2(dto.agencyAddress2())
                .agencyPhone(dto.agencyPhone())
                .agencyOwner(dto.agencyOwner())
                .agencyRegion(region)
                .agencyEmail(dto.agencyEmail())
                .share(false)
                .build();
    }

    public void updateAgency(Profile.MemberDto dto){
        this.agencyName = dto.agencyName();
        this.agencyAddress1 = dto.agencyAddress1();
        this.agencyAddress2 = dto.agencyAddress2();
        this.agencyPhone = dto.agencyPhone();
        this.agencyOwner = dto.agencyOwner();
        this.agencyRegion = AgencyRegion.fromLabel(dto.agencyRegion());
        this.agencyEmail = dto.agencyEmail();
    }
}
