package com.cooperation.project.cooperationcenter.domain.school.service;

import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.School;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import com.cooperation.project.cooperationcenter.domain.school.repository.SchoolBoardRepository;
import com.cooperation.project.cooperationcenter.domain.school.repository.SchoolPostRepository;
import com.cooperation.project.cooperationcenter.domain.school.repository.SchoolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final SchoolBoardRepository schoolBoardRepository;
    private final SchoolPostRepository schoolPostRepository;

    private final SchoolFindService schoolFindService;
    private final FileService fileService;

    @Transactional
    public void saveSchool(SchoolRequest.SchoolDto request){
        School school = School.fromDto(request);
        schoolRepository.save(school);
    }

    @Transactional
    public void saveBoard(SchoolRequest.SchoolBoardDto request){
        School school = schoolFindService.loadSchoolById(request.schoolId());
        SchoolBoard schoolBoard = SchoolBoard.fromDto(request);
        school.addBoard(schoolBoardRepository.save(schoolBoard));
    }

    @Transactional
    public void savePost(SchoolRequest.SchoolPostDto request,List<MultipartFile> files){
        SchoolBoard board = schoolFindService.loadBoardById(request.boardId());
        SchoolPost post = SchoolPost.fromDto(request);
        post = schoolPostRepository.save(post);
        List<FileAttachment> attachments = new ArrayList<>();
        for(MultipartFile file : files){
            attachments.add(
                    fileService.saveFile(new FileAttachmentDto(file,"SCHOOL",post.getId().toString(),null,null)
                    ))
            ;
        }
        post.addFile(attachments);
        board.addPost(schoolPostRepository.save(post));
    }

    public Page<SchoolResponse.SchoolPostDto> getPostByPage(SchoolRequest.PostDto request, Pageable pageable){
        SchoolBoard board = schoolFindService.loadBoardById(request.boardId());
        return schoolFindService.loadPostPageByBoardByDto(board,pageable);
    }



}
