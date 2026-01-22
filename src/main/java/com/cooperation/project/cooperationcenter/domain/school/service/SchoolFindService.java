package com.cooperation.project.cooperationcenter.domain.school.service;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.repository.FileAttachmentRepository;
import com.cooperation.project.cooperationcenter.domain.school.dto.*;
import com.cooperation.project.cooperationcenter.domain.school.exception.SchoolHandler;
import com.cooperation.project.cooperationcenter.domain.school.exception.status.SchoolErrorStatus;
import com.cooperation.project.cooperationcenter.domain.school.model.*;
import com.cooperation.project.cooperationcenter.domain.school.repository.*;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolFindService {

    private final SchoolRepository schoolRepository;
    private final SchoolBoardRepository schoolBoardRepository;
    private final SchoolPostRepository schoolPostRepository;
    private final IntroPostRepository introPostRepository;
    private final FilePostRepository filePostRepository;
    private final SchoolScheduleRepository schoolScheduleRepository;
    private final SchoolPostQSDLRepository schoolPostQSDLRepository;
    private final CollegeRepository collegeRepository;

    private final FileAttachmentRepository fileAttachmentRepository;

    @Transactional(readOnly = true)
    public List<SchoolResponse.SchoolDto> loadAllSchoolByDto(){
        return loadAllSchool().stream()
                .map(SchoolResponse.SchoolDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SchoolResponse.SchoolHomeDto> loadAllSchoolByHomeDto(){
        return loadAllSchool().stream()
                .map(SchoolResponse.SchoolHomeDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<School> loadAllSchool(){
        return schoolRepository.findAll();
    }

    public SchoolResponse.SchoolDto loadSchoolByEnglishNameByDto(String englishName){
        return SchoolResponse.SchoolDto.from(schoolRepository.findSchoolBySchoolEnglishName(englishName).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.SCHOOL_NOT_FOUND)
        ));
    }

    public School loadSchoolByEnglishName(String englishName){
        return schoolRepository.findSchoolBySchoolEnglishName(englishName)
                .orElseThrow(() -> new BaseException(SchoolErrorStatus.SCHOOL_NOT_FOUND));
    }

    public School loadSchoolById(Long id){
        return schoolRepository.findSchoolById(id).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.SCHOOL_NOT_FOUND));
    }

    public List<SchoolBoard> loadBoardBySchool(School school){
        return schoolBoardRepository.findBySchool(school);
    }

    public List<SchoolResponse.SchoolBoardDto> loadBoardBySchoolByDto(School school,Long nowId){
        return schoolBoardRepository.findBySchool(school).stream()
                .map(dto -> SchoolResponse.SchoolBoardDto.from(dto,nowId))
                .toList();
    }

    public SchoolBoard loadBoardById(Long boardId){
        return schoolBoardRepository.findSchoolBoardById(boardId).orElseThrow(
                ()-> new SchoolHandler(SchoolErrorStatus.SCHOOL_BOARD_NOT_FOUND));
    }

    public SchoolPost loadPostById(Long postId){
        return schoolPostRepository.findById(postId).orElseThrow(
                ()-> new SchoolHandler(SchoolErrorStatus.SCHOOL_POST_NOT_FOUND));
    }

    public SchoolResponse.SchoolPostDto loadPostByIdByDto(Long postId){
        return SchoolResponse.SchoolPostDto.from(loadPostById(postId));
    }

    public List<SchoolPost> loadPostByBoard(SchoolBoard board){
        return schoolPostRepository.findBySchoolBoard(board);
    }

    public Page<SchoolPost> loadPostByPage(SchoolBoard board, Pageable pageable){
        return schoolPostRepository.findBySchoolBoard(board,pageable);
    }

    public Page<FilePost> loadFilePostByPage(SchoolBoard board, Pageable pageable){
        return filePostRepository.findFilePostBySchoolBoardAndStatus(board,pageable, PostStatus.PUBLISHED);
    }

    public Page<FilePost> loadFilePostByPageByKeyword(SchoolBoard board, Pageable pageable,String keyword){
        return filePostRepository.findFilePostBySchoolBoardAndStatusAndPostTitleContainingIgnoreCase(board, PostStatus.PUBLISHED,keyword,pageable);
    }

    public List<FilePost> loadNoticeFilePostByPageByKeyword(SchoolBoard board,String keyword){
        return filePostRepository.findFilePostBySchoolBoardAndStatusAndPostTitleContainingIgnoreCaseAndType(board, PostStatus.PUBLISHED,keyword, PostType.NOTICE);
    }

    public List<FilePost> loadNormalFilePostByPageByKeyword(SchoolBoard board,String keyword){
        return filePostRepository.findFilePostBySchoolBoardAndStatusAndPostTitleContainingIgnoreCaseAndType(board, PostStatus.PUBLISHED,keyword,PostType.NORMAL);
    }

    public List<SchoolResponse.SchoolPostDto> loadPostByBoardByDto(SchoolBoard board){
        return schoolPostRepository.findBySchoolBoard(board).stream()
                .map(SchoolResponse.SchoolPostDto::from)
                .collect(Collectors.toList());
    }

    public List<SchoolPost> loadNoticePostByBoardAndKeyword(SchoolBoard board, Pageable pageable, String keyword){
        PostType type = PostType.NOTICE;
        PostStatus status = PostStatus.PUBLISHED;
        return schoolPostRepository.searchPosts(board,type,status,keyword);
    }

    public List<SchoolPost> loadNormalPostByBoardAndKeyword(SchoolBoard board, Pageable pageable, String keyword){
        PostType type = PostType.NORMAL;
        PostStatus status = PostStatus.PUBLISHED;
        return schoolPostRepository.searchPosts(board,type,status,keyword);
    }

    public Page<SchoolResponse.SchoolPostDto> loadPostByBoardByDto(SchoolBoard board,Pageable pageable,String keyword){
        List<SchoolResponse.SchoolPostDto> noticePage = SchoolResponse.SchoolPostDto.from(loadNoticePostByBoardAndKeyword(board,pageable,keyword));
        List<SchoolResponse.SchoolPostDto> normalPage = SchoolResponse.SchoolPostDto.from(loadNormalPostByBoardAndKeyword(board,pageable,keyword));

        List<SchoolResponse.SchoolPostDto> merged = new ArrayList<>();
        merged.addAll(noticePage);
        merged.addAll(normalPage);

        int total = merged.size();
        return new PageImpl<>(merged, pageable, total);
    }

    public Page<SchoolResponse.SchoolPostDto> loadPostPageByBoardByDto(SchoolBoard board,Pageable pageable){
        return loadPostByPage(board,pageable)
                .map(SchoolResponse.SchoolPostDto::from);
    }

    public Page<SchoolResponse.SchoolPostDto> getFilePostPageByBoardByDto(SchoolBoard board,Pageable pageable){
        return loadFilePostByPage(board,pageable)
                .map(SchoolResponse.SchoolPostDto::from);
    }

    public Page<SchoolResponse.SchoolPostDto> getFilePostPageByBoardByDto(SchoolBoard board,Pageable pageable,String keyword){
        List<SchoolResponse.SchoolPostDto> noticeFilePost = SchoolResponse.SchoolPostDto.fromFilePost(loadNoticeFilePostByPageByKeyword(board,keyword));
        List<SchoolResponse.SchoolPostDto> normalFilePost = SchoolResponse.SchoolPostDto.fromFilePost(loadNormalFilePostByPageByKeyword(board,keyword));
        List<SchoolResponse.SchoolPostDto> merged = new ArrayList<>();

        merged.addAll(noticeFilePost);
        merged.addAll(normalFilePost);

        int total = merged.size();
        log.info("find file post size : {}",total);
        return new PageImpl<>(merged,pageable,total);
    }


    public SchoolPost getBeforePostById(Long postId, SchoolBoard board){
        return schoolPostRepository.findTopBySchoolBoardAndIdLessThanOrderByIdDesc(board,postId).orElse(null);
    }

    public SchoolPost getAfterPostById(Long postId, SchoolBoard board){
        return schoolPostRepository.findTopBySchoolBoardAndIdGreaterThanOrderByIdAsc(board,postId).orElse(null);
    }

    public FilePost getBeforeFilePostById(Long postId, SchoolBoard board){
        return filePostRepository.findTopBySchoolBoardAndIdLessThanOrderByIdDesc(board,postId).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.FILE_POST_NOT_FOUND)
        );
    }

    public FilePost getAfterFilePostById(Long postId, SchoolBoard board){
        return filePostRepository.findTopBySchoolBoardAndIdGreaterThanOrderByIdAsc(board,postId).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.FILE_POST_NOT_FOUND)
        );
    }

    public IntroPost loadIntroById(Long introId){
        return introPostRepository.findIntroPostById(introId).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.INTRO_POST_NOT_FOUND)
        );
    }

    public IntroPost loadIntroByBoard(SchoolBoard schoolBoard){
        return introPostRepository.findIntroPostsBySchoolBoard(schoolBoard).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.INTRO_POST_NOT_FOUND)
        );
    }

    public List<SchoolResponse.SchoolPageDto> getSchoolPage(){
        List<School> schools = loadAllSchool();
        return schools.stream()
                .map(SchoolResponse.SchoolPageDto::from)
                .collect(Collectors.toList());
    }

    public List<College> loadCollegesByIntro(IntroPost introPost){
        return collegeRepository.findCollegesByIntroPost(introPost);
    }

    public College loadCollegesById(Long id){
        return collegeRepository.findCollegeById(id).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.COLLEGE_NOT_FOUND)
        );
    }

    public List<FileAttachment> loadFileByPost(SchoolPost schoolPost){
        return fileAttachmentRepository.findFileAttachmentsBySchoolPost(schoolPost);
    }

    public List<FileAttachment> loadFileByPost(long postId){
        return loadFileByPost(loadPostById(postId));
    }

    public List<SchoolResponse.PostFileDto> loadPostFileByPost(SchoolPost schoolPost){
        return SchoolResponse.PostFileDto.from(loadFileByPost(schoolPost));
    }

    public List<SchoolResponse.PostFileDto> loadPostFileByPost(Long postId){
        return SchoolResponse.PostFileDto.from(loadFileByPost(postId));
    }

    @Transactional
    public SchoolResponse.PostDetailDto getDetailPostDto(Long postId){
        log.info("enter DetailDto");
        increasePostView(postId);
        SchoolPost schoolPost = loadPostById(postId);
        SchoolResponse.SchoolPostDto dto = SchoolResponse.SchoolPostDto.from(schoolPost);
        //fixme 수정하기 QueryDSL로

        SchoolPost beforePost = schoolPostQSDLRepository.findBeforePost(schoolPost);
        SchoolPost afterPost = schoolPostQSDLRepository.findAfterPost(schoolPost);

        return new SchoolResponse.PostDetailDto(
                dto,
                loadPostFileByPost(postId),
                (beforePost==null)? null: SchoolResponse.SchoolPostSimpleDto.from(beforePost),
                (afterPost==null)? null: SchoolResponse.SchoolPostSimpleDto.from(afterPost));
    }

    @Transactional
    public SchoolResponse.FilePostDetailDto getDetailFilePostDto(Long postId){
        log.info("enter DetailDto");
        increasePostView(postId);
        FilePost filePost = loadFilePostById(postId);

        SchoolResponse.SchoolPostDto dto = SchoolResponse.SchoolPostDto.from(filePost);
        SchoolBoard schoolBoard = filePost.getSchoolBoard();
        FilePost beforePost = getBeforeFilePostById(postId,schoolBoard);
        FilePost afterPost = getAfterFilePostById(postId,schoolBoard);

        return new SchoolResponse.FilePostDetailDto(dto,
                SchoolResponse.PostFileDto.from(filePost.getFile()),
                (beforePost==null)? null: SchoolResponse.SchoolPostSimpleDto.from(beforePost),
                (afterPost==null)? null: SchoolResponse.SchoolPostSimpleDto.from(afterPost));
    }



    public FilePost loadFilePostById(Long postId){
        return filePostRepository.findFilePostById(postId).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.FILE_POST_NOT_FOUND)
        );
    }

    public SchoolSchedule loadScheduleById(Long id){
        return schoolScheduleRepository.findById(id).orElseThrow(
                () -> new SchoolHandler(SchoolErrorStatus.SCHEDULE_NOT_FOUND)
        );
    }

    public SchoolResponse.ScheduleDto getScheduleDtoById(Long id){
        return SchoolResponse.ScheduleDto.from(loadScheduleById(id));
    }

    public List<SchoolSchedule> loadScheduleByBoard(SchoolBoard schoolBoard){
        return schoolScheduleRepository.findSchoolSchedulesBySchoolBoard(schoolBoard);
    }

    public List<SchoolResponse.ScheduleDto> loadScheduleDtoByBoard(SchoolBoard schoolBoard){
        return SchoolResponse.ScheduleDto.from(loadScheduleByBoard(schoolBoard));
    }

    public Page<SchoolSchedule> loadSchedulesPageByCondition(SchoolRequest.ScheduleDto request,Pageable pageable){
        ScheduleType type = (request.type()==null)? null : ScheduleType.from(request.type());
        return schoolScheduleRepository.findSchedulesByBoardAndFlexibleDate(request.boardId(),request.startDate(),request.endDate(),request.title(),type,pageable);
    }

    public Page<SchoolResponse.ScheduleDto> getScheduleDtoPageByCondition(SchoolRequest.ScheduleDto request,Pageable pageable){
        return SchoolResponse.ScheduleDto.from(loadSchedulesPageByCondition(request,pageable));
    }

    @Transactional
    public void increasePostView(Long postId) {
        schoolPostRepository.incrementViewCount(postId); // 방법 1 사용
    }

}
