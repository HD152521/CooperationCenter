package com.cooperation.project.cooperationcenter.domain.file.controller;

import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
@Slf4j
public class FileRestController {

    private final FileService fileService;

    //note 다운로드용
    @GetMapping("/{type}/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String type,@PathVariable String fileId) throws MalformedURLException {
        log.info("enter file controller");
        return fileService.loadFile(fileId,type);
    }

    //note 이미지 뷰용
    @GetMapping("/img/{type}/{fileId}")
    public ResponseEntity<Resource> viewImage(@PathVariable String type,@PathVariable String fileId) throws IOException {
        log.info("enter file controller-img");
        return fileService.viewImage(fileId,type);
    }

    //note 이미지 뷰용
    @GetMapping("/pdf/{type}/{fileId}")
    public ResponseEntity<Resource> viewPdf(@PathVariable String type,@PathVariable String fileId) throws IOException {
        log.info("enter file controller-img");
        return fileService.viewPdf(fileId,type);
    }

    @PostMapping("/{type}")
    public ResponseEntity<?> saveFile(@PathVariable String type,@RequestParam("file-0") MultipartFile file) throws IOException {
        log.info("save file");
        return ResponseEntity.ok(fileService.saveSchoolImgAndReturnUrl(type, file));
    }

}
