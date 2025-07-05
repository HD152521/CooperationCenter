package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.file.dto.SurveyFileDto;
import com.cooperation.project.cooperationcenter.domain.file.model.SurveyFileType;
import com.cooperation.project.cooperationcenter.domain.file.model.SurveyFile;
import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
    private final FileService fileService;

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
        List<SurveyLog> surveyLog = surveyFindService.getSurveyLogs(survey);
        //status 추가
        List<AnswerResponse.LogDto> logs = new ArrayList<>();
        if(!surveyLog.isEmpty())  logs = AnswerResponse.LogDto.from(surveyLog);
        return AnswerResponse.AnswerDto.from(survey,logs);
    }





    @Transactional
    protected List<Answer> saveAnswer(AnswerRequest.Dto answerList, MultipartHttpServletRequest multipartRequest,SurveyLog surveyLog){
        List<Answer> saveList = new ArrayList<>();
        SurveyFile surveyFile=null;
        for (AnswerRequest.AnswerDto an : answerList.answers()) {
            if (QuestionType.isFile(an.type())) {
                surveyFile = saveFile(an,multipartRequest,answerList.surveyId());
                //note 파일 저장은 따로 추가하자
            }
            saveList.add(convertToAnswer(an,answerList.surveyId(),surveyFile,surveyLog));
            log.info("Q{}: {}", an.questionId(), an.answer());
        }
        return answerRepository.saveAll(saveList);
    }

    private Answer convertToAnswer(AnswerRequest.AnswerDto answer,String surveyId,SurveyFile surveyFile,SurveyLog surveyLog){
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
            return Answer.builder()
                    .surveyLog(surveyLog)
                    .answerType(questionType)
                    .questionId(answer.questionId())
                    .questionRealId(answer.questionRealId())
                    .surveyFile(surveyFile)
                    .build();
        }
        return null;
    }

    public AnswerResponse.AnswerLogDto getAnswerLogDetail(String surveyId, String logId){
        //Note 질문이랑 답변 문항들 RESPONSE로 보내야함
        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        SurveyLog surveyLog = surveyFindService.getSurveyLog(logId);
        List<Answer> answers = surveyFindService.getAnswer(surveyLog);
        log.info("log Detail 조회 완료");
        return AnswerResponse.AnswerLogDto.from(survey,surveyLog,answers);
    }

    private SurveyFile saveFile(AnswerRequest.AnswerDto answer,MultipartHttpServletRequest multipartRequest, String surveyId){
        String key="";
        if (answer.answer() instanceof String str) key = answer.answer().toString();

        MultipartFile file = multipartRequest.getFile(key);
        if (file == null) {
            log.warn("❌ {} 필드 없음", key);
        } else if (file.isEmpty()) {
            log.warn("❌ {} 는 비어 있음", key);
        } else {
            log.info("✅ {} 수신 성공: {}", key, file.getOriginalFilename());
            //key예시 file-0 image-1
            String type = key.split("-")[0];
            return fileService.saveFile(new SurveyFileDto(file, surveyId, SurveyFileType.fromType(type)));
        }
        return null;
    }

}
