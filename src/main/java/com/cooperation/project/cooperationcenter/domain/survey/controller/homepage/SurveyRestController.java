package com.cooperation.project.cooperationcenter.domain.survey.controller.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveySaveService;
import com.cooperation.project.cooperationcenter.global.exception.BaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/survey")
@Slf4j
public class SurveyRestController {

    private final SurveySaveService surveySaveService;
    private final SurveyFindService surveyFindService;

    @PostMapping("/make")
    public void saveSurvey(@RequestBody SurveyRequest.SurveyDto request){
        log.info("[controller] {}",request.toString());
        surveySaveService.saveSurvey(request);
    }

    @GetMapping("/{surveyId}")
    public AnswerPageDto getSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        return surveySaveService.getSurveys(surveyId);
    }

    @GetMapping("/getAll")
    public List<SurveyResponseDto> getSurveyAll(){
        return surveyFindService.getAllSurvey();
    }

    @DeleteMapping("/{surveyId}")
    public BaseResponse<?> deleteSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.deleteSurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/copy/{surveyId}")
    public BaseResponse<?> copoSurvey(@PathVariable String surveyId){
        log.info("[controller] getSurvey 진입 : {}",surveyId);
        surveySaveService.copySurvey(surveyId);
        return BaseResponse.onSuccess("success");
    }

//    @PostMapping("/answer")
//    public void saveSurvey(@RequestBody AnswerRequest.Dto request){
//        log.info("[contorller answer] {}",request.toString());
//    }

//    @PostMapping(value = "/answer", consumes = {"multipart/form-data", "multipart/mixed"})
//    public ResponseEntity<Void> receiveSurveyAnswer(
//            @RequestPart("data") String data,
//            @RequestPart Map<String, MultipartFile> files
//    ) throws JsonProcessingException {
//        log.info("진입 완료");
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        AnswerRequest.Dto request = objectMapper.readValue(data, AnswerRequest.Dto.class);
//        log.info("진입 완료(1)");
//        for (AnswerRequest.AnswerDto answer : request.answers()) {
//            if ("file".equals(answer.type())) {
//                String fileKey = answer.answer(); // ex: "file-2"
//                MultipartFile file = files.get(fileKey);
//                log.info("진입 완료(2)");
//                if (file == null) {
//                    System.out.println("❌ file-7 필드 없음");
//                } else if (file.isEmpty()) {
//                    System.out.println("❌ file-7는 비어있음");
//                } else {
//                    System.out.println("✅ file-7 수신 성공: " + file.getOriginalFilename());
//                }
//
//                if (file != null && !file.isEmpty()) {
//                    // 파일 저장 처리
//                    Path saved = Paths.get("/uploads", file.getOriginalFilename());
//                    try {
//                        file.transferTo(saved);
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    System.out.println("Saved file: " + saved);
//                }
//            } else {
//                System.out.println("Q" + answer.questionId() + ": " + answer.answer());
//            }
//        }
//
//        return ResponseEntity.ok().build();
//    }
@PostMapping("/answer")
public ResponseEntity<Void> receiveSurveyAnswer(
        @RequestPart("data") String data,
        HttpServletRequest request
) throws JsonProcessingException {
    log.info("진입 완료");
    ObjectMapper objectMapper = new ObjectMapper();
    log.info("진입 완료2");
    AnswerRequest.Dto requestDto = objectMapper.readValue(data, AnswerRequest.Dto.class);
    log.info("진입 완료3");
    if (!(request instanceof MultipartHttpServletRequest multipartRequest)) {
        throw new IllegalStateException("Multipart request expected");
    }

    for (AnswerRequest.AnswerDto answer : requestDto.answers()) {
        if ("file".equals(answer.type())) {
            log.info("진입 완료4");
            String key=""; // ex: file-7
            if (answer.answer() instanceof String str) {
                key = answer.answer().toString();
                System.out.println("문자열 답변: " + str);
            } else if (answer.answer() instanceof List<?> list) {
                System.out.println("다중 선택 답변: " + list);
            }

            MultipartFile file = multipartRequest.getFile(key);
            if (file == null) {
                log.warn("❌ {} 필드 없음", key);
            } else if (file.isEmpty()) {
                log.warn("❌ {} 는 비어 있음", key);
            } else {
                log.info("✅ {} 수신 성공: {}", key, file.getOriginalFilename());
                Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");
                try {
                    Files.createDirectories(uploadDir); // 경로 없으면 생성
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Path saved = uploadDir.resolve(file.getOriginalFilename());
                try {
                    file.transferTo(saved);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            log.info("Q{}: {}", answer.questionId(), answer.answer());
        }
    }

    return ResponseEntity.ok().build();
}

}
