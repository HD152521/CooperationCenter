package com.cooperation.project.cooperationcenter.domain.file.model;

import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
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
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE file SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class SurveyFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) private String fileId;
    private String fileRealName;
    private String fileName;
    private String filePath;
    private double fileSize;

    @Transient private Path realPath;
    private FileType fileType;

    @Builder
    public SurveyFile(String filePath, Path realPath, FileType fileType, MultipartFile file){
        this.fileId = UUID.randomUUID().toString();
        this.fileRealName = file.getOriginalFilename();
        this.fileName = this.fileId+"_"+fileRealName;
        this.filePath = filePath;
        this.fileSize = file.getSize(); //byte 단위임 -> kb하려면 /1024
        this.realPath = realPath;
        this.fileType = fileType;
    }
}
