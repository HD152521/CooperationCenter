package com.cooperation.project.cooperationcenter.domain.member.repository;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>,MemberRepositoryCustom  {
    Optional<Member> findMemberByEmail(String email);
    Boolean existsMemberByEmail(String email);
    List<Member> findTop4ByApprovalSignupFalseOrderByCreatedAtDesc();
    Page<Member> findByStatus(UserStatus status, Pageable pageable);

    long count();
    long countByStatus(UserStatus status);
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
