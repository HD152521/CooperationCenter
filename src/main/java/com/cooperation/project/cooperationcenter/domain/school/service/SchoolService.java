package com.cooperation.project.cooperationcenter.domain.school.service;

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
import org.springframework.stereotype.Service;

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
    public void savePost(SchoolRequest.SchoolPostDto request){
        SchoolBoard board = schoolFindService.loadBoardById(request.BoardId());
        SchoolPost post = SchoolPost.fromDto(request);
        board.addPost(schoolPostRepository.save(post));
    }

    public List<SchoolResponse.SchoolPostDto> getPost(String boardId){
        List<SchoolResponse.SchoolPostDto> response = new ArrayList<>();
        return response;
    }



}
