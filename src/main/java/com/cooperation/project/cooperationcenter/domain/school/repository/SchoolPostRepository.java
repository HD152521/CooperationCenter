package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolPostRepository extends JpaRepository<SchoolPost,Long> {
    List<SchoolPost> findBySchoolBoard(SchoolBoard schoolBoard);
    Page<SchoolPost> findBySchoolBoard(SchoolBoard schoolBoard, Pageable pageable);
}
