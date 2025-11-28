package com.cooperation.project.cooperationcenter.domain.school.dto;

import java.util.List;

public class IntroRequest {

    public record TotalIntroSaveDto(
            IntroSaveDto intro,
            BasicInfoSaveDto basicInfo,
            int collegeRank,
            HomepageUrlSaveDto urlsDto,
            CollegeSaveDto collegeDto,
            String homepageUrl,
            String englishPageUrl
    ){}


    public record IntroSaveDto(
            String title,
            String description,
            List<String> advantages
    ){}
    
    public record BasicInfoSaveDto(
            String schoolName,
            String builtAt,
            String location,
            String feature
    ){}

    public record HomepageUrlSaveDto(
            List<String> urlNames,
            List<String> url
    ){}


    public record CollegeSaveDto(
            String name,
            String description,
            String type,
            String departments //과 사이를 _로 구분해서 받기
    ) {}
}
