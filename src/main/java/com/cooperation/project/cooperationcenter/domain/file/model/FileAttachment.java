package com.cooperation.project.cooperationcenter.domain.file.model;

import com.cooperation.project.cooperationcenter.domain.school.model.IntroPost;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import com.cooperation.project.cooperationcenter.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "file_attachment")
@SQLDelete(sql = "UPDATE file_attachment SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class FileAttachment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) private String fileId;
    private String originalName;
    private String storedName;
    private String path;
    private double size;
    private String contentType;

    @Transient private String storedPath;
    @Enumerated(EnumType.STRING) private FileTargetType filetype;
    @Enumerated(EnumType.STRING) private ContentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_post_id", nullable = true)
    private SchoolPost schoolPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intro_post_id", nullable = true)
    private IntroPost introPost;

    @Getter
    public enum ContentType{
        IMG("IMG"),
        FILE("FILE");

        private final String type;

        ContentType(String type){
            this.type = type;
        }

        public static ContentType fromType(String fileType){
            return Arrays.stream(ContentType.values())
                    .filter(type -> type.getType().equalsIgnoreCase(fileType))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown fileType: " + fileType));
        }

    }

    public void addPost(SchoolPost schoolPost) {
        this.schoolPost = schoolPost;
    }

    @Builder
    public FileAttachment(String path, String storedPath, MultipartFile file,FileTargetType filetype){

        String safeName = java.util.Objects.requireNonNullElse(file.getOriginalFilename(), "file");
        safeName = safeName.replaceAll("[\\r\\n]", "");

        LocalDate now = LocalDate.now();
        this.fileId = UUID.randomUUID().toString();
        this.originalName = safeName;

        this.storedName = this.fileId+"_"+originalName+"_"+now;
        this.path = path+"/"+this.storedName;
        this.size = file.getSize(); //byte 단위임 -> kb하려면 /1024

        this.storedPath = storedPath+"/"+this.storedName;
        this.filetype = filetype;
        this.contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            this.type = ContentType.IMG;
        }else{
            this.type = ContentType.FILE;
        }

    }
}
