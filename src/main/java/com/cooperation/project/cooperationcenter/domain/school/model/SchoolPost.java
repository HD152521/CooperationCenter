package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
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
@Table(name = "school_post")
@Builder
@SQLDelete(sql = "UPDATE school_board SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SchoolPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postTitle;
    private String description;
    private String content;

    @Enumerated(EnumType.STRING) private PostStatus status;
    @Enumerated(EnumType.STRING) private PostType type;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_board")
    private SchoolBoard schoolBoard;

    @OneToMany(mappedBy = "schoolPost")
    private List<FileAttachment> files = new ArrayList<>();

    @Getter
    public enum PostType {
        NORMAL("NORMAL"),
        NOTICE("NOTICE");
        private final String type;
        PostType(String type) {this.type = type;}
    }
}
