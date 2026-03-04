package com.cooperation.project.cooperationcenter.domain.member.service;

import com.cooperation.project.cooperationcenter.domain.agency.model.Agency;
import com.cooperation.project.cooperationcenter.domain.agency.repository.AgencyRepository;
import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.exception.FileHandler;
import com.cooperation.project.cooperationcenter.domain.file.exception.status.FileErrorStatus;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.model.FileTargetType;
import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.dto.Profile;
import com.cooperation.project.cooperationcenter.domain.member.exception.MemberHandler;
import com.cooperation.project.cooperationcenter.domain.member.exception.status.MemberErrorStatus;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyLog;
import com.cooperation.project.cooperationcenter.domain.survey.service.homepage.SurveyFindService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus.MEMBER_NOT_FOUND));
    }

    public Profile.ProfileDto getProfileDto(MemberDetails memberDetails, Pageable pageable){

        Member member = getMember(memberDetails.getUsername());
        Agency agency = member.getAgency();

        if (agency == null) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_AGENCY_NOT_FOUND);
        }

        FileAttachment business = agency.getBusinessPicture();
        log.info("memberName:{}",memberDetails.getUsername());
        log.info("business file exists: {}", business != null);

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
        if (!member.isAccept()) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_NOT_ACCEPTED);
        }
        member.updateMember(request);
        memberRepository.save(member);
    }

    @Transactional
    public void updateAgency(Profile.AgencyDto request,MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        if (!member.isAccept()) {
            throw new MemberHandler(MemberErrorStatus.MEMBER_NOT_ACCEPTED);
        }
        Agency agency = member.getAgency();
        agency.updateAgency(request);
        agencyRepository.save(agency);
    }

    @Transactional
    public void updateBusinessCert(MultipartFile file, MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        Agency agency = member.getAgency();

        requireFile(file);

        FileAttachment old = agency.getBusinessPicture();
        String uuid = UUID.randomUUID().toString();
        FileAttachment fresh = fileService.saveFile(new FileAttachmentDto(file, "member", null, uuid, null));

        agency.updateBusinessCertificate(fresh);
        agencyRepository.saveAndFlush(agency);  // FK 교체 먼저 확정

        if (old != null) {
            try {
                fileService.deleteFileById(old.getFileId(), old.getFiletype());
            } catch (Exception e) {
                log.error("old business cert delete failed", e);
            }
        }
    }

    @Transactional
    public void updateAgencyPicture(MultipartFile file,MemberDetails memberDetails){
        Member member = getMember(memberDetails.getUsername());
        Agency agency = member.getAgency();

        requireFile(file);

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
            throw new FileHandler(FileErrorStatus.FILE_EMPTY);
        if (file.getSize() > 10 * 1024 * 1024L)
            throw new FileHandler(FileErrorStatus.FILE_SIZE_ERROR);
    }
}
