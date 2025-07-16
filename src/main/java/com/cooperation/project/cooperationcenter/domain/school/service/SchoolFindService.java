package com.cooperation.project.cooperationcenter.domain.school.service;

import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.School;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolBoard;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import com.cooperation.project.cooperationcenter.domain.school.repository.SchoolBoardRepository;
import com.cooperation.project.cooperationcenter.domain.school.repository.SchoolPostRepository;
import com.cooperation.project.cooperationcenter.domain.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public List<SchoolResponse.SchoolDto> loadAllSchoolByDto(){
        try{
            return schoolRepository.findAll().stream()
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

    public List<SchoolPost> loadPostByBoard(SchoolBoard board){
        try{
            return schoolPostRepository.findBySchoolBoard(board);
        }catch(Exception e){
            log.warn(e.getMessage());
            return Collections.emptyList();
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


}
