package com.cooperation.project.cooperationcenter.domain.school.controller.adminpage;


import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/school")
@Slf4j
public class SchoolAdminRestController {

    private final SchoolService schoolService;

    @PostMapping("/save")
    public BaseResponse<?> saveSchool(@RequestBody SchoolRequest.SchoolDto request){
        schoolService.saveSchool(request);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/board")
    public BaseResponse<?> saveBoard(@RequestBody SchoolRequest.SchoolBoardDto request){
        schoolService.saveBoard(request);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/post")
    public BaseResponse<?> saveBoard(@RequestBody SchoolRequest.SchoolPostDto request){
        schoolService.savePost(request);
        return BaseResponse.onSuccess("success");
    }

    @GetMapping("/posts")
    public BaseResponse<?> getPost(String boardId){
        return BaseResponse.onSuccess(schoolService.getPost(boardId));
    }
}
