package com.cooperation.project.cooperationcenter.domain.school.service;

import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolRequest;
import com.cooperation.project.cooperationcenter.domain.school.dto.SchoolResponse;
import com.cooperation.project.cooperationcenter.domain.school.model.*;
import com.cooperation.project.cooperationcenter.domain.school.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final SchoolBoardRepository schoolBoardRepository;
    private final SchoolPostRepository schoolPostRepository;
    private final IntroPostRepository introPostRepository;
    private final FilePostRepository filePostRepository;
    private final SchoolScheduleRepository schoolScheduleRepository;

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

        if(schoolBoard.getType().equals(SchoolBoard.BoardType.INTRO)){
            IntroPost introPost = IntroPost.builder()
                    .title(schoolBoard.getBoardTitle())
                    .content("introductionTemplate")
                    .schoolBoard(schoolBoard)
                    .build();
            schoolBoard.setIntroPost(introPost);
        }
        school.addBoard(schoolBoardRepository.save(schoolBoard));
    }

    @Transactional
    public void savePost(SchoolRequest.SchoolPostDto request,List<MultipartFile> files){
        SchoolBoard board = schoolFindService.loadBoardById(request.boardId());
        SchoolPost post = SchoolPost.fromDto(request);

        post = schoolPostRepository.save(post);

        if (files != null && !files.isEmpty()) {
            List<FileAttachment> attachments = new ArrayList<>();
            for (MultipartFile file : files) {
                attachments.add(
                        fileService.saveFile(new FileAttachmentDto(file, "SCHOOL", post.getId().toString(), null, null))
                );
            }
            post.addFile(attachments);
        }
        board.addPost(schoolPostRepository.save(post));
    }

    @Transactional
    public void saveSchedule(SchoolRequest.ScheduleDto request){
        SchoolBoard board = schoolFindService.loadBoardById(request.boardId());
        SchoolSchedule schedule = SchoolSchedule.fromDto(request);

        board.addSchedule(schoolScheduleRepository.save(schedule));
    }

    @Transactional
    public void saveIntro(SchoolRequest.IntroDto request){
        SchoolBoard board = schoolFindService.loadBoardById(request.boardId());
        IntroPost introPost = IntroPost.fromDto(request);

        introPost = introPostRepository.save(introPost);
        board.setIntroPost(introPost);

        //todo 여기에는 그냥 내용 수정된거 저장하는 용도
        schoolBoardRepository.save(board);
    }

    @Transactional
    public void editPost(SchoolRequest.SchoolPostDto request,List<MultipartFile> files){
        SchoolPost post = schoolFindService.loadPostById(request.postId());
        post.updateFromDto(request);
        //todo  request.deleteFileIds()이거 있는거 삭제하기
        deleteFileWithPost(post,request.deleteFileIds());

        //fixme file 부분 있으면 저장 안해야함 수정하기
        if (files != null && !files.isEmpty()) {
            List<FileAttachment> attachments = new ArrayList<>();
            for (MultipartFile file : files) {
                attachments.add(
                        fileService.saveFile(new FileAttachmentDto(file, "SCHOOL", post.getId().toString(), null, null))
                );
            }
            post.addFile(attachments);
        }
    }

    public void deleteFileWithPost(SchoolPost post, List<String> fileId){
        if(fileId == null || fileId.isEmpty()) return;
        log.info("deleteFileWithPost: fileId null아님");

        List<FileAttachment> attachments = fileId.stream().map(
                (id) ->  fileService.loadFileAttachment(id,"school")
        ).toList();

        attachments.forEach(file -> {
            post.deleteFile(file);
            fileService.deleteFile(file);
        });
    }

    public Page<SchoolResponse.SchoolPostDto> getPostByPage(SchoolRequest.PostDto request, Pageable pageable){
        SchoolBoard board = schoolFindService.loadBoardById(request.boardId());
        SchoolBoard.BoardType type = board.getType();
        if(type == SchoolBoard.BoardType.NOTICE) return schoolFindService.loadPostPageByBoardByDto(board,pageable);
        else return schoolFindService.loadFilePostPageByBoardByDto(board,pageable);
    }

    @Transactional
    public void deleteBoard(SchoolRequest.BoardIdDto request){
        deleteBoard(request.boardId());
    }

    @Transactional
    public void deleteBoard(Long boardId){
        SchoolBoard board = schoolFindService.loadBoardById(boardId);
        List<SchoolPost> postListCopy = new ArrayList<>(board.getPosts());
        for (SchoolPost post : postListCopy) deletePost(post.getId());
        board.deleteSchool();
        schoolBoardRepository.delete(board);
    }

    @Transactional
    public void saveFilePost(SchoolRequest.FilePostDto request,MultipartFile file){
        SchoolBoard board = schoolFindService.loadBoardById(request.boardId());

        FilePost post = FilePost.fromDto(request);
        post.setBoard(board);
        post = filePostRepository.saveAndFlush(post); // IDENTITY면 saveAndFlush가 안전
        FileAttachment requestFile = fileService.saveFile(new FileAttachmentDto(file, "SCHOOL", post.getId().toString(), null, null));
        post.setFile(requestFile);
        board.addFilePost(post);
    }

    @Transactional
    public void editFilePost(SchoolRequest.FilePostDto request, MultipartFile requestFile){
        FilePost post = schoolFindService.loadFilePostById(request.postId());
        post.updateFormDto(request);
        //todo  request.deleteFileIds()이거 있는거 삭제하기

        FileAttachment file = post.getFile();
        if(request.deleteFileIds().equals(file.getFileId())){
            fileService.deleteFile(file);
            post.deleteFile();
        }

        //fixme file 부분 있으면 저장 안해야함 수정하기
        if (requestFile != null && !requestFile.isEmpty()) {
            post.setFile(fileService.saveFile(new FileAttachmentDto(requestFile, "SCHOOL", post.getId().toString(), null, null)));
        }
    }

    @Transactional
    public void deleteFilePost(SchoolRequest.PostIdDto request){
        FilePost post = schoolFindService.loadFilePostById(request.postId());
        fileService.deleteFile(post.getFile());
        post.deleteFile();
        filePostRepository.delete(post);
        log.info("filePost delete complete");
    }

    @Transactional
    public void deletePost(SchoolRequest.PostIdDto request){
        deletePost(request.postId());
    }

    @Transactional
    public void editSchedule(SchoolRequest.ScheduleDto request){
        SchoolSchedule schedule = schoolFindService.loadScheduleById(request.scheduleId());
        schedule.updateFromDto(request);
    }

    @Transactional
    public void deleteSchedule(SchoolRequest.PostIdDto request){
        SchoolSchedule schedule = schoolFindService.loadScheduleById(request.postId());
        SchoolBoard board = schedule.getSchoolBoard();
        board.deleteSchedule(schedule);
        schedule.setBoard(null);

        schoolScheduleRepository.delete(schedule);
    }

    @Transactional
    public void deletePost(long postId){
        SchoolPost post = schoolFindService.loadPostById(postId);
        post.deleteBoard();
        List<FileAttachment> attachments = new ArrayList<>(post.getFiles());
        post.deleteFile();
        fileService.deleteFile(attachments);
        schoolPostRepository.delete(post);
        log.info("post delete complete");
    }

    public void getScheduleDto(){

    }

}
