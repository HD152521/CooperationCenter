package com.cooperation.project.cooperationcenter.domain.student.service;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.student.dto.StudentResponse;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.cooperation.project.cooperationcenter.domain.student.repository.StudentRepository;
import com.cooperation.project.cooperationcenter.domain.student.repository.StudentRepositoryCustom;
import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentRepositoryCustom studentRepositoryCustom;

    private String[] headers = {
            "ID","중문명","영문명","생년월일","성별","이메일",
            "여권번호","수험번호","에이전트 WeChat","에이전트 이메일",
            "비상연락처","소속(Agency)"
    };

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
            if(answer == null){
                log.warn("템플릿 문항에 대한 답변이 없습니다. questionId:{}, question:{}", q.getQuestionId(), q.getQuestion());
                continue;
            }
            String generateAnswer = answer.getAnswer();
            if(generateAnswer == null){
                log.warn("템플릿 문항 답변 값이 null 입니다. questionId:{}, question:{}", q.getQuestionId(), q.getQuestion());
                continue;
            }

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

    public byte[] exportStudentsExcel(StudentRequest.ConditionDto condition){
        try{
            List<StudentResponse.ListDto> rows = getStudentDtoByCondition(condition);
            return buildExcel(rows);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    @Transactional
    public void saveStudent(Student student){
        try{
            studentRepository.save(student);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }

    public Page<StudentResponse.ListDto> getStudentDtoPageByCondition(StudentRequest.ConditionDto condition, Pageable pageable){
        try{
            return StudentResponse.ListDto.from(loadStudentPageByCondition(condition,pageable));
        }catch (Exception e){
            log.warn(e.getMessage());
            return Page.empty();
        }
    }

    public List<StudentResponse.ListDto> getStudentDtoByCondition(StudentRequest.ConditionDto condition){
        try{
            return StudentResponse.ListDto.from(loadStudentDtoByCondition(condition));
        }catch (Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }


    public Page<Student> loadStudentPageByCondition(StudentRequest.ConditionDto condition, Pageable pageable){
        try{
            return studentRepositoryCustom.loadStudentPageByCondition(condition,pageable);
        }catch (Exception e){
            log.warn(e.getMessage());
            return Page.empty();
        }
    }

    public List<Student> loadStudentDtoByCondition(StudentRequest.ConditionDto condition){
        try{
            return studentRepositoryCustom.loadStudentDtoByCondition(condition);
        }catch (Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<StudentResponse.ListDto> getAllStudentDto(){
        try{
            return StudentResponse.ListDto.from(loadAllStudent());
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Student> loadAllStudent(){
        try{
            return studentRepository.findAll();
        }catch (Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public StudentResponse.ListDto getStudentDtoById(Long id){
        try{
            return StudentResponse.ListDto.from(loadStudentById(id));
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }
    
    public Student loadStudentById(Long id){
        try{
            return studentRepository.findById(id).orElseThrow(
                    () -> new BaseException(ErrorCode._BAD_REQUEST)
            );
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    private byte[] buildExcel(List<StudentResponse.ListDto> rows) throws IOException {
        if (rows == null) rows = java.util.Collections.emptyList();


        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (SXSSFWorkbook wb = new SXSSFWorkbook(500);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            SXSSFSheet sh = (SXSSFSheet) wb.createSheet("Students");

            sh.trackAllColumnsForAutoSizing();

            // 헤더 스타일
            CellStyle headerStyle = wb.createCellStyle();
            Font bold = wb.createFont(); bold.setBold(true);
            headerStyle.setFont(bold);
            headerStyle.setWrapText(false);

            // 헤더 작성
            Row h = sh.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = h.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // 본문
            int r = 1;
            for (StudentResponse.ListDto s : rows) {
                Row row = sh.createRow(r++);
                int col = 0;

                row.createCell(col++).setCellValue(s.studentId() == null ? "" : String.valueOf(s.studentId()));
                row.createCell(col++).setCellValue(nvl(s.chineseName()));
                row.createCell(col++).setCellValue(nvl(s.englishName()));
                row.createCell(col++).setCellValue(s.birthDate() == null ? "" : df.format(s.birthDate()));
                row.createCell(col++).setCellValue(s.gender() == null ? "" : s.gender().name());
                row.createCell(col++).setCellValue(nvl(s.studentEmail()));
                row.createCell(col++).setCellValue(nvl(s.passportNumber()));
                row.createCell(col++).setCellValue(nvl(s.examNumber()));
                row.createCell(col++).setCellValue(nvl(s.agentWechat()));
                row.createCell(col++).setCellValue(nvl(s.agentEmail()));
                row.createCell(col++).setCellValue(nvl(s.emergencyContactNum()));
                row.createCell(col++).setCellValue(nvl(s.memberName()));
            }

            // ✅ 모든 데이터 쓴 뒤에 autosize 수행
            for (int i = 0; i < headers.length; i++) {
                sh.autoSizeColumn(i); // 필요시: sh.autoSizeColumn(i, true)
                // 너무 좁아지는 것 방지
                int width = Math.min(sh.getColumnWidth(i) + 512, 255 * 256);
                sh.setColumnWidth(i, width);
            }

            // 선택: 헤더 고정
            sh.createFreezePane(0, 1);

            wb.write(out);
            wb.dispose(); // 임시파일 정리
            return out.toByteArray();
        }
    }


    private String nvl(String s) {
        return (s == null) ? "" : s;
    }

    public static String encodeAttachmentFilename(String filename) {
        return "attachment; filename*=UTF-8''" +
                URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
    }
}
