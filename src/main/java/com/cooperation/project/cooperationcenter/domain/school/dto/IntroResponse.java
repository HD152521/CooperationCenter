package com.cooperation.project.cooperationcenter.domain.school.dto;

import java.util.List;

public class IntroResponse {
    public record IntroPostResponseDto(
            Long id,
            IntroInfo intro,
            BasicInfo basicInfo,
            UrlInfo homepageUrls,
            List<CollegeInfo> colleges,
            String homepageUrl,
            String englishPageUrl,
            String mapUrl,
            String mapLocationURl,
            int collegeRank,
            SchoolDto schoolDto
    ) {}

    // --- intro(title, description, advantages) ---
    public record IntroInfo(
            String title,
            String description,
            List<String> advantages,
            String schoolPicUrl
    ) {}

    // --- 기본 정보 ---
    public record BasicInfo(
            String schoolName,
            String builtAt,
            String location,
            List<String> feature
    ) {}

    // --- 홈페이지 관련 urlNames, urls ---
    public record UrlInfo(
            List<String> urlNames,
            List<String> urls
    ) {}

    // --- 대학 정보 ---
    public record CollegeInfo(
            long id,
            String collegeName,
            String type,
            List<String> departments
    ) {}

    public record SchoolDto(
            String koreanName,
            String englishName
    ){}
}


