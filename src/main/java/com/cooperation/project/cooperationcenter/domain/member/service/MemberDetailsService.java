package com.cooperation.project.cooperationcenter.domain.member.service;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberAuthContext;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.exception.MemberHandler;
import com.cooperation.project.cooperationcenter.domain.member.exception.status.MemberErrorStatus;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null || username.isBlank()) {
            log.warn("[AUTH] username is null or blank");
            throw new UsernameNotFoundException("Invalid username");
        }

        Member member = memberRepository.findMemberByEmail(username)
                .orElseThrow(() -> {
                    log.info("[AUTH] member not found: {}", username);
                    return new UsernameNotFoundException("Member not found");
                });

        if (!member.isAccept()) {
            log.info("[AUTH] member not accepted: {}", username);
            throw new BaseException(MemberErrorStatus.MEMBER_NOT_ACCEPTED);
        }

        MemberAuthContext ctx = MemberAuthContext.of(member);
        return new MemberDetails(ctx);
    }
}