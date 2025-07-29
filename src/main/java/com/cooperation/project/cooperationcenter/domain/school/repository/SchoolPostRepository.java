package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SchoolPostRepository extends JpaRepository<SchoolPost,Long> {
    List<SchoolPost> findBySchoolBoard(SchoolBoard schoolBoard);
    Page<SchoolPost> findBySchoolBoard(SchoolBoard schoolBoard, Pageable pageable);
    @Override
    Optional<SchoolPost> findById(Long id);
    Optional<SchoolPost> findTopBySchoolBoardAndIdLessThanOrderByIdDesc(SchoolBoard schoolBoard, Long id); // 이전 글
    Optional<SchoolPost> findTopBySchoolBoardAndIdGreaterThanOrderByIdAsc(SchoolBoard schoolBoard, Long id); // 다음 글

    @Modifying
    @Query("UPDATE SchoolPost p SET p.views = p.views + 1 WHERE p.id = :postId")
    void incrementViewCount(@Param("postId") Long postId);
}
