package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerRequest;
import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyAnswerService {

    private final SurveyFindService surveyFindService;

    @Transactional
    public void answerSurvey(String data, HttpServletRequest request) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        AnswerRequest.Dto requestDto = objectMapper.readValue(data, AnswerRequest.Dto.class);

        if (!(request instanceof MultipartHttpServletRequest multipartRequest)) {
            throw new IllegalStateException("Multipart request expected");
        }

        Survey survey = surveyFindService.getSurveyFromId(requestDto.surveyId());
        //fixme 추후 예정
        Member member = null;

        SurveyLog surveyLog = SurveyLog.builder()
                .survey(survey)
                .member(member)
                .build();

        //답변 로그를 저장하고 -> 문항들 저장
        List<Answer> savedAnswer = saveAnswer(requestDto.answers(),multipartRequest);

    }

    @Transactional
    public List<Answer> saveAnswer(List<AnswerRequest.AnswerDto> answerList, MultipartHttpServletRequest multipartRequest){
        List<Answer> saveList = new ArrayList<>();

        for (AnswerRequest.AnswerDto an : answerList) {
            if ("file".equals(an.type())) {
                saveFile(an,multipartRequest);
                //note 파일 저장은 따로 추가하자
            } else {
                saveList.add(convertToAnswer(an));
                log.info("Q{}: {}", an.questionId(), an.answer());
            }
        }

        return saveList;
    }

    private Answer convertToAnswer(AnswerRequest.AnswerDto answer){
        return null;
    }

    private void saveFile(AnswerRequest.AnswerDto answer, MultipartHttpServletRequest multipartRequest){
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
    }

}
