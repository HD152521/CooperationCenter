package com.cooperation.project.cooperationcenter.domain.school.dto;

public class SchoolRequest {
    public record SchoolDto(
            String schoolKoreanName,
            String schoolEnglishName,
            String imgUrl
    ){}

    public record SchoolBoardDto(
            Long schoolId,
            String boardTitle,
            String realTitle,
            String boardDescription,
            String boardType
    ){}

    public record SchoolPostDto(
            Long boardId,
            String title,
            String description,
            String content,
            String status,
            String type
    ){}

    public record PostDto(
            Long boardId,
            String keyword,
            String status
    ){}
}
