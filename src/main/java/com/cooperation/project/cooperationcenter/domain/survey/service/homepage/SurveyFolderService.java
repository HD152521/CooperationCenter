package com.cooperation.project.cooperationcenter.domain.survey.service.homepage;

import com.cooperation.project.cooperationcenter.domain.member.dto.MemberDetails;
import com.cooperation.project.cooperationcenter.domain.member.model.Member;
import com.cooperation.project.cooperationcenter.domain.member.repository.MemberRepository;
import com.cooperation.project.cooperationcenter.domain.survey.dto.SurveyFolderDto;
import com.cooperation.project.cooperationcenter.domain.survey.model.SurveyFolder;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyFolderRepository;
import com.cooperation.project.cooperationcenter.domain.survey.repository.SurveyRepository;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyFolderService {

    private final SurveyFolderRepository surveyFolderRepository;
    private final MemberRepository memberRepository;
    private final SurveyRepository surveyRepository;

    public List<SurveyFolder>   loadSurveyFolders() {
        try{
            return surveyFolderRepository.findAll();
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public SurveyFolder loadSurveyFolderById(String folderId) {
        try{
            return surveyFolderRepository.findByFolderId(folderId).orElseThrow(
                    () -> new BaseException(ErrorCode.BAD_REQUEST)
            );
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SurveyFolderDto> getSurveyFolderDtos() {
        try{
            return SurveyFolderDto.from(loadSurveyFolders());
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public void deleteSurveyFolder(final SurveyFolder surveyFolder) {
        surveyFolderRepository.delete(surveyFolder);
    }

    @Transactional
    public void deleteSurveyFolder(final String folderId) {
        deleteSurveyFolder(loadSurveyFolderById(folderId));

    }

    @Transactional
    public void saveSurveyFolderDto(final SurveyFolderDto surveyFolderDto, MemberDetails memberDetails) {
        Member member = getMember(memberDetails);
        saveSurveyFolder(SurveyFolder.from(surveyFolderDto,member));
    }

    @Transactional
    public void updateSurveyFolderDto(final SurveyFolderDto surveyFolderDto){
        SurveyFolder surveyFolder = loadSurveyFolderById(surveyFolderDto.folderId());
        surveyFolder.updateFromDto(surveyFolderDto);
    }

    @Transactional
    public void saveSurveyFolder(final SurveyFolder surveyFolder) {
        surveyRepository.deleteAll(surveyFolder.getSurveys());
        surveyFolderRepository.save(surveyFolder);
    }

    public Member getMember(MemberDetails memberDetails){
        try{
            return memberRepository.findMemberByEmail(memberDetails.getUsername()).orElseThrow(
                    ()->new BaseException(ErrorCode.BAD_REQUEST)
            );
        }catch(Exception e){
            log.warn(e.getMessage());
        }
            return null;
        }
}
