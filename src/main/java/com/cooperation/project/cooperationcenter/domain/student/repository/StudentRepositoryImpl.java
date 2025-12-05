package com.cooperation.project.cooperationcenter.domain.student.repository;

import com.cooperation.project.cooperationcenter.domain.student.dto.Gender;
import com.cooperation.project.cooperationcenter.domain.student.dto.StudentRequest;
import com.cooperation.project.cooperationcenter.domain.student.model.QStudent;
import com.cooperation.project.cooperationcenter.domain.student.model.Student;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StudentRepositoryImpl implements StudentRepositoryCustom {

    private final JPAQueryFactory qf;

    @Override
    public Page<Student> loadStudentPageByCondition(StudentRequest.ConditionDto c, Pageable pageable) {
        QStudent st = QStudent.student;

        // where 절 재사용을 위해 한 번 구성
        BooleanExpression[] predicates = new BooleanExpression[] {
                nameContains(c.name(), st),
                genderEq(c.gender(), st),
                birthGoe(c.birthStart(), st),
                birthLoe(c.birthEnd(), st),
                emailContains(c.email(), st),
                passportContains(c.passport(), st),
                examContains(c.exam(), st),
                surveyIdEq(c.surveyLogId(), st),
                memberNameContains(c.agencyName(), st)
        };

        // content
        List<Student> content = qf.selectFrom(st)
                .where(predicates)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // total
        Long total = qf.select(st.count())
                .from(st)
                .where(predicates)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public List<Student> loadStudentDtoByCondition(StudentRequest.ConditionDto c) {
        QStudent st = QStudent.student;

        // where 절 재사용을 위해 한 번 구성
        BooleanExpression[] predicates = new BooleanExpression[] {
                nameContains(c.name(), st),
                genderEq(c.gender(), st),
                birthGoe(c.birthStart(), st),
                birthLoe(c.birthEnd(), st),
                emailContains(c.email(), st),
                passportContains(c.passport(), st),
                examContains(c.exam(), st),
                surveyIdEq(c.surveyLogId(), st),
                memberNameContains(c.agencyName(), st)
        };

        return qf.selectFrom(st)
                .where(predicates)
                .fetch();
    }

    // ===== predicates =====

    private BooleanExpression surveyIdEq(String surveyId, QStudent st) {
        if (!hasText(surveyId)) return null;
        return st.surveyLog.survey.surveyId.eq(surveyId);
    }

    private BooleanExpression memberNameContains(String agencyName, QStudent st) {
        log.info("agencyName:{}",agencyName);
        if (!hasText(agencyName)) return null;
        return st.agency.agencyName.containsIgnoreCase(agencyName);
    }

    private BooleanExpression nameContains(String name, QStudent st) {
        if (!hasText(name)) return null;
        return st.chineseName.containsIgnoreCase(name)
                .or(st.englishName.containsIgnoreCase(name));
    }
    private BooleanExpression genderEq(Gender gender, QStudent st) {
        return gender == null ? null : st.gender.eq(gender);
    }
    private BooleanExpression birthGoe(LocalDate start, QStudent st) {
        return start == null ? null : st.birthDate.goe(start);
    }
    private BooleanExpression birthLoe(LocalDate end, QStudent st) {
        return end == null ? null : st.birthDate.loe(end);
    }
    private BooleanExpression emailContains(String email, QStudent st) {
        return hasText(email) ? st.studentEmail.containsIgnoreCase(email) : null;
    }
    private BooleanExpression passportContains(String passport, QStudent st) {
        return hasText(passport) ? st.passportNumber.containsIgnoreCase(passport) : null;
    }
    private BooleanExpression examContains(String exam, QStudent st) {
        return hasText(exam) ? st.examNumber.containsIgnoreCase(exam) : null;
    }

    private boolean hasText(String s) { return s != null && !s.isBlank(); }

    // ===== pageable sort → QueryDSL order spec =====

//    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, QStudent st) {
//        if (sort == null || sort.isUnsorted()) {
//            return new OrderSpecifier<?>[]{ st.id.desc() }; // 기본 정렬
//        }
//        PathBuilder<Student> path = new PathBuilder<>(Student.class, st.getMetadata().getName()); // "student"
//        List<OrderSpecifier<?>> orders = new ArrayList<>();
//        for (Sort.Order o : sort) {
//            orders.add(new OrderSpecifier<>(
//                    o.isAscending() ? Order.ASC : Order.DESC,
//                    path.get(o.getProperty())
//            ));
//        }
//        return orders.toArray(new OrderSpecifier<?>[0]);
//    }
}
