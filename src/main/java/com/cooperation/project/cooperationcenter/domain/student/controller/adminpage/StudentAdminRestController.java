package com.cooperation.project.cooperationcenter.domain.student.controller.adminpage;

import com.cooperation.project.cooperationcenter.domain.student.service.StudentService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/students")
@Slf4j
public class StudentAdminRestController {

    private final StudentService studentService;

    @GetMapping
    public BaseResponse<?> getAllStudent(){
        return BaseResponse.onSuccess(studentService.getAllStudentDto());
    }

    @GetMapping("/{id}")
    public BaseResponse<?> getStudentById(@PathVariable Long id){
        log.info("enter get student / "+id);
        return BaseResponse.onSuccess(studentService.getStudentDtoById(id));
    }



}
