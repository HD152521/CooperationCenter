package com.cooperation.project.cooperationcenter.domain.student.repository;

import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {
}
