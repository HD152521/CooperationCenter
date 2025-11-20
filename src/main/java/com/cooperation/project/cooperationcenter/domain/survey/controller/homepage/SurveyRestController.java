package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.*;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
@Slf4j
public class SurveyRestController {

    private final SurveySaveService surveySaveService;
    private final SurveyFindService surveyFindService;
    private final SurveyAnswerService surveyAnswerService;
    private final SurveyLogService surveyLogService;
    private final SurveyQRService surveyQRService;
    private final SurveyFolderService surveyFolderService;


    //Note 설문조사 보여주는 controller

    @GetMapping("/list")
    public BaseResponse<?> getSurveyList(@PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
                                             Pageable pageable,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) String surveyType){
        log.info("response:{}",surveyFindService.getFilteredSurveysAll(pageable,new SurveyRequest.LogFilterDto(title,null,null, Survey.SurveyType.getSruveyType(surveyType)),null));
        return BaseResponse.onSuccess(surveyFindService.getFilteredSurveysAll(pageable,new SurveyRequest.LogFilterDto(title,null,null,Survey.SurveyType.getSruveyType(surveyType)),null));
    }

    @GetMapping("/{surveyId}")
    public BaseResponse<?> getSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        log.info("response:{}",surveySaveService.getSurveys(surveyId).toString());
        return BaseResponse.onSuccess(surveySaveService.getSurveys(surveyId));
    }

    @PostMapping("/admin/make")
    public BaseResponse<?> saveSurvey(@RequestBody SurveyRequest.SurveyDto request){
        log.info("[controller] {}",request.toString());
        surveySaveService.saveSurvey(request);
        return BaseResponse.onSuccess("success");
    }

    @DeleteMapping("/admin/{surveyId}")
    public BaseResponse<?> deleteSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.deleteSurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/admin/copy/{surveyId}")
    public BaseResponse<?> copySurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.copySurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }


    @PatchMapping("/admin/edit")
    public BaseResponse<?> editSurvey(@RequestBody SurveyEditDto request){
        log.info("[controller] getSurvey 진입 : {}",request.surveyId());
        //fixme 제목 안바뀜
        surveySaveService.editSurvey(request);
        return BaseResponse.onSuccess("success");
    }

    
    //note 설문조사 답변 및 로그 확인
    @PostMapping("/answer")
    public BaseResponse<?> receiveSurveyAnswer(
            @RequestPart("data") String data,
            HttpServletRequest request,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) throws JsonProcessingException {
        log.info("[submit answer] dto:{}",data);
        try{
            surveyAnswerService.answerSurvey(data,request,memberDetails);
        }catch(BaseException e){
          log.warn(e.getMessage());
        } catch (Exception e){
            log.warn(e.getMessage());
        }
        log.info("save answer");
        return BaseResponse.onSuccess("succeess");
    }

    @GetMapping("/admin/answer")
    public BaseResponse<?> getAllAnswerLog(){
        List<AnswerResponse.LogDto> result = surveyLogService.getAllAnswerLog();
        log.info("result : {}",result.toString());
        return BaseResponse.onSuccess(result);
    }

    @GetMapping("/admin/answer/{surveyId}")
    public BaseResponse<?> getAnswerLog(@PathVariable String surveyId){
        AnswerResponse.AnswerDto result = surveyLogService.getAnswerLog(surveyId);
        log.info("result : {}",result.toString());
        return BaseResponse.onSuccess(result);
    }

    @PostMapping("/admin/log/csv")
    public ResponseEntity<StreamingResponseBody> extractCsv(@RequestBody LogCsv.RequestDto request){
        log.info("[enter extract csv]");
        return surveyLogService.extractCsv(request);
    }

    @PostMapping("/admin/log/{surveyId}")
    public ResponseEntity<StreamingResponseBody> extractCsv(@PathVariable String surveyId){
        log.info("extracy all csv...");
        return surveyLogService.extractAllCsv(surveyId);
    }

    @PostMapping("/admin/log/file/student/{surveyId}")
    public ResponseEntity<StreamingResponseBody> extractFileStudent(@PathVariable String surveyId){
        return surveyLogService.extractFileStudent(surveyId);
    }
    @PostMapping("/admin/log/file/survey/{surveyId}")
    public ResponseEntity<StreamingResponseBody> extractFileSurvey(@PathVariable String surveyId){
        return surveyLogService.extractFileSurvey(surveyId);
    }

    @GetMapping("/qr")
    public Object cerateQR(@RequestParam String url,HttpServletRequest request) throws WriterException, IOException {
        log.info("url:{}",url);
        Object Qr = surveyQRService.cerateQR(url,request);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(Qr);
    }

    @GetMapping("/admin/template")
    public BaseResponse<?> getTemplate(@RequestParam("type") String type){
        log.info("enter controller type:{}",type);
        return BaseResponse.onSuccess(surveySaveService.getTemplate(type));
    }

    //note 설문조사 폴더용 controller7

    @GetMapping("/admin/folders")
    public BaseResponse<?> getFolders(){
        return BaseResponse.onSuccess(surveyFolderService.getSurveyFolderDtos());
    }
    //fixme 수정
    @PostMapping("/admin/folders")
    public BaseResponse<?> makeFolder(@RequestBody SurveyFolderDto request,@AuthenticationPrincipal MemberDetails memberDetails){
        surveyFolderService.saveSurveyFolderDto(request,memberDetails);
        return BaseResponse.onSuccess("success");
    }
    //fixme 수정
    @PatchMapping("/admin/folders/{folderId}")
    public BaseResponse<?> updateFolder(@RequestBody SurveyFolderDto request){
        surveyFolderService.updateSurveyFolderDto(request);
        return BaseResponse.onSuccess("success");
    }

    @DeleteMapping("/admin/folders/{folderId}")
    public BaseResponse<?> deleteFolder(@PathVariable String folderId){
        surveyFolderService.deleteSurveyFolder(folderId);
        return BaseResponse.onSuccess("success");
    }
}
