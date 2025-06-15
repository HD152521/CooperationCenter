package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerPageDto;
import com.cooperation.project.cooperationcenter.domain.survey.dto.QuestionDto;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyRequest;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyResponseDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveySaveService {

    //repo
    private final SurveyRepository surveyRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;

    public void saveSurvey(SurveyRequest.SurveyDto request){
        Survey survey = SurveyRequest.SurveyDto.toEntity(request);
        List<Question> questions = getQuestionsFromDto(request.questions(),survey);
        List<QuestionOption> options = getQuestionOptionFromDto(request.questions(),questions,survey);

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
        for(QuestionDto dto : request){
            QuestionType type = QuestionType.fromType(dto.type());
            Question question = Question.builder()
                    .isNecessary(dto.required())
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
                            .text(optionText)
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

    public AnswerPageDto getSurveys(Long surveyId){
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
            List<String> optionList = q.isOption() ? optionMap.getOrDefault(q.getId(), new ArrayList<>()) : null;
            response.add(
                    new QuestionDto(
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

//    String type,
//    String question,
//    String description,
//    List<String> options,
//    boolean required

    public Survey getSurveyFromId(Long surveyId){
        try{
            return surveyRepository.findSurveyById(surveyId);
        }catch (Exception e){
            log.warn("getSurveyFormId Fail");
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
            response.add(
                    new SurveyResponseDto(
                        survey.getSurveyTitle(),
                            survey.getCreatedAt(),
                            survey.getParticipantCount(),
                            3
                    )
            );
            //fixme 남은 날짜 고쳐야함
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

    //todo 추후 개발
    public void saveOptions(){

    }

}
