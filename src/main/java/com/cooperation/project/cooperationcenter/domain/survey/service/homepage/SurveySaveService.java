package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.model.*;
import com.cooperation.project.cooperationcenter.domain.survey.repository.QuestionOptionRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.QuestionRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyFolderRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyRepository;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveySaveService {

    //repo
    private final SurveyRepository surveyRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final SurveyFindService surveyFindService;
    private final SurveyFolderRepository surveyFolderRepository;

    @Transactional
    public void saveSurvey(SurveyRequest.SurveyDto request){
        log.info("{}",request);
        Survey survey = SurveyRequest.SurveyDto.toEntity(request);

        SurveyFolder surveyFolder = loadSurveyFolderById(request.folderId());
        survey.setSurveyFolder(surveyFolder);

        List<Question> questions = getQuestionsFromDto(request.questions(),survey);
        List<QuestionOption> options = getQuestionOptionFromDto(request.questions(),questions,survey);
        save(survey,questions,options);
    }

    public SurveyFolder loadSurveyFolderById(String fileId){
        try{
            return surveyFolderRepository.findByFolderId(fileId).orElseThrow(
                    () -> new BaseException(ErrorCode.BAD_REQUEST)
            );
        }catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    @Transactional
    public void editSurvey(SurveyEditDto request){
        log.info("data:{}",request.toString());
        Survey survey = surveyFindService.getSurveyFromId(request.surveyId());
        survey.setByDto(request);

        List<Question> questions = getQuestionsFromDto(request.questions(),survey);
        deleteRemovedQuestions(survey, questions);

        List<QuestionOption> options = getQuestionOptionFromDto(request.questions(),questions,survey);
        save(survey,questions,options);
    }

    public void deleteRemovedQuestions(Survey survey, List<Question> questions){
        List<Question> surveyQuestions = survey.getQuestions();

        Set<Long> submittedIds = questions.stream()
                .map(Question::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Question> toDelete = surveyQuestions.stream()
                .filter(q -> q.getId() != null && !submittedIds.contains(q.getId()))
                .toList();

        for (Question q : toDelete) {
            survey.removeQuestion(q); // 양방향 연관관계라면 필요
            questionRepository.delete(q);
        }
    }

    @Transactional
    public void save(Survey survey, List<Question> questions, List<QuestionOption> options){
            surveyRepository.save(survey);
            questionRepository.saveAll(questions);
            questionOptionRepository.saveAll(options);
            log.info("설문조사 저장 완료");
    }
//HtmlUtils.htmlEscape(question);
    public List<Question> getQuestionsFromDto(List<QuestionDto> request, Survey survey){
        List<Question> questions = new ArrayList<>();
        int i = 1;
        for(QuestionDto dto : request){
            if(dto.questionId()!=null){
                //note 원래 있던 질문들
                Question question = surveyFindService.getQuestion(dto.questionId());
                if (question!=null) {
                    question.setQuestion(HtmlUtils.htmlEscape(dto.question()));
                    question.setQuestionDescription(dto.description());
                    QuestionType questionType = QuestionType.fromType(dto.type());
                    question.setQuestionType(questionType);
                    question.setOption(QuestionType.checkType(questionType));
                    question.setQuestionOrder(i++);
                    question.setDomainField(dto.domainField());
                    question.setTemplate(dto.isTemplate());
                    questions.add(question);

                    survey.removeQuestion(question);
                    survey.setQuestion(question);
                }
                continue;
            }

            QuestionType type = QuestionType.fromType(dto.type());
            Question question = Question.builder()
                    .questionDescription(dto.description())
                    .questionType(type)
                    .survey(survey)
                    .question(HtmlUtils.htmlEscape(dto.question()))
                    .questionOrder(i++)
                    .template(dto.isTemplate())
                    .domainField(dto.domainField())
                    .template(dto.isTemplate())
                    .build();
            questions.add(question);
            survey.setQuestion(question);

            log.info("questino is Option:{}",question.getOptions());
        }
        return questions;
    }

    @Transactional
    public List<QuestionOption> getQuestionOptionFromDto(List<QuestionDto> requestDtos, List<Question> questions,Survey survey){
        List<QuestionOption> options = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            QuestionDto dto = requestDtos.get(i);
            log.info("null:{} options:{}",q.isOption(),q.getOptions());
            if (q.isOption()) {
                for (OptionDto optionText : dto.options()) {

                    //fixme id값 있을 경우
                    QuestionOption questionOption = questionOptionRepository.findQuestionOptionById(optionText.optionId());
                    if(questionOption!=null){
                        questionOption.setOptionText(HtmlUtils.htmlEscape(optionText.text()));
                        questionOption.setNextQuestionId(optionText.nextQuestion());
                        questionOption.setRealNextQuestionId(optionText.realNextQuestion());
                        questionOption.setParentOptionId(optionText.parentOptionId());
                        questionOption.setLevel(optionText.level());
                        questionOption.setHierarchyId(optionText.hierarchyId());
                        questionOptionRepository.save(questionOption);

                        survey.removeOption(questionOption);
                        survey.setOptions(questionOption);
                        q.removeOption(questionOption);
                        q.setOptions(questionOption);
                        continue;
                    }

                    QuestionOption option = QuestionOption.builder()
                            .text(HtmlUtils.htmlEscape(optionText.text()))
                            .nextQuestionId(optionText.nextQuestion())
                            .realNextQuestionId(q.getQuestionId())
                            .survey(survey)
                            .question(q)
                            .parentOptionId(optionText.parentOptionId())
                            .level(optionText.level())
                            .hierarchyId(optionText.hierarchyId())
                            .build();
                    options.add(option);
                    q.setOptions(option);
                    survey.setOptions(option);
                }
            }
        }
        return options;
    }

    public AnswerPageDto getSurveys(String surveyId){
        List<QuestionDto> response = new ArrayList<>();
        Survey survey = surveyFindService.getSurveyFromId(surveyId);

        boolean expired = (survey.getEndDate() != null) && LocalDate.now().isAfter(survey.getEndDate());  // today > endDate

        if(!survey.isShare()) throw new BaseException(ErrorCode.SURVEY_NOT_SHARE);
        if(expired) throw new BaseException(ErrorCode.SURVEY_DATE_NOT_VALID);

        List<Question> questions = surveyFindService.getQuestions(survey);
        List<QuestionOption> options = surveyFindService.getOptions(survey);

        for(Question q : questions){
            response.add(
                    new QuestionDto(
                            q.getQuestionId(),
                            q.getQuestionType().toString().toLowerCase(),
                            q.getQuestion(),
                            q.getQuestionDescription(),
                            OptionDto.to(q.getOptions()),
                            q.getQuestionOrder(),
                            q.isTemplate(),
                            q.getDomainField()
                    )
            );
        }

        return new AnswerPageDto(survey.getSurveyTitle(),survey.getSurveyDescription(),response);
    }




    @Transactional
    public void deleteSurvey(String surveyId){
        Survey survey = surveyFindService.getSurveyFromId(surveyId);
        SurveyFolder surveyFolder = survey.getSurveyFolder();
        log.info("delete survey:{}",survey.getSurveyId());
        try {
            for (Question question : survey.getQuestions()) {
                questionOptionRepository.deleteAll(question.getOptions());
                question.getOptions().clear(); // 메모리 내 컬렉션도 정리
            }
        }catch (Exception e){
            log.warn(e.getMessage());
            log.warn("option delete fail...");
        }
        survey.getOptions().clear();

        try {
            questionRepository.deleteAll(survey.getQuestions());
            survey.getQuestions().clear();
        }catch (Exception e){
            log.warn(e.getMessage());
            log.warn("question 삭제 실패");
        }

        surveyFolder.deleteSurvey(survey);
        surveyRepository.delete(survey);
        log.info("survey delete success...");
    }

    @Transactional
    public Survey copySurvey(String originalSurveyId) {
        Survey original = surveyFindService.getSurveyFromId(originalSurveyId);
        String copyTitle = original.getSurveyTitle() + " - 복사본("+(original.getCopyCnt()+1)+")";

        Survey copy = Survey.builder()
                .surveyTitle(copyTitle)
                .surveyDescription(original.getSurveyDescription())
                .owner(original.getOwner())
                .startDate(original.getStartDate())
                .endDate(original.getEndDate())
                .surveyType(original.getSurveyType())
                .surveyFolder(original.getSurveyFolder())
                .build();
        surveyRepository.save(copy);

        original.copyCntPlus();
        surveyRepository.save(original);

        for (Question originalQ : original.getQuestions()) {
            Question newQ = Question.builder()
                    .questionType(originalQ.getQuestionType())
                    .questionDescription(originalQ.getQuestionDescription())
                    .isNecessary(originalQ.isNecessary())
                    .question(originalQ.getQuestion())
                    .survey(copy)
                    .domainField(originalQ.getDomainField())
                    .template(originalQ.isTemplate())
                    .questionOrder(originalQ.getQuestionOrder())
                    .build();
            questionRepository.save(newQ);

            for (QuestionOption originalOpt : originalQ.getOptions()) {
                QuestionOption newOpt = QuestionOption.builder()
                        .text(originalOpt.getOptionText())
                        .nextQuestionId(originalOpt.getNextQuestionId())
                        .question(newQ)
                        .survey(copy)
                        .build();
                questionOptionRepository.save(newOpt);
            }
        }

        return copy;
    }

    public List<QuestionDto> getTemplate(String type){
        Survey.SurveyType surveyType = Survey.SurveyType.getSruveyType(type);
        if(surveyType.equals(Survey.SurveyType.STUDENT)){
            List<QuestionDto> questions = getStudentTempalte();
            log.info("questions:{}",questions);
            return questions;
        }else{
            return null;
        }
    }

    public List<QuestionDto> getStudentTempalte(){
        return List.of(
                new QuestionDto(
                        null,
                        "hierarchy",
                        "학/석사 선택",
                        "",
                        List.of(
                                // ====== Level 1 (학사 / 석사) ======
                                new OptionDto(0, null, "학사", null, null, "1-1", 1),
                                new OptionDto(0, null, "석사", null, null, "1-2", 1),

                                // ====== Level 2: 학사 계열 단과대학 ======
                                new OptionDto(0, null, "소프트웨어융합대학", null, "1-1", "2-1", 2),
                                new OptionDto(0, null, "자연과학대학", null, "1-1", "2-2", 2),

                                // ====== Level 3: 소프트웨어융합대학 학과 ======
                                new OptionDto(0, null, "컴퓨터공학과", null, "2-1", "3-1", 3),
                                new OptionDto(0, null, "지능기전공학과", null, "2-1", "3-2", 3),

                                // ====== Level 3: 자연과학대학 학과 ======
                                new OptionDto(0, null, "물리천문학과", null, "2-2", "3-3", 3),
                                new OptionDto(0, null, "수학통계학과", null, "2-2", "3-4", 3),

                                // ====== Level 2: 석사 계열 단과대학 ======
                                new OptionDto(0, null, "자연과학", null, "1-2", "2-3", 2),
                                new OptionDto(0, null, "공학", null, "1-2", "2-4", 2),

                                // ====== Level 3: 석사 계열 학과 ======
                                new OptionDto(0, null, "수학과", null, "2-3", "3-5", 3),
                                new OptionDto(0, null, "응용통계학과", null, "2-3", "3-6", 3),
                                new OptionDto(0, null, "건축공학과", null, "2-4", "3-7", 3),
                                new OptionDto(0, null, "건설환경공학과", null, "2-4", "3-8", 3)
                        ),
                        0,
                        true,
                        "degreeZip"
                )
                ,
                new QuestionDto(null, "short", "중국어이름", "", null, 0, true, "chineseName"),
                new QuestionDto(null, "short", "영문이름", "", null, 0, true, "englishName"),
                new QuestionDto(null, "date", "생년월일", "", null, 0, true, "birthDate"),
                new QuestionDto(null, "multiple", "성별", "", List.of(
                        new OptionDto(0, null, "남성", null,null,null,0),
                        new OptionDto(0, null, "여성", null,null,null,0)
                ), 0, true, "gender"),

                new QuestionDto(null, "short", "학생 메일", "", null, 0, true, "studentEmail"),
                new QuestionDto(null, "short", "여권 번호", "", null, 0, true, "passportNumber"),
                new QuestionDto(null, "short", "수험 번호", "", null, 0, true, "examNumber"),
                new QuestionDto(null, "short", "유학원 담당자 위챗", "", null, 0, true, "agentWechat"),
                new QuestionDto(null, "short", "유학원 담당자 이메일", "", null, 0, true, "agentEmail"),
                new QuestionDto(null, "short", "긴급연락처", "", null, 0, true, "emergencyContactNum")
        );
    }

}
