package com.cooperation.project.cooperationcenter.domain.student.controller.adminpage;

import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class StudentAdminController {

    private final StudentService studentService;
    private final String studentPath = "/adminpage/user/student/";

    @RequestMapping("/student")
    public String studentPage(Model model, @ModelAttribute StudentRequest.ConditionDto condition,
                              @PageableDefault(size = 10, sort = "studentId", direction = Sort.Direction.DESC)
                              Pageable pageable){
        model.addAttribute("condition", condition);
        model.addAttribute("students",studentService.getStudentDtoPageByCondition(condition,pageable));
        return studentPath+"studentList";
    }
}
