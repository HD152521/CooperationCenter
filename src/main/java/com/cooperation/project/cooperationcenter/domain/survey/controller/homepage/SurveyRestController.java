package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.*;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{surveyId}")
    public AnswerPageDto getSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        return surveySaveService.getSurveys(surveyId);
    }

    @PostMapping("/answer")
    public ResponseEntity<Void> receiveSurveyAnswer(
            @RequestPart("data") String data,
            HttpServletRequest request,
            @AuthenticationPrincipal MemberDetails memberDetails
    ) throws JsonProcessingException {
        log.info("[submit answer] dto:{}",data);
        try{
            surveyAnswerService.answerSurvey(data,request,memberDetails);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
        log.info("save answer");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/make")
    public void saveSurvey(@RequestBody SurveyRequest.SurveyDto request){
        log.info("[controller] {}",request.toString());
        surveySaveService.saveSurvey(request);
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
    public void editSurvey(@RequestBody SurveyEditDto request){
        log.info("[controller] getSurvey 진입 : {}",request.surveyId());
        //fixme 제목 안바뀜
        surveySaveService.editSurvey(request);
    }

    @GetMapping("/admin/answer/{surveyId}")
    public AnswerResponse.AnswerDto getAnswerLog(@PathVariable String surveyId){
        AnswerResponse.AnswerDto result = surveyLogService.getAnswerLog(surveyId);
        log.info("result : {}",result.toString());
        return result;
    }

    @PostMapping("/admin/log/csv")
    public ResponseEntity<Resource> extractCsv(@RequestBody LogCsv.RequestDto request){
        log.info("[enter extract csv]");
        return surveyLogService.extractCsv(request);
    }

    @PostMapping("/admin/log/{surveyId}")
    public ResponseEntity<Resource> extractCsv(@PathVariable String surveyId){
        log.info("extracy all csv...");
        return surveyLogService.extractAllCsv(surveyId);
    }

    @PostMapping("/admin/log/file/student/{surveyId}")
    public ResponseEntity<byte[]> extractFileStudent(@PathVariable String surveyId){
        return surveyLogService.extractFileStudent(surveyId);
    }
    @PostMapping("/admin/log/file/survey/{surveyId}")
    public ResponseEntity<byte[]> extractFileSurvey(@PathVariable String surveyId){
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

    //fixme 파일 다운로드 받는 코드임 수정 필요
//    @GetMapping("/api/v1/survey/{surveyId}/download-answers")
//    public ResponseEntity<byte[]> downloadAnswersZip(@PathVariable String surveyId) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
//
//            List<Question> questions = questionRepository.findBySurveyId(surveyId);
//
//            for (Question question : questions) {
//                List<FileAttachment> files = fileAttachmentRepository.findAllByQuestionId(question.getId());
//
//                int index = 1;
//                for (FileAttachment file : files) {
//                    Path path = Paths.get(file.getPath()).resolve(file.getStoredName());
//                    if (!Files.exists(path)) continue;
//
//                    String zipEntryName = "Q" + question.getQuestionOrder() + "/" + index + "_" + file.getOriginalName();
//                    zos.putNextEntry(new ZipEntry(zipEntryName));
//                    Files.copy(path, zos);
//                    zos.closeEntry();
//                    index++;
//                }
//            }
//
//            zos.finish(); // 명시적 종료
//        }
//
//        byte[] zipBytes = baos.toByteArray();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + surveyId + ".zip\"")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .contentLength(zipBytes.length)
//                .body(zipBytes);
//    }

}
