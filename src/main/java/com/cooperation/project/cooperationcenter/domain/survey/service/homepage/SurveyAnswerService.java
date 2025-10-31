package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.student.service.StudentService;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionType;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.domain.survey.repository.AnswerRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyLogRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyRepository;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
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
    private final StudentService studentService;

    private final AnswerRepository answerRepository;
    private final SurveyLogRepository surveyLogRepository;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;


    @Transactional
    public void answerSurvey(String data, HttpServletRequest request, MemberDetails memberDetails) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        AnswerRequest.Dto requestDto = objectMapper.readValue(data, AnswerRequest.Dto.class);
        log.info("request 매핑완료");
        log.info("request:{}",requestDto.toString());

        if (!(request instanceof MultipartHttpServletRequest multipartRequest)) {
            throw new IllegalStateException("Multipart request expected");
        }

        Survey survey = surveyFindService.getSurveyFromId(requestDto.surveyId());
//        if(checkDate(survey)) throw new BaseException(ErrorCode.SURVEY_DATE_NOT_VALID);
        survey.setParticipantCount();

        Member member = memberRepository.findMemberByEmail(memberDetails.getUsername()).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(requestDto.startTime(), formatter);

        log.info("date convert완료");
        SurveyLog surveyLog = SurveyLog.builder()
                .survey(survey)
                .member(member)
                .startTime(dateTime)
                .build();
        surveyLog = surveyLogRepository.save(surveyLog);

        List<Answer> savedAnswer = saveAnswer(requestDto,multipartRequest,surveyLog);

        //note 학생으로 변환
        if(survey.getSurveyType().equals(Survey.SurveyType.STUDENT)) studentService.addStudentBySurvey(survey.getQuestions(),savedAnswer,member);

        surveyLog.addAnswer(savedAnswer);
        surveyRepository.save(survey);
        surveyLogRepository.save(surveyLog);
    }

    public boolean checkDate(Survey survey){
        LocalDate now = LocalDate.now();
        return (survey.getStartDate() != null && survey.getEndDate() != null) &&
                ( !now.isBefore(survey.getStartDate()) && !now.isAfter(survey.getEndDate()) );
    }

    @Transactional
    protected List<Answer> saveAnswer(AnswerRequest.Dto answerList, MultipartHttpServletRequest multipartRequest,SurveyLog surveyLog){
        List<Answer> saveList = new ArrayList<>();
        FileAttachment surveyFile=null;
        if(!answerList.answers().isEmpty() && answerList.answers()!=null) {
            for (AnswerRequest.AnswerDto an : answerList.answers()) {
                if (QuestionType.isFile(an.type())) {
                    surveyFile = saveFile(an, multipartRequest, answerList.surveyId());
                }
                saveList.add(convertToAnswer(an, answerList.surveyId(), surveyFile, surveyLog));
                log.info("Q{}: {}", an.questionId(), an.answer());
            }
        }if(!answerList.templateAnswers().isEmpty() && answerList.templateAnswers()!=null){
            //todo 템플릿 문항들 이제 매핑 해야함.  템플릿 문항들이 맞는지도 봐야할듯
            log.info("template널 값 아님");
            log.info("tempalte 문제들:{}",answerList.templateAnswers());
            for (AnswerRequest.AnswerDto an : answerList.templateAnswers()) {
                if (QuestionType.isFile(an.type())) {
                    surveyFile = saveFile(an, multipartRequest, answerList.surveyId());
                }
                saveList.add(convertToAnswer(an, answerList.surveyId(), surveyFile, surveyLog));
                log.info("Q{}: {}", an.questionId(), an.answer());
            }

        }
        return answerRepository.saveAll(saveList);
    }

    private Answer convertToAnswer(AnswerRequest.AnswerDto answer,String surveyId,FileAttachment surveyFile,SurveyLog surveyLog){
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

    private FileAttachment saveFile(AnswerRequest.AnswerDto answer,MultipartHttpServletRequest multipartRequest, String surveyId){
        String key="";
        if (answer.answer() instanceof String str) key = answer.answer().toString();

        MultipartFile file = multipartRequest.getFile(key);
        double mb = file.getSize() / 1024.0 / 1024.0;

        if (file == null) {
            log.warn("❌ {} 필드 없음", key);
        } else if (file.isEmpty()) {
            log.warn("❌ {} 는 비어 있음", key);
        } else if(mb>20){
            throw new BaseException(ErrorCode.FILE_SIZE_ERROR);
        }else {
            log.info("✅ {} 수신 성공: {}", key, file.getOriginalFilename());
            //key예시 file-0 image-1
            String type = key.split("-")[0];
            return fileService.saveFile(new FileAttachmentDto(file,"survey", null,null,surveyId));
        }
        return null;
    }

}
