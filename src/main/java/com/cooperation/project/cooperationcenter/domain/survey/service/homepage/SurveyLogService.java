package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.aliyun.oss.model.OSSObject;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.oss.OssService;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerResponse;
import com.cooperation.project.cooperationcenter.domain.survey.dto.LogCsv;
import com.cooperation.project.cooperationcenter.domain.survey.model.*;
import com.cooperation.project.cooperationcenter.domain.survey.repository.AnswerRepository;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public List<AnswerResponse.LogDto> getAllAnswerLog(){
        List<SurveyLog> surveyLog = surveyFindService.findAllSurveyLog();
        return AnswerResponse.LogDto.from(surveyLog);

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

    public ResponseEntity<StreamingResponseBody> extractCsv(LogCsv.RequestDto request){
        var logs = surveyFindService.getSurveyLogs(request.logIds());
        if (logs == null || logs.isEmpty()) throw new BaseException(ErrorCode.BAD_REQUEST);

        Survey survey = logs.get(0).getSurvey();
        List<Question> questions = surveyFindService.getQuestions(survey);

        StreamingResponseBody body = out -> {
            // Excel UTF-8 BOM
            out.write("\uFEFF".getBytes(StandardCharsets.UTF_8));
            var writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(out, StandardCharsets.UTF_8));

            // 헤더: questionOrder 기준(escape)
            writer.write("no,");
            writer.write(questions.stream().map(q -> toCsvSafe(q.getQuestion())).collect(Collectors.joining(",")));
            writer.write("\n");

            int row = 1;
            for (SurveyLog log : logs) {
                // N+1 방지: findService에서 answers를 fetch join으로 가져오게 하거나 여기서 배치 조회
                List<Answer> answers = surveyFindService.getAnswer(log);

                // questionId -> Answer 매핑
                Map<String, Answer> byQid = answers.stream()
                        .collect(Collectors.toMap(Answer::getQuestionRealId, a -> a, (a,b)->a));

                List<String> cells = new ArrayList<>(questions.size()+1);
                cells.add(String.valueOf(row++));

                // “질문 리스트 순서(questionOrder)”대로 셀 채우기 (size로 1..N 도는 방식 지양)
                for (Question q : questions) {
                    Answer a = byQid.get(q.getQuestionId());
                    if (a == null) { cells.add(""); continue; }

                    if (QuestionType.isFile(a.getAnswerType())) {
                        String id = a.getAnswer().split("_")[0]; // TODO: 안전한 파싱으로 교체 권장
                        // 하이퍼링크 수식 주입 시에도 CSV 인젝션 보호 필요
                        cells.add("\"=HYPERLINK(\"\"" + origin + a.getAnswer().split("_")[0] + "\"\")\"");
                    } else if (QuestionType.checkType(a.getAnswerType())) {
                        cells.add(toCsvSafe(surveyFindService.getAnswerFromMultiple(a)));
                    } else {
                        cells.add(toCsvSafe(a.getAnswer()));
                    }
                }
                writer.write(String.join(",", cells));
                writer.write("\n");
                writer.flush(); // 청크 단위로 흘려보내기
            }
            writer.flush();
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition("survey-logs.csv"))
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(body);
    }


    private static String contentDisposition(String filename) {
        // RFC 5987 방식 + 기본 filename 함께 제공
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");
        return "attachment; filename=\"" + filename + "\"; filename*=UTF-8''" + encoded;
    }

    public ResponseEntity<StreamingResponseBody> extractAllCsv(String surveyId){
        final String fileBaseUrl = origin; // 기존 origin 사용
        final Survey survey = surveyFindService.getSurveyFromId(surveyId);
        final List<SurveyLog> logs = surveyFindService.getSurveyLogs(survey);
        if (logs == null || logs.isEmpty()) {
            throw new BaseException(ErrorCode.BAD_REQUEST);
        }
        final List<Question> questions = surveyFindService.getQuestions(survey);

        StreamingResponseBody body = out -> {
            // 엑셀 호환 BOM
            out.write("\uFEFF".getBytes(StandardCharsets.UTF_8));

            try (BufferedWriter writer =
                         new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {

                // 1) 헤더 (질문 순서대로)
                String header = "no," + questions.stream()
                        .map(q -> toCsvSafe(q.getQuestion()))
                        .collect(Collectors.joining(","));
                writer.write(header);
                writer.write("\n");
                writer.flush();

                // 2) 본문
                int row = 1;
                for (SurveyLog log : logs) {
                    // N+1이면 surveyFindService.getAnswer(log) 쪽 fetch join/일괄조회 최적화 권장
                    List<Answer> answers = surveyFindService.getAnswer(log);

                    // 질문ID(real) -> 답변 맵 (※ Answer에 getQuestionRealId가 없으면 getQuestionId로 대체)
                    Map<String, Answer> byQid = new HashMap<>();
                    for (Answer a : answers) {
                        String key = a.getQuestionRealId(); // 없으면 a.getQuestionId()
                        byQid.put(key, a);
                    }

                    List<String> cells = new ArrayList<>(questions.size() + 1);
                    cells.add(String.valueOf(row++));

                    // 질문 리스트 순서(questionOrder)대로 채우기
                    for (Question q : questions) {
                        Answer a = byQid.get(q.getQuestionId());
                        if (a == null) {
                            cells.add("");
                            continue;
                        }

                        if (QuestionType.isFile(a.getAnswerType())) {
                            // 파일 셀은 의도적으로 HYPERLINK 수식 사용
                            String fileKey = safeFirstToken(a.getAnswer()); // "id_rest" 형태 보호
                            cells.add("\"=HYPERLINK(\"\"" + fileBaseUrl + fileKey + "\"\")\"");
                        } else if (QuestionType.checkType(a.getAnswerType())) {
                            cells.add(toCsvSafe(surveyFindService.getAnswerFromMultiple(a)));
                        } else {
                            // 일반 텍스트는 CSV 인젝션 방지 + CSV escape
                            cells.add(toCsvSafe(preventCsvInjection(a.getAnswer())));
                        }
                    }

                    writer.write(String.join(",", cells));
                    writer.write("\n");
                    writer.flush(); // 청크로 바로바로 전송
                }
                writer.flush();
            }
        };

        ContentDisposition cd = ContentDisposition.attachment()
                .filename("survey-logs.csv", StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(body);
    }

    private static String preventCsvInjection(String s) {
        if (s == null || s.isEmpty()) return "";
        char c = s.charAt(0);
        if (c == '=' || c == '+' || c == '-' || c == '@' || c == '\t') {
            return "'" + s;
        }
        return s;
    }

    private static String safeFirstToken(String v) {
        if (v == null || v.isBlank()) return "";
        int idx = v.indexOf('_');
        String token = (idx >= 0) ? v.substring(0, idx) : v;
        return token.replaceAll("[\\r\\n\\t\\x00-\\x1F\\x7F]", "");
    }

    public ResponseEntity<StreamingResponseBody> extractFileStudent(String surveyId){
        log.info("학생 폴더 출력 start...");
        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        List<SurveyLog> logs = surveyFindService.getSurveyLogs(survey);

        StreamingResponseBody body = out -> {
            try (ZipOutputStream zos = new ZipOutputStream(out)) {
                int idx = 1;
                for (SurveyLog log : logs) {
                    String memberName = log.getMember() != null ? log.getMember().getMemberName() : "unknown";
                    String folder = idx++ + "_" + memberName + "/";

                    // N+1 주의: answers를 fetch join으로 미리 가져오게
                    for (Answer a : surveyFindService.getAnswer(log)) {
                        if (!(a.getAnswerType() == QuestionType.FILE || a.getAnswerType() == QuestionType.IMAGE)) continue;

                        FileAttachment f = a.getSurveyFile();
                        if (f == null) continue;

                        if (!ossService.isFileExist(f)) {
                            System.out.println("file missing: "+f.getStoredName());
                            continue; }

                        String entryName = folder + "Q" + a.getQuestionId() + "_" + f.getOriginalName();
                        zos.putNextEntry(new ZipEntry(entryName));
                        try (var obj = ossService.getObject(f); InputStream in = obj.getObjectContent()) {
                            in.transferTo(zos);
                        }
                        zos.closeEntry();
                    }
                }
                zos.finish();
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition(surveyId + "_logs.zip"))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(body);
    }

    public ResponseEntity<StreamingResponseBody> extractFileSurvey(String surveyId){
        log.info("설문조사 폴더 출력 start...");
        Survey survey = surveyFindService.getSurveyFromId(surveyId);

        // 미리 필요한 질문만 필터
        List<Question> questions = survey.getQuestions().stream()
                .filter(q -> q.getQuestionType() == QuestionType.FILE || q.getQuestionType() == QuestionType.IMAGE)
                .toList();

        StreamingResponseBody body = out -> {
            try (ZipOutputStream zos = new ZipOutputStream(out)) {
                for (Question question : questions) {
                    // ⚠️ N+1이면 여기서 answers 일괄조회/페치조인으로 최적화 권장
                    List<Answer> answers = answerRepository.findAnswerByQuestionRealId(question.getQuestionId());

                    int idx = 1;
                    for (Answer a : answers) {
                        FileAttachment file = a.getSurveyFile();
                        if (file == null) continue;

                        // (선택) 존재 확인 호출이 비싸면 getObject 404로 분기하거나 스킵
                        if (!ossService.isFileExist(file)) {
                            log.debug("file missing: {}", file.getStoredName());
                            continue;
                        }

                        String entryName = "Q" + question.getQuestionOrder() + "/"
                                + idx++ + "_" + file.getOriginalName();

                        zos.putNextEntry(new ZipEntry(entryName));
                        try (OSSObject obj = ossService.getObject(file);
                             InputStream in = obj.getObjectContent()) {
                            in.transferTo(zos);
                        }
                        zos.closeEntry();
                    }
                }
                zos.finish();
            }
        };

        ContentDisposition cd = ContentDisposition.attachment()
                .filename(surveyId + ".zip", StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(body);
    }


    private static String toCsvSafe(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
