package com.cooperation.project.cooperationcenter.domain.member.service;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.agency.repository.AgencyRepository;
import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.model.FileTargetType;
import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyLogRepository;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final MemberRepository memberRepository;
    private final AgencyRepository agencyRepository;
    private final SurveyFindService surveyFindService;
    private final FileService fileService;

    public Member getMember(String email){
        return memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Profile.ProfileDto getProfileDto(MemberDetails memberDetails, Pageable pageable){
        Member member = getMember(memberDetails.getUsername());
        Agency agency = member.getAgency();
        log.info("memberName:{}",memberDetails.getUsername());
        log.info("member File:{}",agency.getBusinessPicture().toString());

        Page<SurveyLog> logs = surveyFindService.getSurveyLogs(member,pageable);

        return new Profile.ProfileDto(
                Profile.MemberDto.from(member),
                Profile.AgencyDto.from(agency),
                Profile.SurveyDto.from(logs),
                Profile.MemberFileDto.from(agency.getBusinessPicture()),
                Profile.MemberFileDto.from(agency.getAgencyPicture()));
    }



    @Transactional
    public void updateMember(Profile.MemberDto request,MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        member.updateMember(request);
        memberRepository.save(member);
    }

    @Transactional
    public void updateAgency(Profile.AgencyDto request,MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        Agency agency = member.getAgency();

        agency.updateAgency(request);
        agencyRepository.save(agency);
    }

    @Transactional
    public void updateBussinessCert(MultipartFile file,MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        Agency agency = member.getAgency();

        // 옛 엔티티를 건드리지 않음 (프록시 초기화 금지)
        String uuid = UUID.randomUUID().toString();
        FileAttachment fresh = fileService.saveFile(new FileAttachmentDto(file, "member", null, uuid, null));

        FileAttachment old = agency.getBusinessPicture();
        String oldFileId = old.getFileId();
        FileTargetType oldType = old.getFiletype();

        agency.updateBusinessCertificate(fresh);
        agencyRepository.saveAndFlush(agency);  // FK 교체 먼저 확정

        fileService.deleteFileById(oldFileId,oldType);
    }

    @Transactional
    public void updateAgencyPicture(MultipartFile file,MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        Agency agency = member.getAgency();

        FileAttachment old = agency.getAgencyPicture();
        String oldFileId = old.getFileId();
        FileTargetType oldType = old.getFiletype();

        String uuid = UUID.randomUUID().toString();
        FileAttachment newMemberFile = fileService.saveFile(new FileAttachmentDto(file,"member",null,uuid,null));
        agency.updateAgencyPicture(newMemberFile);
        agencyRepository.saveAndFlush(agency);

        fileService.deleteFileById(oldFileId,oldType);
    }

    private void requireFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일이 비었습니다.");
        if (file.getSize() > 10 * 1024 * 1024L)
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "최대 10MB.");
    }
}
