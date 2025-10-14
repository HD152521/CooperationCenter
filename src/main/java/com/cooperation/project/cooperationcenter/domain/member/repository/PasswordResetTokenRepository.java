package com.cooperation.project.cooperationcenter.domain.member.repository;

import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findPasswordResetTokenByToken(String token);
    PasswordResetToken findPasswordResetTokenByMember(Member member);
}
