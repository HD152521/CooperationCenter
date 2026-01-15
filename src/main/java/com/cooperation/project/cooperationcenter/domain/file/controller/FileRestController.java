package com.cooperation.project.cooperationcenter.domain.file.controller;

import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import com.cooperation.project.cooperationcenter.domain.oss.OssService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
@Slf4j
public class FileRestController {

    private final FileService fileService;
    private final OssService ossService;

    @GetMapping("/{type}/{fileId}")
    @Operation(summary = "파일 다운로드")
    public ResponseEntity<Void> downloadFile(@PathVariable String type,@PathVariable String fileId) throws MalformedURLException {
        log.info("enter file controller");
        return fileService.loadFile(fileId,type);
    }

    @GetMapping("/img/{type}/{fileId}")
    @Operation(summary = "이미지 뷰")
    public ResponseEntity<Void> viewImage(@PathVariable String type,@PathVariable String fileId) throws IOException {
        log.info("enter file controller-img");
        return fileService.viewFile(fileId,type);
    }

    @GetMapping("/pdf/{type}/{fileId}")
    @Operation(summary = "pdf새 탭으로 열기")
    public ResponseEntity<StreamingResponseBody> viewPdf(@PathVariable String type, @PathVariable String fileId) throws IOException {
        log.info("enter file controller-img");
        return fileService.viewPdf(fileId,type);
    }

    @PostMapping("/{type}")
    @Operation(summary = "학교 이미지 저장")
    public ResponseEntity<Void> saveFile(@PathVariable String type,@RequestParam("file-0") MultipartFile file) throws IOException {
        log.info("save file");
        return fileService.saveSchoolImgAndReturnUrl(type, file);
    }

    @GetMapping("/default/agency")
    @Operation(summary = "유학원 대체 이미지")
    public ResponseEntity<Void> getAgencyDefaultImage(){
        log.info("enter agency");
        return fileService.viewDefaultImg("agency");
    }

    @GetMapping("/default/school")
    @Operation(summary = "학교 대체 이미지")
    public ResponseEntity<Void> getSchoolDefaultImage(){
        return fileService.viewDefaultImg("school");
    }
}
