package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
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
@Table(name = "intro_post")
@Builder
@SQLDelete(sql = "UPDATE intro_post SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class IntroPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Lob
    @Column(columnDefinition = "LONGTEXT") // 또는 "TEXT"
    private String content;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_board")
    private SchoolBoard schoolBoard;

    @OneToMany(mappedBy = "schoolPost")
    private List<FileAttachment> files = new ArrayList<>();

    public void setBoard(SchoolBoard board){
        this.schoolBoard = board;
    }

    public void deleteBoard(){
        this.schoolBoard.deleteIntroPost(this);
    }

    public static SchoolPost fromDto(SchoolRequest.IntroDto dto){
        return SchoolPost.builder()
                .postTitle(dto.title())
                .content(dto.content())
                .files(new ArrayList<>())
                .build();
    }
}
