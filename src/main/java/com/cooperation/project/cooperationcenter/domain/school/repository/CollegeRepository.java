package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.College;
import com.cooperation.project.cooperationcenter.domain.school.model.IntroPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {
    List<College> findCollegesByIntroPost(IntroPost introPost);
    Optional<College> findCollegeById(Long id);
}
