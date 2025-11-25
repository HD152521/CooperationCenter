package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.school.dto.CollegeDegreeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "college")
@Builder
@SQLDelete(sql = "UPDATE college SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "intro_post_id", nullable = false)
    private IntroPost introPost;

    @OneToMany(mappedBy = "college")
    private List<Department> departments;

    private String collegeName;

    @Enumerated(EnumType.STRING)
    private CollegeDegreeType type;


}
