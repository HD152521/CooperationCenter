package com.cooperation.project.cooperationcenter.domain.member.service;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final MemberRepository memberRepository;

    public Member getMember(String email){
        try {
            return memberRepository.findMemberByEmail(email)
                    .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        } catch (BaseException e){
            log.warn("멤버 조회 실패: {}", e.getMessage());
            return null;
        } catch (Exception e){
            log.error("알 수 없는 에러 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    public Profile.ProfileDto getProfileDto(MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        log.info("memberName:{}",memberDetails.getUsername());
        List<SurveyLog> logs = member.getSurveyLogs();
        return new Profile.ProfileDto(
                Profile.MemberDto.from(member),
                Profile.SurveyDto.from(logs)
        );
    }

}
