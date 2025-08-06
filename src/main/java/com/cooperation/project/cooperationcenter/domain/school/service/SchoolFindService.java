package com.cooperation.project.cooperationcenter.domain.school.service;

import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.repository.FileAttachmentRepository;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.*;
import com.cooperation.project.cooperationcenter.domain.school.repository.*;
import com.cooperation.project.cooperationcenter.global.exception.BaseException;
import com.cooperation.project.cooperationcenter.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final FileAttachmentRepository fileAttachmentRepository;

    public List<SchoolResponse.SchoolDto> loadAllSchoolByDto(){
        try{
            return loadAllSchool().stream()
                    .map(SchoolResponse.SchoolDto::from)
                    .collect(Collectors.toList());
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<School> loadAllSchool(){
        try{
            return schoolRepository.findAll();
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public SchoolResponse.SchoolDto loadSchoolByEnglishNameByDto(String englishName){
        try{
            return SchoolResponse.SchoolDto.from(schoolRepository.findSchoolBySchoolEnglishName(englishName).orElseThrow(
                    () -> new BaseException(ErrorCode.BAD_REQUEST)
            ));
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public School loadSchoolByEnglishName(String englishName){
        log.info("loadEnglishName : {}",englishName);
        try{
            return schoolRepository.findSchoolBySchoolEnglishName(englishName).orElseThrow(
                    () -> new BaseException(ErrorCode.BAD_REQUEST)
            );
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public School loadSchoolById(Long id){
        try{
            return schoolRepository.findSchoolById(id).orElseThrow(() -> new BaseException(ErrorCode.BAD_REQUEST));
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SchoolBoard> loadBoardBySchool(School school){
        try{
            return schoolBoardRepository.findBySchool(school);
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<SchoolResponse.SchoolBoardDto> loadBoardBySchoolByDto(School school){
        try{
            return schoolBoardRepository.findBySchool(school).stream()
                    .map(SchoolResponse.SchoolBoardDto::from)
                    .collect(Collectors.toList());
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public SchoolBoard loadBoardById(Long boardId){
            try{
                return schoolBoardRepository.findSchoolBoardById(boardId).orElseThrow(()-> new BaseException(ErrorCode.BAD_REQUEST));
            }catch(Exception e){
                log.warn(e.getMessage());
                return null;
            }
    }

    public SchoolPost loadPostById(Long postId){
        try{
            return schoolPostRepository.findById(postId).orElseThrow(()-> new BaseException(ErrorCode.BAD_REQUEST));
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public SchoolResponse.SchoolPostDto loadPostByIdByDto(Long postId){
        try{
            return SchoolResponse.SchoolPostDto.from(loadPostById(postId));
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SchoolPost> loadPostByBoard(SchoolBoard board){
        try{
            return schoolPostRepository.findBySchoolBoard(board);
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public Page<SchoolPost> loadPostByPage(SchoolBoard board, Pageable pageable){
        try{
            return schoolPostRepository.findBySchoolBoard(board,pageable);
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SchoolResponse.SchoolPostDto> loadPostByBoardByDto(SchoolBoard board){
        try{
            return schoolPostRepository.findBySchoolBoard(board).stream()
                    .map(SchoolResponse.SchoolPostDto::from)
                    .collect(Collectors.toList());
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
        }
    }

    public Page<SchoolResponse.SchoolPostDto> loadPostByBoardByDto(SchoolBoard board,Pageable pageable){
        try{
            return schoolPostRepository.findPostsByBoardOrderByNoticeFirst(board.getId(),pageable)
                    .map(SchoolResponse.SchoolPostDto::from);
        }catch(Exception e){
            log.warn(e.getMessage());
            return Page.empty();
        }
    }

    public Page<SchoolResponse.SchoolPostDto> loadPostPageByBoardByDto(SchoolBoard board,Pageable pageable){
        try{
            return loadPostByPage(board,pageable)
                    .map(SchoolResponse.SchoolPostDto::from);
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public SchoolPost getBeforePostById(Long postId, SchoolBoard board){
        try{
            return schoolPostRepository.findTopBySchoolBoardAndIdLessThanOrderByIdDesc(board,postId).orElse(null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public SchoolPost getAfterPostById(Long postId, SchoolBoard board){
        try{
            return schoolPostRepository.findTopBySchoolBoardAndIdGreaterThanOrderByIdAsc(board,postId).orElse(null);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }


    public IntroPost loadIntroById(Long introId){
        try{
            return introPostRepository.findIntroPostById(introId).orElseThrow(
                    () -> new BaseException(ErrorCode.BAD_REQUEST)
            );
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public SchoolResponse.IntroDto loadIntroByIdByDto(Long introId){
        try{
            return SchoolResponse.IntroDto.from(loadIntroById(introId));
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public IntroPost loadIntroByBoard(SchoolBoard schoolBoard){
        try{
            return introPostRepository.findIntroPostsBySchoolBoard(schoolBoard).orElseThrow(
                    () -> new BaseException(ErrorCode.BAD_REQUEST)
            );
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public SchoolResponse.IntroDto loadIntroByBoardByDto(SchoolBoard schoolBoard){
        try{
            return SchoolResponse.IntroDto.from(loadIntroByBoard(schoolBoard));
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public List<SchoolResponse.SchoolPageDto> getSchoolPage(){
        List<School> schools = loadAllSchool();
        return schools.stream()
                .map(SchoolResponse.SchoolPageDto::from)
                .collect(Collectors.toList());
    }

    public List<FileAttachment> loadFileByPost(SchoolPost schoolPost){
        try{
            return fileAttachmentRepository.findFileAttachmentsBySchoolPost(schoolPost);
        }catch (Exception e){
            log.warn("post file가져오는데 실패");
            return Collections.emptyList();
        }
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
        SchoolBoard schoolBoard = schoolPost.getSchoolBoard();
        SchoolPost beforePost = getBeforePostById(postId,schoolBoard);
        SchoolPost afterPost = getAfterPostById(postId,schoolBoard);

        return new SchoolResponse.PostDetailDto(dto,
                loadPostFileByPost(postId),
                (beforePost==null)? null: SchoolResponse.SchoolPostSimpleDto.from(beforePost),
                (afterPost==null)? null: SchoolResponse.SchoolPostSimpleDto.from(afterPost));
    }

    public FilePost loadFilePostById(Long postId){
        try{
            return filePostRepository.findFilePostById(postId).orElseGet(null);
        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }


    @Transactional
    public void increasePostView(Long postId) {
        schoolPostRepository.incrementViewCount(postId); // 방법 1 사용
    }

}
