package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.*;
import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionOption;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionType;
import com.cooperation.project.cooperationcenter.domain.survey.model.Survey;
import com.cooperation.project.cooperationcenter.domain.survey.repository.QuestionOptionRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.QuestionRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void saveSurvey(SurveyRequest.SurveyDto request){
        log.info("{}",request);
        Survey survey = SurveyRequest.SurveyDto.toEntity(request);
        List<Question> questions = getQuestionsFromDto(request.questions(),survey);
        List<QuestionOption> options = getQuestionOptionFromDto(request.questions(),questions,survey);
        save(survey,questions,options);
    }

    @Transactional
    public void editSurvey(SurveyEditDto request){
        log.info("data:{}",request.toString());
        Survey survey = getSurveyFromId(request.surveyId());
        survey.updateFromEditDto(request);

        List<Question> questions = getQuestionsFromDto(request.questions(),survey);
        deleteRemovedQuestions(survey, questions);
        //fixme option은 조금 더 나중에 하자
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
        try {
            surveyRepository.save(survey);
            questionRepository.saveAll(questions);
            questionOptionRepository.saveAll(options);
            log.info("설문조사 저장 완료");
        }catch (Exception e){
            log.warn(e.getMessage());
            log.warn("설문조사 저장 실패");
        }
    }

    public List<Question> getQuestionsFromDto(List<QuestionDto> request, Survey survey){
        List<Question> questions = new ArrayList<>();
        int i = 1;
        for(QuestionDto dto : request){
            if(dto.questionId()!=null){
                //note 원래 있던 질문들
                Question question = getQuestion(dto.questionId());
                if (question!=null) {
                    question.setQuestion(dto.question());
                    question.setQuestionDescription(dto.description());
                    QuestionType questionType = QuestionType.fromType(dto.type());
                    question.setQuestionType(questionType);
                    question.setOption(QuestionType.checkType(questionType));
                    question.setQuestionOrder(i++);
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
                    .question(dto.question())
                    .build();
            questions.add(question);
            survey.setQuestion(question);
        }
        return questions;
    }

    public List<QuestionOption> getQuestionOptionFromDto(List<QuestionDto> requestDtos, List<Question> questions,Survey survey){
        List<QuestionOption> options = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            QuestionDto dto = requestDtos.get(i);

            if (q.isOption()) {
                for (String optionText : dto.options()) {
                    QuestionOption option = QuestionOption.builder()
                            .text(optionText.text())
                            .nextQuestionId(optionText.nextQuestion())
                            .realNextQuestionId(q.getQuestionId())
                            .survey(survey)
                            .question(q)
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
        Survey survey = getSurveyFromId(surveyId);
        List<Question> questions = getQuestions(survey);
        List<QuestionOption> options = getOptions(survey);

        log.info("option empty?:{}",options.isEmpty());

        Map<Long, List<String>> optionMap = new HashMap<>();
        for (QuestionOption opt : options) {
            Long questionId = opt.getQuestion().getId();
            log.info("questionId:{}",questionId);
            optionMap.computeIfAbsent(questionId, k -> new ArrayList<>())
                    .add(opt.getOptionText());
        }

        for(Question q : questions){
            response.add(
                    new QuestionDto(
                            q.getId(),
                            q.getQuestionType().toString().toLowerCase(),
                            q.getQuestion(),
                            q.getQuestionDescription(),
                            optionList,
                            q.isNecessary()
                    )
            );
        }

        return new AnswerPageDto(survey.getSurveyTitle(),survey.getSurveyDescription(),response);
    }


    public Survey getSurveyFromId(Long surveyId){
        try{
            return surveyRepository.findSurveyById(surveyId);
        }catch (Exception e){
            log.warn("getSurveyFormId Fail");
            return null;
        }
    }

    public Survey getSurveyFromId(String surveyId){
        try{
            return surveyRepository.findSurveyBySurveyId(surveyId);
        }catch (Exception e){
            log.warn("getSurveyForm survey Id Fail");
            return null;
        }
    }

    public List<Question> getQuestions(Survey survey){
        try{
            return questionRepository.findQuestionsBySurvey(survey);
        }catch(Exception e){
            log.warn("getQuestionBySurvey failed...");
            return null;
        }
    }

    public Question getQuestion(Long id){
        try{
            return questionRepository.findQuestionById(id);
        }catch (Exception e){
            log.warn("getQuestionById failed...");
            return null;
        }
    }

    public List<QuestionOption> getOptions(Survey survey){
        try{
            log.info("surveyId:{}",survey.getId());
            return questionOptionRepository.findQuestionOptionsBySurvey(survey);
        }catch (Exception e){
            log.warn("getOptionsBy survey and question failed...");
            return null;
        }
    }

    public List<SurveyResponseDto> getAllSurvey(){
        List<SurveyResponseDto> response = new ArrayList<>();
        List<Survey> surveys = getAllSurveyFromDB();
        for(Survey survey : surveys){
            LocalDate now = LocalDate.now();
            int daysLeft = (survey.getEndDate()==null) ? 0 : Period.between(now, survey.getEndDate()).getDays();
            boolean isBefore = survey.getStartDate() != null && now.isBefore(survey.getStartDate());
            response.add(
                    new SurveyResponseDto(
                        survey.getSurveyTitle(),
                            survey.getCreatedAt(),
                            survey.getParticipantCount(),
                            daysLeft,
                            survey.getSurveyId(),
                            isBefore
                    )
            );
        }
        log.info("response:{}",response.get(0).toString());
        return response;
    }

    public List<Survey> getAllSurveyFromDB(){
        try{
            return surveyRepository.findAll();
        }catch (Exception e){
            log.warn("get all survey failed...");
            return null;
        }
    }

    @Transactional
    public void deleteSurvey(String surveyId){
        Survey survey = getSurveyFromId(surveyId);
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

        surveyRepository.delete(survey);
        log.info("survey delete success...");
    }

    @Transactional
    public Survey copySurvey(String originalSurveyId) {
        Survey original = getSurveyFromId(originalSurveyId);
        String copyTitle = original.getSurveyTitle() + " - 복사본("+(original.getCopyCnt()+1)+")";

        Survey copy = Survey.builder()
                .surveyTitle(copyTitle)
                .surveyDescription(original.getSurveyDescription())
                .owner(original.getOwner())
                .startDate(original.getStartDate())
                .endDate(original.getEndDate())
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

}
