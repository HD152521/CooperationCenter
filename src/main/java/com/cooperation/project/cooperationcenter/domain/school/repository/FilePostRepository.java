package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.dto.PostStatus;
import com.cooperation.project.cooperationcenter.domain.school.dto.PostType;
import com.cooperation.project.cooperationcenter.domain.school.model.FilePost;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FilePostRepository extends JpaRepository<FilePost,Long> {
    Optional<FilePost> findFilePostById(Long id);
    Page<FilePost> findFilePostBySchoolBoardAndStatus(SchoolBoard schoolBoard, Pageable pageable, PostStatus status);

    Page<FilePost> findFilePostBySchoolBoardAndStatusAndPostTitleContainingIgnoreCase(
            SchoolBoard schoolBoard,
            PostStatus status,
            String keyword,
            Pageable pageable
    );
    List<FilePost> findFilePostBySchoolBoardAndStatusAndPostTitleContainingIgnoreCaseAndType(
            SchoolBoard schoolBoard,
            PostStatus status,
            String keyword,
            PostType type
    );

    Optional<FilePost> findTopBySchoolBoardAndIdLessThanOrderByIdDesc(SchoolBoard schoolBoard, Long id); // 이전 글
    Optional<FilePost> findTopBySchoolBoardAndIdGreaterThanOrderByIdAsc(SchoolBoard schoolBoard, Long id); // 다음 글
}
