package com.cooperation.project.cooperationcenter.domain.school.dto;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.school.model.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SchoolResponse {
    public record SchoolDto(
            String koreanName,
            String englishName,
            String imgUrl,
            Long schoolId,
            Long firstBoardId
    ){
        public static SchoolDto from(School school){
            Long firstId = (school.getBoards().isEmpty())? 0 : school.getBoards().get(0).getId();
            return new SchoolDto(school.getSchoolKoreanName(), school.getSchoolEnglishName(), school.getLogoUrl(), school.getId(),firstId);
        }
    }

    public record SchoolHomeDto(
            String koreanName,
            String englishName,
            String imgUrl,
            Long schoolId,
            List<SchoolCategoryDto> categories
    ){
        public static SchoolHomeDto from(School school){
            return new SchoolHomeDto(
                    school.getSchoolKoreanName(),
                    school.getSchoolEnglishName(),
                    school.getLogoUrl(),
                    school.getId(),
                    SchoolCategoryDto.from(school.getBoards())
            );
        }
    }

    public record SchoolCategoryDto(
            String categoryName,
            Long boardId
    ){
        public static SchoolCategoryDto from(SchoolBoard boards){
            return new SchoolCategoryDto(
                    boards.getBoardTitle(),
                    boards.getId()
            );
        }

        public static List<SchoolCategoryDto> from(List<SchoolBoard> boards){
            return boards.stream().map(SchoolCategoryDto::from).toList();
        }
    }

    public record SchoolBoardDto(
            Long id,
            String boardTitle,
            String realTitle,
            String boardDescription,
            String boardType,
            boolean isActive
    ){
        public static SchoolBoardDto from(SchoolBoard board,Long nowId){
            return new SchoolBoardDto(
                    board.getId(),
                    board.getBoardTitle(),
                    board.getRealTitle(),
                    board.getBoardDescription(),
                    board.getType().getType(),
                    board.getId().equals(nowId)
            );
        }
        public static List<SchoolBoardDto> from(List<SchoolBoard> boards,Long nowId){
            if(boards.isEmpty()) return Collections.emptyList();
            return boards.stream().map(b -> SchoolBoardDto.from(b, nowId)).toList();
        }
    }

    public record SchoolPostDto(
            Long id,
            String title,
            String description,
            String content,
            String status,
            String type,
            LocalDateTime createdAt,
            Long boardId,
            int views,
            boolean isFile,
            String postType,
            String fileId
    ){
            public static SchoolPostDto from(SchoolPost post){
                return new SchoolPostDto(
                        post.getId(),
                        post.getPostTitle(),
                        post.getPostTitle(),
                        post.getContent(),
                        post.getStatus().getStatus(),
                        post.getType().getType(),
                        post.getCreatedAt(),
                        post.getSchoolBoard().getId(),
                        post.getViews(),
                        !post.getFiles().isEmpty(),
                        "NOTICE",
                        null
                );
            }
            public static List<SchoolPostDto> from(List<SchoolPost> posts){
                return posts.stream()
                        .map(SchoolPostDto::from)
                        .collect(Collectors.toList());
            }

            public static Page<SchoolPostDto> fromPostPage(Page<SchoolPost> posts){
                return posts.map(SchoolPostDto::from);
            }

            public static SchoolPostDto from(FilePost post){
                return new SchoolPostDto(
                        post.getId(),
                        post.getPostTitle(),
                        post.getPostTitle(),
                        null,
                        post.getStatus().getStatus(),
                        post.getType().getType(),
                        post.getCreatedAt(),
                        post.getSchoolBoard().getId(),
                        post.getDownloads(),
                        true,
                        "FILES",
                        post.getFile().getFileId()
                );
            }
            public static Page<SchoolPostDto> fromFilePostPage(Page<FilePost> posts){
                return posts.map(SchoolPostDto::from);
            }
    }

    public record ScheduleDto(
            String title,
            LocalDate startDate,
            LocalDate endDate,
            String type,
            Long id
    ){
        public static ScheduleDto from(SchoolSchedule schedule){
            return new ScheduleDto(
                    schedule.getTitle(),
                    schedule.getStartDate(),
                    schedule.getEndDate(),
                    schedule.getType().getType().toLowerCase(),
                    schedule.getId()
            );
        }

        public static List<ScheduleDto> from(List<SchoolSchedule> schedules){
            return schedules.stream()
                    .map(ScheduleDto::from)
                    .toList();
        }

        public static Page<ScheduleDto> from(Page<SchoolSchedule> schedules){
            return schedules.map(ScheduleDto::from);
        }
    }

    public record SchoolPostSimpleDto(
            String title,
            Long id
    ){
        public static SchoolPostSimpleDto from(SchoolPost post){
            return new SchoolPostSimpleDto(
                    post.getPostTitle(),
                    post.getId()
            );
        }

        public static SchoolPostSimpleDto from(FilePost post){
            return new SchoolPostSimpleDto(
                    post.getPostTitle(),
                    post.getId()
            );
        }
    }

    public record SchoolPageDto(
            SchoolDto schoolDto,
            List<SchoolBoardDto> boardDtos
    ){
        public static SchoolPageDto from(School school){
            return new SchoolPageDto(
                    SchoolDto.from(school),
                    SchoolBoardDto.from(school.getBoards(),null)
            );
        }
    }

    public record PostFileDto(
            String name,
            String url,
            String fileId
    ){
        public static PostFileDto from(FileAttachment file){
            String url = "/api/v1/file/"+file.getFiletype().getFileType()+"/"+file.getFileId();
            return new PostFileDto(
                    file.getOriginalName(),
                    url,
                    file.getFileId()
            );
        }
        public static List<PostFileDto> from(List<FileAttachment> file){
            return file.stream().map(PostFileDto::from).collect(Collectors.toList());
        }
    }

    public record PostDetailDto(
            SchoolPostDto post,
            List<PostFileDto> file,
            SchoolPostSimpleDto beforePost,
            SchoolPostSimpleDto afterPost
    ){
    }

    public record FilePostDetailDto(
            SchoolPostDto post,
            PostFileDto file,
            SchoolPostSimpleDto beforePost,
            SchoolPostSimpleDto afterPost
    ){
    }

    public record IntroDto(
            String title,
            String content,
            Long boardId,
            Long introId
    ){
        public static SchoolResponse.IntroDto from(IntroPost introPost){
            return new SchoolResponse.IntroDto(
                    introPost.getTitle(),
                    introPost.getContent(),
                    introPost.getSchoolBoard().getId(),
                    introPost.getId()
            );
        }
    }
}
