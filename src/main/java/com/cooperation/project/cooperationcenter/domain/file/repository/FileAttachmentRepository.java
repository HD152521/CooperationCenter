package com.cooperation.project.cooperationcenter.domain.file.repository;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.model.FileTargetType;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileAttachmentRepository extends JpaRepository<FileAttachment,Long> {
    Optional<FileAttachment> findByFileIdAndFiletype(String fileId, FileTargetType fileTargetType);
    List<FileAttachment> findFileAttachmentsBySchoolPost(SchoolPost schoolPost);
}
