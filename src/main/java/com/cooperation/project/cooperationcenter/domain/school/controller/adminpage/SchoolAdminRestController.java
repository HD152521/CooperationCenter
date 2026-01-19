package com.cooperation.project.cooperationcenter.domain.school.controller.adminpage;


import com.cooperation.project.cooperationcenter.domain.school.dto.IntroRequest;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolFindService;
import com.cooperation.project.cooperationcenter.domain.school.service.SchoolService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "School Admin",
        description = "학교 정보 및 게시판 관리 API"
)
public class SchoolAdminRestController {

    private final SchoolService schoolService;
    private final SchoolFindService schoolFindService;

    @Operation(
            summary = "학교 정보 등록",
            description = """
        학교 기본 정보를 등록합니다.
        """
    )
    @PostMapping("/save")
    public BaseResponse<?> saveSchool(@RequestBody SchoolRequest.SchoolDto request){
        schoolService.saveSchool(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 소개 정보 저장",
            description = """
        학교 소개 페이지에 사용되는 정보를 저장합니다.
        기본 정보, 소개 문구, 대학 정보 등을 포함합니다.
        """
    )
    @PostMapping("/intro")
    public BaseResponse<?> saveIntro(@RequestBody IntroRequest.TotalIntroSaveDto request){
        schoolService.saveIntro(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 소개 정보 조회",
            description = """
        게시판 ID를 기준으로 학교 소개 정보를 조회합니다.
        """
    )
    @GetMapping("/intro/{boardId}")
    public BaseResponse<?> getIntro(@PathVariable Long boardId){
        return BaseResponse.onSuccess(schoolService.loadIntro(boardId));
    }

    @Operation(
            summary = "학교 게시판 생성",
            description = """
        새로운 학교 게시판을 생성합니다.
        """
    )
    @PostMapping("/board")
    public BaseResponse<?> saveBoard(@RequestBody SchoolRequest.SchoolBoardDto request){
        schoolService.saveBoard(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 게시판 삭제",
            description = """
        학교 게시판을 삭제합니다.
        게시판에 포함된 게시글도 함께 삭제됩니다.
        """
    )
    @DeleteMapping("/board")
    public BaseResponse<?> deleteBoard(@RequestBody SchoolRequest.BoardIdDto request){
        schoolService.deleteBoard(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 게시글 등록",
            description = """
        학교 게시판에 새로운 게시글을 등록합니다.
        파일 업로드를 함께 처리할 수 있습니다.
        """
    )
    @PostMapping("/post")
    public BaseResponse<?> savePost(@ModelAttribute  SchoolRequest.SchoolPostDto request,
                                    @RequestPart(required = false) List<MultipartFile> files){
        log.info("request data:{}",request.toString());
        schoolService.savePost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 게시글 수정",
            description = """
        기존 학교 게시글 내용을 수정합니다.
        """
    )
    @PatchMapping("/post")
    public BaseResponse<?> editBoard(@ModelAttribute  SchoolRequest.SchoolPostDto request,
                                     @RequestPart(required = false) List<MultipartFile> files){
        log.info("schoolpostdto:{}",request.toString());
        schoolService.editPost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 게시글 삭제",
            description = """
        게시글 ID를 기준으로 학교 게시글을 삭제합니다.
        """
    )
    @DeleteMapping("/post")
    public BaseResponse<?> deletePost(@RequestBody SchoolRequest.PostIdDto request){
        schoolService.deletePost(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 게시글 단건 조회",
            description = """
        게시글 ID를 기준으로 학교 게시글을 조회합니다.
        """
    )
    @GetMapping("/post")
    public BaseResponse<?> getPost(@RequestParam Long postId){
        return BaseResponse.onSuccess(schoolFindService.getDetailPostDto(postId));
    }


    @Operation(
            summary = "학교 게시글 목록 조회",
            description = """
        조건 및 페이지네이션을 기반으로
        학교 게시글 목록을 조회합니다.
        """
    )
    @GetMapping("/posts")
    public BaseResponse<?> getPosts(@ModelAttribute SchoolRequest.PostDto request,
                                   @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                   Pageable pageable){
        log.info("data:{}",request.toString());
        Page<SchoolResponse.SchoolPostDto> response = schoolService.getPostByPage(request,pageable);
        return BaseResponse.onSuccess(response);
    }

    @Operation(
            summary = "학교 파일 게시글 등록",
            description = """
        파일이 포함된 학교 게시글을 등록합니다.
        """
    )
    @PostMapping("/file")
    public BaseResponse<?> saveFilePost(@ModelAttribute  SchoolRequest.FilePostDto request,
                                    @RequestPart(required = false) MultipartFile files){
        schoolService.saveFilePost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 파일 게시글 조회",
            description = """
        파일이 포함된 학교 게시글을 조회합니다.
        """
    )
    @GetMapping("/file")
    public BaseResponse<?> getFilePost(@ModelAttribute SchoolRequest.PostIdDto request){
        return BaseResponse.onSuccess(schoolFindService.getDetailFilePostDto(request.postId()));
    }

    @Operation(
            summary = "학교 파일 게시글 수정",
            description = """
        파일 게시글 정보를 수정합니다.
        """
    )
    @PatchMapping("/file")
    public BaseResponse<?> editFilePost(@ModelAttribute  SchoolRequest.FilePostDto request,
                                     @RequestPart(required = false) MultipartFile files){
        log.info("filePost:{}",request.toString());
        schoolService.editFilePost(request,files);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학교 파일 게시글 삭제",
            description = """
        파일 게시글을 삭제합니다.
        """
    )
    @DeleteMapping("/file")
    public BaseResponse<?> deleteFilePost(@RequestBody  SchoolRequest.PostIdDto request){
        log.info("dto:{}",request.toString());
        schoolService.deleteFilePost(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "학사 일정 등록",
            description = """
        새로운 학사 일정을 등록합니다.
        """
    )
    @PostMapping("/schedule")
    public BaseResponse<?> saveSchedule(@ModelAttribute  SchoolRequest.ScheduleDto request){
        schoolService.saveSchedule(request);
        return BaseResponse.onSuccess("success");
    }
    @Operation(
            summary = "학사 일정 조회",
            description = """
        게시글 ID를 기준으로 특정 학사 일정을 조회합니다.
        """
    )
    @GetMapping("/schedule")
    public BaseResponse<?> getSchedule(@ModelAttribute SchoolRequest.PostIdDto request){
        return BaseResponse.onSuccess(schoolFindService.getScheduleDtoById(request.postId()));
    }

    @Operation(
            summary = "학사 일정 목록 조회",
            description = """
        조건 및 페이지네이션을 기반으로
        학사 일정 목록을 조회합니다.
        """
    )
    @GetMapping("/schedules")
    public BaseResponse<?> getSchedules(@ModelAttribute SchoolRequest.ScheduleDto request,
                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                                        Pageable pageable){
        log.info("request:{}",request.toString());
        return BaseResponse.onSuccess(schoolFindService.getScheduleDtoPageByCondition(request,pageable));
    }

    @Operation(
            summary = "학사 일정 수정",
            description = """
        기존 학사 일정 정보를 수정합니다.
        """
    )

    @PatchMapping("/schedule")
    public BaseResponse<?> updateSchedule(@ModelAttribute  SchoolRequest.ScheduleDto request){
        schoolService.editSchedule(request);
        return BaseResponse.onSuccess("");
    }

    @Operation(
            summary = "학사 일정 삭제",
            description = """
        학사 일정 ID를 기준으로 학사 일정을 삭제합니다.
        """
    )
    @DeleteMapping("/schedule")
    public BaseResponse<?> deleteSchedule(@RequestBody SchoolRequest.PostIdDto request){
        schoolService.deleteSchedule(request);
        return BaseResponse.onSuccess("");
    }
}

