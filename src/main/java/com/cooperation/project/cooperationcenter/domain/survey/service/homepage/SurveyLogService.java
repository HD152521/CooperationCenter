package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.aliyun.oss.model.OSSObject;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.oss.OssService;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.dto.LogCsv;
import com.cooperation.project.cooperationcenter.domain.survey.model.*;
import com.cooperation.project.cooperationcenter.domain.survey.repository.AnswerRepository;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyLogService {

    private final SurveyFindService surveyFindService;
    private final String origin = "http://localhost:8080/api/v1/file/survey/";
    private final AnswerRepository answerRepository;
    private final OssService ossService;

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

    public ResponseEntity<byte[]> extractFileStudent(String surveyId){
        log.info("학생 폴더 출력 start...");
        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            List<SurveyLog> logs = surveyFindService.getSurveyLogs(survey);
            int logIndex = 1;
            for (SurveyLog log : logs) {
                String memberName = log.getMember().getMemberName();
                String logFolder = logIndex + "_" + memberName + "/";

                List<Answer> answers = log.getAnswers();

                for (Answer answer : answers) {
                    System.out.println("teset: "+answer.getAnswerType());
                    if(!(answer.getAnswerType().equals(QuestionType.FILE)||answer.getAnswerType().equals(QuestionType.IMAGE))) continue;

                    FileAttachment file = answer.getSurveyFile();
                    System.out.println("filename:"+file.getStoredName());

                    if(!ossService.isFileExist(file)){
                        System.out.println("해당 파일 없음");
                        continue;
                    }


                    String fileName = "Q" + answer.getQuestionId() + "_" + file.getOriginalName();
                    String zipEntryName = logFolder + fileName;
                    zos.putNextEntry(new ZipEntry(zipEntryName));

                    try (OSSObject ossObject = ossService.getObject(file);
                         InputStream inputStream = ossObject.getObjectContent()) {
                        inputStream.transferTo(zos);
                    }
                    zos.closeEntry();
                }
                logIndex++;
            }
            zos.finish();

        } catch (IOException e) {
            log.warn(e.getMessage());
            throw new RuntimeException(e);
        }

        byte[] zipBytes = baos.toByteArray();
        log.info("zip 생성 완료");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + surveyId + "_logs.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipBytes.length)
                .body(zipBytes);
    }

    public ResponseEntity<byte[]> extractFileSurvey(String surveyId){
        log.info("설문조사 폴더 출력 start...");
        Survey survey =surveyFindService.getSurveyFromId(surveyId);;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {

            List<Question> questions = survey.getQuestions();

            for (Question question : questions) {
                if(!(question.getQuestionType().equals(QuestionType.FILE)||question.getQuestionType().equals(QuestionType.IMAGE))) continue;
                List<Answer> answers = answerRepository.findAnswerByQuestionRealId(question.getQuestionId());
                List<FileAttachment> files = answers.stream().map(Answer::getSurveyFile).toList();

                int index = 1;
                for (FileAttachment file : files) {

                    if(!ossService.isFileExist(file)){
                        System.out.println("해당 파일 없음");
                        continue;
                    }

                    String zipEntryName = "Q" + question.getQuestionOrder() + "/" + index + "_" + file.getOriginalName();
                    zos.putNextEntry(new ZipEntry(zipEntryName));

                    try (OSSObject ossObject = ossService.getObject(file);
                         InputStream inputStream = ossObject.getObjectContent()) {
                        inputStream.transferTo(zos);
                    }

                    zos.closeEntry();
                    index++;
                }
            }
            zos.finish(); // 명시적 종료
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw new RuntimeException(e);
        }

        byte[] zipBytes = baos.toByteArray();
        log.info("zip 생성 완료");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + surveyId + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipBytes.length)
                .body(zipBytes);
    }


    private static String toCsvSafe(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
