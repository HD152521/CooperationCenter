package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionType;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.domain.survey.repository.AnswerRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyLogRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyAnswerService {

    private final SurveyFindService surveyFindService;

    private final AnswerRepository answerRepository;
    private final SurveyLogRepository surveyLogRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void answerSurvey(String data, HttpServletRequest request) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        AnswerRequest.Dto requestDto = objectMapper.readValue(data, AnswerRequest.Dto.class);

        if (!(request instanceof MultipartHttpServletRequest multipartRequest)) {
            throw new IllegalStateException("Multipart request expected");
        }

        Survey survey = surveyFindService.getSurveyFromId(requestDto.surveyId());
        //fixme 추후 예정
        Member member = memberRepository.findMemberByEmail("test@test.com").get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(requestDto.startTime(), formatter);

        log.info("date convert완료");
        SurveyLog surveyLog = SurveyLog.builder()
                .survey(survey)
                .member(member)
                .startTime(dateTime)
                .build();

        //답변 로그를 저장하고 -> 문항들 저장
        List<Answer> savedAnswer = saveAnswer(requestDto,multipartRequest,surveyLog);
        surveyLog.addAnswer(savedAnswer);
        log.info("답변 저장 완료");

        surveyLogRepository.save(surveyLog);
        log.info("➡️survey 답변 완료!");
    }

    public AnswerResponse.AnswerDto getAnswerLog(String surveyId){
        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        List<SurveyLog> surveyLog = surveyLogRepository.findSurveysLogBySurvey(survey);
        //status 추가
        List<AnswerResponse.LogDto> logs = AnswerResponse.LogDto.from(surveyLog);
        return AnswerResponse.AnswerDto.from(survey,logs);
    }





    @Transactional
    protected List<Answer> saveAnswer(AnswerRequest.Dto answerList, MultipartHttpServletRequest multipartRequest,SurveyLog surveyLog){
        List<Answer> saveList = new ArrayList<>();
        String fileName=null;
        for (AnswerRequest.AnswerDto an : answerList.answers()) {
            fileName=null;
            if (QuestionType.isFile(an.type())) {
                MultipartFile file = saveFile(an,multipartRequest,answerList.surveyId());
                fileName = file.getOriginalFilename();
                //note 파일 저장은 따로 추가하자
            }
            saveList.add(convertToAnswer(an,answerList.surveyId(),fileName,surveyLog));
            log.info("Q{}: {}", an.questionId(), an.answer());
        }
        return answerRepository.saveAll(saveList);
    }

    private Answer convertToAnswer(AnswerRequest.AnswerDto answer,String surveyId,String filename,SurveyLog surveyLog){
        log.info(answer.type());
        QuestionType questionType = QuestionType.fromType(answer.type());
        if(questionType==null){
            log.warn("Answer type 변환 중 올바르지 않음");
        }
        if (QuestionType.checkType(questionType)) {
            //옵션 질문들
            return Answer.builder()
                    .surveyLog(surveyLog)
                    .answerType(questionType)
                    .questionId(answer.questionId())
                    .questionRealId(answer.questionRealId())
                    .multiAnswer(answer.answer().toString())
                    .build();
        }else if(QuestionType.isDate(questionType)){
            LocalDateTime dateTime = LocalDate.parse((String)answer.answer()).atStartOfDay();
            return Answer.builder()
                    .surveyLog(surveyLog)
                    .answerType(questionType)
                    .questionId(answer.questionId())
                    .questionRealId(answer.questionRealId())
                    .dateAnswer(dateTime)
                    .build();
        }else if(QuestionType.isText(questionType)){
            return Answer.builder()
                    .surveyLog(surveyLog)
                    .answerType(questionType)
                    .questionId(answer.questionId())
                    .questionRealId(answer.questionRealId())
                    .textAnswer(answer.answer().toString())
                    .build();
        }else if(QuestionType.isFile(questionType)){
            String path = "uploads/"+surveyId+"/"+filename;
            return Answer.builder()
                    .surveyLog(surveyLog)
                    .answerType(questionType)
                    .questionId(answer.questionId())
                    .questionRealId(answer.questionRealId())
                    .filePath(path)
                    .build();
        }
        return null;
    }

    private MultipartFile saveFile(AnswerRequest.AnswerDto answer, MultipartHttpServletRequest multipartRequest,String surveyId){
        String key="";
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
            String path = "uploads/"+surveyId;
            Path uploadDir = Paths.get(System.getProperty("user.dir"), path);
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
        return file;
    }

}
