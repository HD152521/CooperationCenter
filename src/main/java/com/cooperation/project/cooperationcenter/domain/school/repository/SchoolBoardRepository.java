package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.School;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolBoardRepository extends JpaRepository<SchoolBoard,Long> {
    List<SchoolBoard> findBySchool(School school);
    Optional<SchoolBoard> findSchoolBoardById(Long id);
}
