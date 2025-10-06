package com.cooperation.project.cooperationcenter.domain.member.repository;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.model.QMember;
import com.cooperation.project.cooperationcenter.domain.member.model.UserStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Member> searchMembers(String keyword, String status, String date,Pageable pageable) {
        QMember member = QMember.member;
        BooleanBuilder builder = new BooleanBuilder();

        // 상태 조건
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            try {
                builder.and(member.status.eq(UserStatus.valueOf(status.trim())));
            } catch (IllegalArgumentException e) {
                log.warn("존재하지 않는 사용자 상태: {}", status);
            }
        }

        // 키워드 조건 (email or name 포함)
        if (keyword != null&& !keyword.trim().isEmpty()  && !keyword.isBlank()) {
            builder.and(
                    member.email.containsIgnoreCase(keyword)
                            .or(member.memberName.containsIgnoreCase(keyword))
            );
        }

        if (date != null && !date.trim().isEmpty()) {
            try {
                int days = Integer.parseInt(date.trim());
                LocalDateTime afterDate = LocalDateTime.now().minusDays(days);
                builder.and(member.createdAt.goe(afterDate));
            } catch (NumberFormatException e) {
                log.warn("잘못된 날짜 필터 값: {}", date);
            }
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