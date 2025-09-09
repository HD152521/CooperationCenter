package com.cooperation.project.cooperationcenter.domain.agency.model;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.member.dto.AgencyRegion;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "agency_picture_id")
    private FileAttachment agencyPicture;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Agency fromMember(
            Member member
    ) {
        FileAttachment file = (member.getAgencyPicture()!=null)?member.getAgencyPicture():null;
        return Agency.builder()
                .agencyName(member.getAgencyName())
                .agencyAddress1(member.getAgencyAddress1())
                .agencyAddress2(member.getAgencyAddress2())
                .agencyPicture(file)
                .agencyPhone(member.getAgencyPhone())
                .agencyOwner(member.getAgencyOwner())
                .member(member)
                .agencyRegion(member.getAgencyRegion())
                .agencyEmail(member.getAgencyEmail())
                .build();
    }
}
