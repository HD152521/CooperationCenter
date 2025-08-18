package com.cooperation.project.cooperationcenter.domain.student.repository;

import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentRepositoryCustom {
    Page<Student> loadStudentByCondition(StudentRequest.ConditionDto condition, Pageable pageable);
}