package com.cooperation.project.cooperationcenter.domain.student.controller.adminpage;

import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.student.service.StudentService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadStudentsByCondition(@ModelAttribute StudentRequest.ConditionDto condition){
        log.info("condition:{}",condition.toString());
        byte[] file = studentService.exportStudentsExcel(condition);
        if (file == null || file.length == 0) {
            return ResponseEntity.noContent().build(); // 또는 헤더만 맞춘 빈 파일 반환
        }
        String filename = "students-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, studentService.encodeAttachmentFilename(filename))
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(file.length)
                .body(file);
    }

}
