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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "school_post")
@Builder
@SQLDelete(sql = "UPDATE school_post SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SchoolPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String postTitle;
    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT") // 또는 "TEXT"
    private String content;

    @Enumerated(EnumType.STRING) private PostStatus status;
    @Enumerated(EnumType.STRING) private PostType type;

    private int views;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_board")
    private SchoolBoard schoolBoard;

    @OneToMany(mappedBy = "schoolPost")
    private List<FileAttachment> files = new ArrayList<>();

    public void setBoard(SchoolBoard board){
        this.schoolBoard = board;
    }

    public void deleteBoard(){
        this.schoolBoard.deletePost(this);
    }

    public void addView(){
        this.views++;
    }

    public void addFile(List<FileAttachment> files){
        files.forEach(file -> file.addPost(this));
        this.files.addAll(files);
    }

    public void deleteFile(FileAttachment file) {
        this.files.remove(file);
        file.addPost(null); // 연관 관계 정리
    }

    public void deleteFile(){
        this.files.forEach(file->file.addPost(null));
        this.files.clear();
    }

    public static SchoolPost fromDto(SchoolRequest.SchoolPostDto dto){
        PostStatus status = PostStatus.from(dto.status());
        PostType postType = PostType.from(dto.type());
        return SchoolPost.builder()
                .postTitle(dto.title())
                .description(dto.description())
                .content(dto.content())
                .status(status)
                .type(postType)
                .files(new ArrayList<>())
                .build();
    }

    public void updateFromDto(SchoolRequest.SchoolPostDto dto) {
        this.postTitle = dto.title();
        this.description = dto.description();
        this.content = dto.content();
        this.status = PostStatus.from(dto.status());
        this.type = PostType.from(dto.type());
    }
}
