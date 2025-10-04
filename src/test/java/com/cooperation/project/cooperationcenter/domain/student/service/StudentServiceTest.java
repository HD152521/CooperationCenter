package com.cooperation.project.cooperationcenter.domain.student.service;

import com.cooperation.project.cooperationcenter.domain.member.dto.AgencyRegion;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.model.UserStatus;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.cooperation.project.cooperationcenter.domain.student.repository.StudentRepository;
import com.cooperation.project.cooperationcenter.domain.student.repository.StudentRepositoryCustom;
import com.cooperation.project.cooperationcenter.domain.survey.model.Answer;
import com.cooperation.project.cooperationcenter.domain.survey.model.Question;
import com.cooperation.project.cooperationcenter.domain.survey.model.QuestionType;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentRepositoryCustom studentRepositoryCustom;

    @InjectMocks
    private StudentService studentService;

    @Test
    void addStudentBySurvey_skipsMissingOptionalTemplateAnswer() {
        Member member = Member.builder()
                .memberName("member")
                .email("member@example.com")
                .password("password")
                .homePhoneNumber("010-0000-0000")
                .phoneNumber("010-0000-0000")
                .address1("address1")
                .address2("address2")
                .birth(LocalDate.now())
                .agencyOwner("owner")
                .agencyName("agency")
                .agencyAddress1("agencyAddress1")
                .agencyAddress2("agencyAddress2")
                .agencyPhone("010-1111-2222")
                .agencyRegion(AgencyRegion.BEIJING)
                .agencyEmail("agency@example.com")
                .memberId("member-id")
                .role(Member.Role.USER)
                .approvalSignup(true)
                .status(UserStatus.APPROVED)
                .build();

        SurveyLog surveyLog = SurveyLog.builder()
                .member(member)
                .survey(null)
                .startTime(LocalDateTime.now())
                .build();

        Question templateQuestion = Question.builder()
                .questionType(QuestionType.SHORT)
                .questionDescription("중문 이름")
                .isNecessary(true)
                .survey(null)
                .question("중문 이름")
                .questionOrder(1)
                .domainField("chineseName")
                .template(true)
                .build();
        templateQuestion.setQuestionId("q1");

        Question optionalTemplateQuestion = Question.builder()
                .questionType(QuestionType.SHORT)
                .questionDescription("선택 문항")
                .isNecessary(false)
                .survey(null)
                .question("선택 문항")
                .questionOrder(2)
                .domainField("optionalField")
                .template(true)
                .build();
        optionalTemplateQuestion.setQuestionId("q2");

        Answer answer = Answer.builder()
                .questionId(1)
                .questionRealId("q1")
                .textAnswer("홍길동")
                .answerType(QuestionType.SHORT)
                .surveyLog(surveyLog)
                .build();

        List<Question> questions = List.of(templateQuestion, optionalTemplateQuestion);
        List<Answer> answers = List.of(answer);

        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        studentService.addStudentBySurvey(questions, answers, member);

        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(captor.capture());
        Student savedStudent = captor.getValue();

        assertThat(savedStudent.getChineseName()).isEqualTo("홍길동");
        assertThat(savedStudent.getEnglishName()).isNull();
        assertThat(savedStudent.getGender()).isNull();
    }
}
