package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.QSchoolPost;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SchoolPostRepositoryImpl implements SchoolPostQSDLRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public SchoolPost findBeforePost(SchoolPost currentPost) {
        String currentTypeName = currentPost.getType().name(); // ✅ Enum → "NOTICE"/"NORMAL"
        NumberTemplate<Integer> currentOrder = Expressions.numberTemplate(
                Integer.class,
                "CASE WHEN {0} = 'NOTICE' THEN 0 ELSE 2 END",
                currentTypeName
        );

        return queryFactory.selectFrom(QSchoolPost.schoolPost)
                .where(QSchoolPost.schoolPost.schoolBoard.eq(currentPost.getSchoolBoard()))
                .where(
                        Expressions.numberTemplate(Integer.class,
                                        "CASE WHEN {0} = 'NOTICE' THEN 0 ELSE 2 END",
                                        QSchoolPost.schoolPost.type)
                                .gt(currentOrder)
                                .or(
                                        QSchoolPost.schoolPost.type.eq(currentPost.getType())
                                                .and(QSchoolPost.schoolPost.createdAt.after(currentPost.getCreatedAt()))
                                )
                )
                .orderBy(
                        Expressions.numberTemplate(Integer.class,
                                "CASE WHEN {0} = 'NOTICE' THEN 0 ELSE 2 END",
                                QSchoolPost.schoolPost.type).asc(),
                        QSchoolPost.schoolPost.createdAt.asc()
                )
                .fetchFirst();
    }

    @Override
    public SchoolPost findAfterPost(SchoolPost currentPost) {
        String currentTypeName = currentPost.getType().name();

        NumberTemplate<Integer> currentOrder = Expressions.numberTemplate(
                Integer.class,
                "CASE WHEN {0} = 'NOTICE' THEN 0 ELSE 2 END",
                currentTypeName
        );

        return queryFactory.selectFrom(QSchoolPost.schoolPost)
                .where(QSchoolPost.schoolPost.schoolBoard.eq(currentPost.getSchoolBoard()))
                .where(
                        Expressions.numberTemplate(Integer.class,
                                        "CASE WHEN {0} = 'NOTICE' THEN 0 ELSE 2 END",
                                        QSchoolPost.schoolPost.type)
                                .lt(currentOrder)
                                .or(
                                        QSchoolPost.schoolPost.type.eq(currentPost.getType())
                                                .and(QSchoolPost.schoolPost.createdAt.before(currentPost.getCreatedAt()))
                                )
                )
                .orderBy(
                        Expressions.numberTemplate(Integer.class,
                                "CASE WHEN {0} = 'NOTICE' THEN 0 ELSE 2 END",
                                QSchoolPost.schoolPost.type).desc(),
                        QSchoolPost.schoolPost.createdAt.desc()
                )
                .fetchFirst();
    }

}
