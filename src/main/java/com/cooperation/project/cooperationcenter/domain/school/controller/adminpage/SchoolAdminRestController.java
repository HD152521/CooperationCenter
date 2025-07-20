package com.cooperation.project.cooperationcenter.domain.school.controller.adminpage;


import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public BaseResponse<?> savePost(@ModelAttribute  SchoolRequest.SchoolPostDto request,
                                    @RequestPart(required = false) List<MultipartFile> files){
        log.info("request data:{}",request.toString());
        schoolService.savePost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @GetMapping("/boards")
    public BaseResponse<?> getPost(@ModelAttribute SchoolRequest.PostDto request,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                   Pageable pageable){
        log.info("data:{}",request.toString());
        return BaseResponse.onSuccess(schoolService.getPostByPage(request,pageable));
    }
}
