package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School,Long> {
    Optional<School> findSchoolById(Long id);
    Optional<School> findSchoolBySchoolEnglishName(String englishName);
}
