package com.cooperation.project.cooperationcenter.domain.school.dto;

import com.cooperation.project.cooperationcenter.domain.school.model.IntroPost;

import java.util.List;

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
            String type,
            Long postId,
            List<String> deleteFileIds
    ){}

    public record PostDto(
            Long boardId,
            String keyword,
            String status
    ){}

    public record BoardIdDto(
            Long boardId
    ){}

    public record PostIdDto(
            Long postId
    ){}

    public record IntroDto(
            Long introId,
            String title,
            String content,
            Long boardId
    ){}
}
