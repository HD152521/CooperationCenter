package com.cooperation.project.cooperationcenter.domain.school.dto;

import com.cooperation.project.cooperationcenter.domain.school.model.School;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;

import java.util.List;
import java.util.stream.Collectors;

public class SchoolResponse {
    public record SchoolDto(
            String koreanName,
            String englishName,
            String imgUrl
    ){
        public static SchoolDto from(School school){
            return new SchoolDto(school.getSchoolKoreanName(), school.getSchoolEnglishName(), school.getLogoUrl());
        }
    }

    public record SchoolBoardDto(
            Long id,
            String boardTitle,
            String realTitle,
            String boardDescription,
            String boardType
    ){
        public static SchoolBoardDto from(SchoolBoard board){
            return new SchoolBoardDto(
                    board.getId(),
                    board.getBoardTitle(),
                    board.getRealTitle(),
                    board.getBoardDescription(),
                    board.getType().getType()
            );
        }
        public static List<SchoolBoardDto> from(List<SchoolBoard> boards){
            return boards.stream().map(SchoolBoardDto::from).collect(Collectors.toList());
        }
    }

    public record SchoolPostDto(
            Long id,
            String title,
            String description,
            String content,
            String status,
            String type

    ){
        public static SchoolPostDto from(SchoolPost post){
            return new SchoolPostDto(
                    post.getId(),
                    post.getPostTitle(),
                    post.getPostTitle(),
                    post.getContent(),
                    post.getStatus().getStatus(),
                    post.getType().getType()
            );
        }
        public static List<SchoolPostDto> from(List<SchoolPost> posts){
            return posts.stream()
                    .map(SchoolPostDto::from)
                    .collect(Collectors.toList());
        }
    }
}
