package com.cooperation.project.cooperationcenter.domain.file.controller;

import com.cooperation.project.cooperationcenter.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
@Slf4j
public class FileRestController {

    private final FileService fileService;

    @GetMapping("/{type}/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String type,@PathVariable String fileId) throws MalformedURLException {
        log.info("enter file controller");
        return fileService.loadFile(fileId,type);
    }

    @GetMapping("/img/{type}/{fileId}")
    public ResponseEntity<Resource> viewImage(@PathVariable String type,@PathVariable String fileId) throws IOException {
        log.info("enter file controller-img");
        return fileService.viewImage(fileId,type);
    }

}
