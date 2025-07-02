package com.cooperation.project.cooperationcenter.domain.file.service;

import com.cooperation.project.cooperationcenter.domain.file.dto.SurveyFileDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileType;
import com.cooperation.project.cooperationcenter.domain.file.model.SurveyFile;
import com.cooperation.project.cooperationcenter.domain.file.repository.SurveyFileRepository;
import com.cooperation.project.cooperationcenter.domain.survey.dto.AnswerRequest;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final SurveyFileRepository surveyFileRepository;

    public SurveyFile saveFile(SurveyFileDto request){
        String path = "uploads/survey/"+request.surveyId();
        if(request.fileType()!=null) path+="/"+request.fileType().getFileType();
        log.info("path:{}",path);
        Path uploadDir = Paths.get(System.getProperty("user.dir"), path);

        try {
            Files.createDirectories(uploadDir); // 경로 없으면 생성
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SurveyFile surveyFile = SurveyFile.builder()
                .filePath(path)
                .realPath(uploadDir)
                .fileType(request.fileType())
                .file(request.file())
                .build();

        Path saved = uploadDir.resolve(surveyFile.getFileName());

        try {
            request.file().transferTo(saved);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return surveyFileRepository.save(surveyFile);
    }

    public ResponseEntity<Resource> loadSurveyFile(String fileId) throws MalformedURLException {
        SurveyFile file = surveyFileRepository.findSurveyFileByFileId(fileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));

        Path filePath = Paths.get(file.getFilePath()).resolve(file.getFileName());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 읽을 수 없습니다.");
        }

        String encodedName = UriUtils.encode(file.getFileRealName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}
