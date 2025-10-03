package com.cooperation.project.cooperationcenter.domain.school.model;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.school.dto.PostStatus;
import com.cooperation.project.cooperationcenter.domain.school.dto.PostType;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "file_post")
@Builder
@SQLDelete(sql = "UPDATE file_post SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class FilePost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postTitle;
    private String description;

    @Enumerated(EnumType.STRING) private PostStatus status;
    @Enumerated(EnumType.STRING) private PostType type;

    private int downloads;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_board")
    private SchoolBoard schoolBoard;

    @OneToOne
    private FileAttachment file;

    public void setBoard(SchoolBoard schoolBoard) {
        this.schoolBoard = schoolBoard;
    }

    public void setFile(FileAttachment file){
        this.file = file;
    }

    public void deleteFile(){
        this.file = null;
    }

    public static FilePost fromDto(SchoolRequest.FilePostDto dto){
        PostStatus status = PostStatus.from(dto.status());
        PostType postType = PostType.from(dto.type());

        return FilePost.builder()
                .postTitle(dto.title())
                .status(status)
                .type(postType)
                .build();
    }

    public void updateFormDto(SchoolRequest.FilePostDto dto){
        this.postTitle = dto.title();
        this.status = PostStatus.from(dto.status());
        this.type = PostType.from(dto.type());
    }

}
