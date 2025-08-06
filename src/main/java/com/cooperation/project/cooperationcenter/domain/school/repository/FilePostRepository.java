package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.FilePost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FilePostRepository extends JpaRepository<FilePost,Long> {
    Optional<FilePost> findFilePostById(Long id);
}
