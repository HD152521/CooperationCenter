package com.cooperation.project.cooperationcenter.domain.school.controller.adminpage;


import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final SchoolFindService schoolFindService;

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

    @DeleteMapping("/board")
    public BaseResponse<?> deleteBoard(@RequestBody SchoolRequest.BoardIdDto request){
        schoolService.deleteBoard(request);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/post")
    public BaseResponse<?> savePost(@ModelAttribute  SchoolRequest.SchoolPostDto request,
                                    @RequestPart(required = false) List<MultipartFile> files){
        log.info("request data:{}",request.toString());
        schoolService.savePost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @PatchMapping("/post")
    public BaseResponse<?> editBoard(@ModelAttribute  SchoolRequest.SchoolPostDto request,
                                     @RequestPart(required = false) List<MultipartFile> files){
        log.info("schoolpostdto:{}",request.toString());
        schoolService.editPost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @DeleteMapping("/post")
    public BaseResponse<?> deletePost(@RequestBody SchoolRequest.PostIdDto request){
        schoolService.deletePost(request);
        return BaseResponse.onSuccess("success");
    }

    @GetMapping("/post")
    public BaseResponse<?> getPost(@RequestParam Long postId){
        return BaseResponse.onSuccess(schoolFindService.getDetailPostDto(postId));
    }

    @GetMapping("/posts")
    public BaseResponse<?> getPosts(@ModelAttribute SchoolRequest.PostDto request,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                   Pageable pageable){
        log.info("data:{}",request.toString());
        Page<SchoolResponse.SchoolPostDto> response = schoolService.getPostByPage(request,pageable);
        return BaseResponse.onSuccess(response);
    }

    @PostMapping("/file")
    public BaseResponse<?> saveFilePost(@ModelAttribute  SchoolRequest.FilePostDto request,
                                    @RequestPart(required = false) MultipartFile files){
        schoolService.saveFilePost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @GetMapping("/file")
    public BaseResponse<?> getFilePost(@ModelAttribute SchoolRequest.PostIdDto request){
        return BaseResponse.onSuccess(schoolFindService.getDetailFilePostDto(request.postId()));
    }

    @PatchMapping("/file")
    public BaseResponse<?> editFilePost(@ModelAttribute  SchoolRequest.FilePostDto request,
                                     @RequestPart(required = false) MultipartFile files){
        log.info("filePost:{}",request.toString());
        schoolService.editFilePost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @DeleteMapping("/file")
    public BaseResponse<?> deleteFilePost(@RequestBody  SchoolRequest.PostIdDto request){
        log.info("dto:{}",request.toString());
        schoolService.deleteFilePost(request);
        return BaseResponse.onSuccess("success");
    }



    @PostMapping("/schedule")
    public BaseResponse<?> saveSchedule(@ModelAttribute  SchoolRequest.ScheduleDto request){
        schoolService.saveSchedule(request);
        return BaseResponse.onSuccess("success");
    }
    @GetMapping("/schedule")
    public BaseResponse<?> getSchedule(@ModelAttribute SchoolRequest.PostIdDto request){
        return BaseResponse.onSuccess(schoolFindService.getScheduleDtoById(request.postId()));
    }

    @GetMapping("/schedules")
    public BaseResponse<?> getSchedules(@ModelAttribute SchoolRequest.ScheduleDto request,
                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                        Pageable pageable){
        log.info("request:{}",request.toString());
        return BaseResponse.onSuccess(schoolFindService.getScheduleDtoPageByCondition(request,pageable));
    }

    @PatchMapping("/schedule")
    public BaseResponse<?> updateSchedule(@ModelAttribute  SchoolRequest.ScheduleDto request){
        schoolService.editSchedule(request);
        return BaseResponse.onSuccess("");
    }

    @DeleteMapping("/schedule")
    public BaseResponse<?> deleteSchedule(@RequestBody SchoolRequest.PostIdDto request){
        schoolService.deleteSchedule(request);
        return BaseResponse.onSuccess("");
    }
}

