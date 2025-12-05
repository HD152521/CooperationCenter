package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.school.dto.CollegeDegreeType;
import com.cooperation.project.cooperationcenter.domain.school.dto.IntroRequest;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "college")
@Builder
@SQLDelete(sql = "UPDATE college SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class College extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "intro_post_id", nullable = false)
    private IntroPost introPost;

    private String departments;

    private String collegeName;

    @Enumerated(EnumType.STRING)
    private CollegeDegreeType type;

    public List<String> getDepartments(){
        return Arrays.stream(departments.split("_")).toList();
    }

    public void updateFromDto(IntroRequest.CollegeSaveDto dto){
        this.collegeName = dto.name();
        this.departments = dto.departments();
        this.type = CollegeDegreeType.valueOf(dto.type());
    }
}
