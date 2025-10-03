package com.cooperation.project.cooperationcenter.domain.survey.repository;

import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyFolder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyFolderRepository extends JpaRepository<SurveyFolder,Long> {
    Optional<SurveyFolder> findByFolderId(String folderId);
}
