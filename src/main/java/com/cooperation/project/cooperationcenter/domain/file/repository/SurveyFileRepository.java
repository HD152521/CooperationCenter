package com.cooperation.project.cooperationcenter.domain.file.repository;

import com.cooperation.project.cooperationcenter.domain.file.model.SurveyFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyFileRepository extends JpaRepository<SurveyFile, Long> {
    Optional<SurveyFile> findSurveyFileByFileId(String fileId);

}
