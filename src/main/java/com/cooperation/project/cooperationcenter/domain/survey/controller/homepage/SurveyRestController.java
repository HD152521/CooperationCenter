package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.*;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "설문 목록 조회",
            description = """
        페이지네이션 및 조건(title, surveyType)을 기반으로
        설문 목록을 조회합니다.
        """
    )
    @GetMapping("/list")
    public BaseResponse<?> getSurveyList(@PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC)
                                             Pageable pageable,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) String surveyType){
        log.info("response:{}",surveyFindService.getFilteredSurveysAll(pageable,new SurveyRequest.LogFilterDto(title,null,null, Survey.SurveyType.getSruveyType(surveyType)),null));
        return BaseResponse.onSuccess(surveyFindService.getFilteredSurveysAll(pageable,new SurveyRequest.LogFilterDto(title,null,null,Survey.SurveyType.getSruveyType(surveyType)),null));
    }

    @Operation(
            summary = "설문 상세 조회",
            description = """
        설문 ID를 기반으로 설문 제목, 설명, 질문 목록을 조회합니다.
        설문 참여 화면에서 사용됩니다.
        """
    )
    @GetMapping("/{surveyId}")
    public BaseResponse<?> getSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        log.info("response:{}",surveySaveService.getSurveys(surveyId).toString());
        return BaseResponse.onSuccess(surveySaveService.getSurveys(surveyId));
    }

    @Operation(
            summary = "설문 생성",
            description = """
        질문 목록을 포함한 새로운 설문을 생성합니다.
        관리자 페이지에서 설문 작성 시 사용됩니다.
        """
    )
    @PostMapping("/admin/make")
    public BaseResponse<?> saveSurvey(@RequestBody SurveyRequest.SurveyDto request){
        log.info("[controller] {}",request.toString());
        surveySaveService.saveSurvey(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "설문 삭제",
            description = """
        설문 ID를 기준으로 설문을 삭제합니다.
        삭제된 설문은 복구할 수 없습니다.
        """
    )
    @DeleteMapping("/admin/{surveyId}")
    public BaseResponse<?> deleteSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.deleteSurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "설문 복사",
            description = """
        기존 설문을 복사하여 새로운 설문으로 생성합니다.
        질문 구성과 설정이 함께 복사됩니다.
        """
    )
    @PostMapping("/admin/copy/{surveyId}")
    public BaseResponse<?> copySurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.copySurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }


    @Operation(
            summary = "설문 수정",
            description = """
        기존 설문의 제목, 설명, 질문 정보를 수정합니다.
        """
    )
    @PatchMapping("/admin/edit")
    public BaseResponse<?> editSurvey(@RequestBody SurveyEditDto request){
        log.info("[controller] getSurvey 진입 : {}",request.surveyId());
        //fixme 제목 안바뀜
        surveySaveService.editSurvey(request);
        return BaseResponse.onSuccess("success");
    }

    
    //note 설문조사 답변 및 로그 확인
    @Operation(
            summary = "설문 응답 제출",
            description = """
        사용자가 작성한 설문 응답 데이터를 서버에 제출합니다.
        제출된 응답은 설문 통계 및 응답 로그로 저장됩니다.
        """
    )
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

    @Operation(
            summary = "전체 설문 응답 로그 조회",
            description = """
        관리자 권한으로 전체 설문 응답 로그를 조회합니다.
        """
    )
    @GetMapping("/admin/answer")
    public BaseResponse<?> getAllAnswerLog(){
        List<AnswerResponse.LogDto> result = surveyLogService.getAllAnswerLog();
        log.info("result : {}",result.toString());
        return BaseResponse.onSuccess(result);
    }

    @Operation(
            summary = "설문별 응답 로그 조회",
            description = """
        특정 설문에 대한 응답 로그를 조회합니다.
        """
    )
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

    @Operation(
            summary = "설문 응답 CSV 추출",
            description = """
        특정 설문에 대한 응답 데이터를 CSV 파일로 스트리밍 다운로드합니다.
        """
    )
    @PostMapping("/admin/log/{surveyId}")
    public ResponseEntity<StreamingResponseBody> extractCsv(@PathVariable String surveyId){
        log.info("extracy all csv...");
        return surveyLogService.extractAllCsv(surveyId);
    }

    @Operation(
            summary = "설문 응답 파일 다운로드 (학생 기준)",
            description = """
        학생 기준으로 업로드된 설문 응답 파일을 응답한 학생 별로 파일을 만들어서 다운로드합니다.
        """
    )
    @PostMapping("/admin/log/file/student/{surveyId}")
    public ResponseEntity<StreamingResponseBody> extractFileStudent(@PathVariable String surveyId){
        return surveyLogService.extractFileStudent(surveyId);
    }

    @Operation(
            summary = "설문 응답 파일 다운로드 (설문 기준)",
            description = """
        설문 응답에 포함된 파일 업로드 데이터를 설문조사 별로 파일을 만들어 다운로드합니다.
        """
    )
    @PostMapping("/admin/log/file/survey/{surveyId}")
    public ResponseEntity<StreamingResponseBody> extractFileSurvey(@PathVariable String surveyId){
        return surveyLogService.extractFileSurvey(surveyId);
    }

    @Operation(
            summary = "설문 QR 코드 생성",
            description = """
        설문 접근 URL을 기반으로 QR 코드 데이터를 생성합니다.
        오프라인 배포용으로 사용됩니다.
        """
    )
    @GetMapping("/qr")
    public Object cerateQR(@RequestParam String url,HttpServletRequest request) throws WriterException, IOException {
        log.info("url:{}",url);
        Object Qr = surveyQRService.cerateQR(url,request);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(Qr);
    }

    @Operation(
            summary = "설문 템플릿 조회",
            description = """
        설문 타입에 따라 미리 정의된 기본 질문 템플릿을 조회합니다.
        """
    )
    @GetMapping("/admin/template")
    public BaseResponse<?> getTemplate(@RequestParam("type") String type){
        log.info("enter controller type:{}",type);
        return BaseResponse.onSuccess(surveySaveService.getTemplate(type));
    }

    @Operation(
            summary = "설문 폴더 목록 조회",
            description = """
        관리자가 생성한 설문 폴더 목록을 조회합니다.
        """
    )
    @GetMapping("/admin/folders")
    public BaseResponse<?> getFolders(){
        return BaseResponse.onSuccess(surveyFolderService.getSurveyFolderDtos());
    }

    @Operation(
            summary = "설문 폴더 생성",
            description = """
        설문을 분류하기 위한 새로운 폴더를 생성합니다.
        """
    )
    @PostMapping("/admin/folders")
    public BaseResponse<?> makeFolder(@RequestBody SurveyFolderDto request,@AuthenticationPrincipal MemberDetails memberDetails){
        surveyFolderService.saveSurveyFolderDto(request,memberDetails);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "설문 폴더 수정",
            description = """
        설문 폴더의 이름 또는 정보를 수정합니다.
        """
    )
    @PatchMapping("/admin/folders/{folderId}")
    public BaseResponse<?> updateFolder(@RequestBody SurveyFolderDto request){
        surveyFolderService.updateSurveyFolderDto(request);
        return BaseResponse.onSuccess("success");
    }

    @Operation(
            summary = "설문 폴더 삭제",
            description = """
        설문 폴더를 삭제합니다.
        폴더 내 설문은 삭제되지 않습니다.
        """
    )
    @DeleteMapping("/admin/folders/{folderId}")
    public BaseResponse<?> deleteFolder(@PathVariable String folderId){
        surveyFolderService.deleteSurveyFolder(folderId);
        return BaseResponse.onSuccess("success");
    }
}
