package com.cooperation.project.cooperationcenter.domain.student.service;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.cooperation.project.cooperationcenter.domain.student.repository.StudentRepository;
import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;

    public void addStudentBySurvey(List<Question> questionList, List<Answer> savedAnswer, Member member){
        log.info("before changing answer to Student");
        int questionLen = questionList.size();
        int answerLen = savedAnswer.size();
        if(questionLen!=answerLen) log.info("문항 답변 개수 다름");

        log.info("=======original Answer List========");
        for(Answer an : savedAnswer){
            log.info("values:{}",an.getAnswer());
        }


        SurveyLog surveyLog = savedAnswer.get(0).getSurveyLog();
        Map<String, Answer> answerByQid = savedAnswer.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getQuestionRealId() != null)
                .collect(Collectors.toMap(
                        Answer::getQuestionRealId,
                        Function.identity(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        log.info("=======AnswerQid List========");
        for(Answer an : answerByQid.values()){
            log.info("an:{}",an.getAnswer());
        }

        Map<String,String> domainMap = new LinkedHashMap<>();

        for(Question q : questionList){
            log.info("q:{} / {}",q.getQuestion(),q.isTemplate());
            if(!q.isTemplate()) continue;
            String domainField = q.getDomainField();
            Answer answer = answerByQid.get(q.getQuestionId());
            String generateAnswer = answer.getAnswer();

            domainMap.put(domainField,generateAnswer);
        }
        log.info("=======domain List========");
        for(String str : domainMap.values()){
            log.info("values:{}",str);
        }

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StudentRequest.MappingDto dto = mapper.convertValue(domainMap, StudentRequest.MappingDto.class);
        log.info("stduent:{}",dto.toString());
        Student student = Student.from(dto,surveyLog,member);

        saveStudent(student);
        log.info("after changing answer to Student");

    }

    @Transactional
    public void saveStudent(Student student){
        try{
            studentRepository.save(student);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }

}
