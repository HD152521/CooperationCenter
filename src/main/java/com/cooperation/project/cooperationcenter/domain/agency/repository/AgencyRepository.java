package com.cooperation.project.cooperationcenter.domain.agency.repository;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency,Long> {
    long countByAgencyPicture(FileAttachment file);
}
