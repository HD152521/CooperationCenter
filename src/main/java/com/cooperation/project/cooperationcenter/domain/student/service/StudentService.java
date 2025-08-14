package com.cooperation.project.cooperationcenter.domain.student.service;

import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
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

    public void addStudentBySurvey(List<Question> questionList, List<Answer> savedAnswer){
        log.info("before changing answer to Student");
        int questionLen = questionList.size();
        int answerLen = savedAnswer.size();
        if(questionLen!=answerLen) log.info("문항 답변 개수 다름");

        Map<String, Answer> answerByQid = savedAnswer.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getQuestionRealId() != null)
                .collect(Collectors.toMap(
                        Answer::getQuestionRealId,
                        Function.identity(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        Map<String,String> domainMap = new LinkedHashMap<>();

        for(Question q : questionList){
            if(q.getDomainField()==null) continue;
            String domainField = q.getDomainField();
            Answer answer = answerByQid.get(q.getQuestionId());
            String generateAnswer = answer.getAnswer();

            domainMap.put(domainField,generateAnswer);
        }

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StudentRequest.MappingDto dto = mapper.convertValue(domainMap, StudentRequest.MappingDto.class);
        saveStudent(dto);

        log.info("after changing answer to Student");
        log.info("stduent:{}",dto.toString());
    }

    @Transactional
    public void saveStudent(StudentRequest.MappingDto request){

    }

}
