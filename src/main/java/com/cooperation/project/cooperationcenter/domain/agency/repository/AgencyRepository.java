package com.cooperation.project.cooperationcenter.domain.agency.repository;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgencyRepository extends JpaRepository<Agency,Long> {
    long countByAgencyPicture(FileAttachment file);
    Optional<Agency> findAgencyByAgencyNameAndAgencyEmail(String name, String email);
    List<Agency> findAgenciesByShare(boolean share);
    Page<Agency> findAgenciesByShare(boolean share, Pageable pageable);

}
