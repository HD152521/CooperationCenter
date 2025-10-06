package com.cooperation.project.cooperationcenter.domain.survey.model;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyFolderDto;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE survey_folder SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SurveyFolder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member owner;

    private String displayName;
    private String storedName;

    private String folderId;

    @OneToMany(mappedBy = "surveyFolder",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = false)
    private final List<Survey> surveys = new ArrayList<>();

    public void addSurvey(Survey survey){
        if(!surveys.contains(survey)) this.surveys.add(survey);
    }

    public void deleteSurvey(Survey survey){
        if(surveys.contains(survey)) this.surveys.remove(survey);
    }

    @Builder
    public SurveyFolder(String displayName, String storedName, String folderId, Member owner) {
        this.displayName = displayName;
        this.storedName = storedName;
        this.folderId = folderId;
        this.owner = owner;
    }

    public static SurveyFolder from(SurveyFolderDto dto, Member member) {
        return SurveyFolder.builder()
                .displayName(dto.displayName())
                .storedName(dto.storedName())
                .folderId(UUID.randomUUID().toString())
                .owner(member)
                .build();
    }

    public void updateFromDto(SurveyFolderDto dto){
        this.displayName = dto.displayName();
        this.storedName = dto.storedName();
    }
}
