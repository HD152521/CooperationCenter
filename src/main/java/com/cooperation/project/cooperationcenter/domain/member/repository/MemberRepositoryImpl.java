package com.cooperation.project.cooperationcenter.domain.member.repository;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.model.QMember;
import com.cooperation.project.cooperationcenter.domain.member.model.UserStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> searchMembers(String keyword, String status, Pageable pageable) {
        QMember member = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        // 상태 조건
        if (status != null && !status.equalsIgnoreCase("ALL")) {
            builder.and(member.status.eq(UserStatus.valueOf(status)));
        }

        // 키워드 조건 (email or name 포함)
        if (keyword != null && !keyword.isBlank()) {
            builder.and(
                    member.email.containsIgnoreCase(keyword)
                            .or(member.memberName.containsIgnoreCase(keyword))
            );
        }

        // 실제 데이터 조회
        List<Member> content = queryFactory
                .selectFrom(member)
                .where(builder)
                .orderBy(member.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        Long total = queryFactory
                .select(member.count())
                .from(member)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}