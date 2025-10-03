package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.IntroPost;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntroPostRepository extends JpaRepository<IntroPost,Long> {
    Optional<IntroPost> findIntroPostById(Long id);
    Optional<IntroPost> findIntroPostsBySchoolBoard(SchoolBoard schoolBoard);
}
