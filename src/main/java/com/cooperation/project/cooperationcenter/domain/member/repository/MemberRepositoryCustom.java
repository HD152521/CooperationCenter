package com.cooperation.project.cooperationcenter.domain.member.repository;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface MemberRepositoryCustom {
    Page<Member> searchMembers(String keyword, String status,String date, Pageable pageable);
}
