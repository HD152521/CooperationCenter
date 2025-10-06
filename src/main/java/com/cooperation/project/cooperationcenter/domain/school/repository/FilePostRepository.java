package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.FilePost;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilePostRepository extends JpaRepository<FilePost,Long> {
    Optional<FilePost> findFilePostById(Long id);
    Page<FilePost> findFilePostBySchoolBoard(SchoolBoard schoolBoard, Pageable pageable);

    Optional<FilePost> findTopBySchoolBoardAndIdLessThanOrderByIdDesc(SchoolBoard schoolBoard, Long id); // 이전 글
    Optional<FilePost> findTopBySchoolBoardAndIdGreaterThanOrderByIdAsc(SchoolBoard schoolBoard, Long id); // 다음 글
}
