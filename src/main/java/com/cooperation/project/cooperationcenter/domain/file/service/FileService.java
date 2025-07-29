package com.cooperation.project.cooperationcenter.domain.file.service;

import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.model.FileTargetType;

import com.cooperation.project.cooperationcenter.domain.file.repository.FileAttachmentRepository;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileAttachmentRepository fileAttachmentRepository;

    public String getPath(FileAttachmentDto request){
        String type = request.type();
        FileTargetType fileType = FileTargetType.fromType(type);

        if(fileType.equals(FileTargetType.MEMBER)) return FileTargetType.MEMBER.getFilePath()+request.memberId();
        else if(fileType.equals(FileTargetType.SCHOOL)) return FileTargetType.SCHOOL.getFilePath()+request.postId();
        else if(fileType.equals(FileTargetType.SURVEY)) return FileTargetType.SURVEY.getFilePath()+request.surveyId();
        else return null;
    }

    public FileAttachment saveFile(FileAttachmentDto request){
        String path = getPath(request);
        FileTargetType fileType = FileTargetType.fromType(request.type());
        Path uploadDir = Paths.get(System.getProperty("user.dir"), path);

        try {
            Files.createDirectories(uploadDir); // 경로 없으면 생성
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileAttachment file = FileAttachment.builder()
                .path(path)
                .storedPath(uploadDir)
                .file(request.file())
                .filetype(fileType)
                .build();

        Path saved = uploadDir.resolve(file.getStoredName());

        try {
            request.file().transferTo(saved);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileAttachmentRepository.save(file);
    }

    public FileAttachment loadFileAttachment(String fileId,String type){
        try{
            FileTargetType fileType = FileTargetType.fromType(type);
            return fileAttachmentRepository.findByFileIdAndFiletype(fileId,fileType).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public ResponseEntity<Resource> loadFile(String fileId,String type) throws MalformedURLException {
        FileTargetType fileType = FileTargetType.fromType(type);
        FileAttachment file = fileAttachmentRepository.findByFileIdAndFiletype(fileId,fileType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));

        Path filePath = Paths.get(file.getPath()).resolve(file.getStoredName());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 읽을 수 없습니다.");
        }

        String encodedName = UriUtils.encode(file.getOriginalName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public ResponseEntity<Resource> viewImage(String fileId,String type) throws IOException {
        FileTargetType fileType = FileTargetType.fromType(type);
        FileAttachment file = fileAttachmentRepository.findByFileIdAndFiletype(fileId,fileType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));
        log.info("파일 읽어옴1");
        Path filePath = Paths.get(file.getPath()).resolve(file.getStoredName());
        Resource resource = new UrlResource(filePath.toUri());
        log.info("파일 읽어옴2");
        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 읽을 수 없습니다.");
        }

        log.info("파일 읽어옴3");
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        log.info("파일 확장자 확인");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    public void deleteFile(FileAttachment fileAttachment){
        try{
            fileAttachmentRepository.delete(fileAttachment);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }

    public void deleteFile(List<FileAttachment> fileAttachments){
        try{
            fileAttachmentRepository.deleteAll(fileAttachments);
        }catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public  Map<String, Object> saveSchoolImgAndReturnUrl(String type, MultipartFile file){
        log.info("save image enter...");
        String path = FileTargetType.SCHOOL.getFilePath()+"img";
        FileTargetType fileType = FileTargetType.fromType(type);
        Path uploadDir = Paths.get(System.getProperty("user.dir"), path);

        try {
            Files.createDirectories(uploadDir); // 경로 없으면 생성
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileAttachment attachment = FileAttachment.builder()
                .path(path)
                .storedPath(uploadDir)
                .file(file)
                .filetype(fileType)
                .build();

        Path saved = uploadDir.resolve(attachment.getStoredName());

        try {
            file.transferTo(saved);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileAttachmentRepository.save(attachment);
        String url = "/api/v1/file/img/school/"+attachment.getFileId();

        Map<String, Object> response = new HashMap<>();
        response.put("result", List.of(Map.of(
                "url", url,
                "name", attachment.getOriginalName(),
                "size", attachment.getSize(),
                "align", "center",
                "tag", "img"
        )));
        return response;
    }


}
