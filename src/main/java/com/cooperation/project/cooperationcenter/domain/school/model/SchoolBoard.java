package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "school_board")
@Builder
@SQLDelete(sql = "UPDATE school_board SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SchoolBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String boardTitle;
    private String realTitle;
    private String boardDescription;
    @Enumerated(EnumType.STRING) private BoardType type;

    @ManyToOne(fetch = FetchType.LAZY)
    private School school;
    @OneToMany(mappedBy = "school_board")
    private List<SchoolPost> posts = new ArrayList<>();


    @Getter
    public enum BoardType {
        INTRO("INTRO"),
        NOTICE("NOTICE");

        private final String type;

        BoardType(String type) {
            this.type = type;
        }
    }
}
