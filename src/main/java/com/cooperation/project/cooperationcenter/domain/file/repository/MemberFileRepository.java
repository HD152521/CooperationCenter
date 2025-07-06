package com.cooperation.project.cooperationcenter.domain.file.repository;

import com.cooperation.project.cooperationcenter.domain.file.model.MemberFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberFileRepository extends JpaRepository<MemberFile,Long> {
    Optional<MemberFile> findSurveyFileByFileId(String fileId);
}
