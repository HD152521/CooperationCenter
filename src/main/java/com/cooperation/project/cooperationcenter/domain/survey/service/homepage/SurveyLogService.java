package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.dto.LogCsv;
import com.cooperation.project.cooperationcenter.domain.survey.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyLogService {

    private final SurveyFindService surveyFindService;
    private final String origin = "http://localhost:8080/api/v1/file/survey/";

    public AnswerResponse.AnswerPagedDto getAnswerLog(String surveyId, Pageable pageable){
        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        Page<SurveyLog> surveyLog = surveyFindService.getSurveyLogs(survey,pageable);
        //status 추가
        Page<AnswerResponse.LogDto> logs = surveyLog.hasContent()
                ? AnswerResponse.LogDto.from(surveyLog)
                : Page.empty();

        return AnswerResponse.AnswerPagedDto.from(survey,logs);
    }

    public AnswerResponse.AnswerDto getAnswerLog(String surveyId){
        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        List<SurveyLog> surveyLog = surveyFindService.getSurveyLogs(survey);
        //status 추가
        List<AnswerResponse.LogDto> logs = new ArrayList<>();
        if(!surveyLog.isEmpty())  logs = AnswerResponse.LogDto.from(surveyLog);
        return AnswerResponse.AnswerDto.from(survey,logs);
    }

    public AnswerResponse.AnswerLogDto getAnswerLogDetail(String logId){
        //Note 질문이랑 답변 문항들 RESPONSE로 보내야함
        SurveyLog surveyLog = surveyFindService.getSurveyLog(logId);
        Survey survey = surveyLog.getSurvey();
        List<Answer> answers = surveyFindService.getAnswer(surveyLog);
//        List<Answer> response = new ArrayList<>();
//        for(int i=0,cnt=1;i<answers.size();cnt++){
//            if(answers.get(i).getQuestionId()==cnt) response.add(answers.get(i++));
//            else response.add(null);
//        }
        log.info("log Detail 조회 완료");
//        log.info("answer : {}",response.toString());
        return AnswerResponse.AnswerLogDto.from(survey,surveyLog,answers);
    }

    public ResponseEntity<Resource> extractCsv(LogCsv.RequestDto request){
        StringBuilder csvBuilder = new StringBuilder();
        final String UTF8_BOM = "\uFEFF";  // ← 이게 핵심!
        csvBuilder.append(UTF8_BOM);

        List<SurveyLog> logs = surveyFindService.getSurveyLogs(request.logIds());
        if(logs == null){
            //fixme 확인해야함.
            log.warn("log is null");
        }
        Survey survey = Objects.requireNonNull(logs).get(0).getSurvey();
        List<Question> questions = surveyFindService.getQuestions(survey);

        List<String> questionTexts = questions.stream()
                .map(q -> toCsvSafe(q.getQuestion()))
                .toList();
        String questionLine = "no,"+String.join(",", questionTexts);
        questionLine+="\n";
        System.out.println(questionLine);
        csvBuilder.append(questionLine);

        int i=1;
        for(SurveyLog log : logs){
            List<Answer> answers = surveyFindService.getAnswer(log);

            Map<Integer, Answer> answerMap = new TreeMap<>();
            for (Answer answer : answers) {
                answerMap.put(answer.getQuestionId(), answer);
            }

            int maxQuestionId = questions.size();

            List<String> answerTexts = new ArrayList<>(maxQuestionId);
            for (int j = 1; j <= maxQuestionId; j++) {
                Answer a = answerMap.get(j);
                if (a == null) {
                    answerTexts.add(""); // 빈 칸 처리
                } else if (QuestionType.isFile(a.getAnswerType())) {
                    answerTexts.add("\"=HYPERLINK(\"\"" + origin + a.getAnswer().split("_")[0] + "\"\")\"");
                } else if (QuestionType.checkType(a.getAnswerType())) {
                    answerTexts.add(toCsvSafe(surveyFindService.getAnswerFromMultiple(a)));
                } else {
                    answerTexts.add(a.getAnswer());
                }
            }
            String AnswerLine = (i++)+","+String.join(",", answerTexts);
            AnswerLine+="\n";
            System.out.println(AnswerLine);
            csvBuilder.append(AnswerLine);
        }

        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        // 2. 헤더 설정 및 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=survey-logs.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }

    public ResponseEntity<Resource> extractAllCsv(String surveyId){
        StringBuilder csvBuilder = new StringBuilder();
        final String UTF8_BOM = "\uFEFF";  // ← 이게 핵심!
        csvBuilder.append(UTF8_BOM);

        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        List<SurveyLog> logs = surveyFindService.getSurveyLogs(survey);

        if(logs == null){
            //fixme 확인해야함.
            log.warn("log is null");
        }
        List<Question> questions = surveyFindService.getQuestions(survey);

        List<String> questionTexts = questions.stream()
                .map(q -> toCsvSafe(q.getQuestion()))
                .toList();
        String questionLine = "no,"+String.join(",", questionTexts);
        questionLine+="\n";
        System.out.println(questionLine);
        csvBuilder.append(questionLine);

        int i=1;
        for(SurveyLog log : logs){
//            List<Answer> answers = surveyFindService.getAnswer(log);
//            List<String> answerTexts = answers.stream()
//                    .map(a ->{
//                        if(QuestionType.isFile(a.getAnswerType())){
//                            return "\"=HYPERLINK(\"\""+origin+a.getAnswer().split("_")[0]+"\"\")\"";
//                        }else if(QuestionType.checkType(a.getAnswerType())){
//                            return toCsvSafe(surveyFindService.getAnswerFromMultiple(a));
//                        }else{
//                            return a.getAnswer();
//                        }
//                    })
//                    .toList();
            List<Answer> answers = surveyFindService.getAnswer(log);

            Map<Integer, Answer> answerMap = new TreeMap<>();
            for (Answer answer : answers) {
                answerMap.put(answer.getQuestionId(), answer);
            }

            int maxQuestionId = questions.size();

            List<String> answerTexts = new ArrayList<>(maxQuestionId);
            for (int j = 1; j <= maxQuestionId; j++) {
                Answer a = answerMap.get(j);
                if (a == null) {
                    answerTexts.add(""); // 빈 칸 처리
                } else if (QuestionType.isFile(a.getAnswerType())) {
                    answerTexts.add("\"=HYPERLINK(\"\"" + origin + a.getAnswer().split("_")[0] + "\"\")\"");
                } else if (QuestionType.checkType(a.getAnswerType())) {
                    answerTexts.add(toCsvSafe(surveyFindService.getAnswerFromMultiple(a)));
                } else {
                    answerTexts.add(a.getAnswer());
                }
            }
            String AnswerLine = (i++)+","+String.join(",", answerTexts);
            AnswerLine+="\n";
            System.out.println(AnswerLine);
            csvBuilder.append(AnswerLine);
        }

        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        // 2. 헤더 설정 및 응답
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=survey-logs.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }

    private static String toCsvSafe(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
