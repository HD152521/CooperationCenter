package com.cooperation.project.cooperationcenter.domain.file.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.model.FileTargetType;

import com.cooperation.project.cooperationcenter.domain.file.repository.FileAttachmentRepository;
import com.cooperation.project.cooperationcenter.domain.oss.OssService;
import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
    private final OSS oss;
    private final OssService ossService;
    @Value("${oss.bucket}") private String bucket;

    public String getPath(FileAttachmentDto request){
        String type = request.type();
        FileTargetType fileType = FileTargetType.fromType(type);

        if(fileType.equals(FileTargetType.MEMBER)) return FileTargetType.MEMBER.getFilePath()+request.memberId();
        else if(fileType.equals(FileTargetType.SCHOOL)) return FileTargetType.SCHOOL.getFilePath()+request.postId();
        else if(fileType.equals(FileTargetType.SURVEY)) return FileTargetType.SURVEY.getFilePath()+request.surveyId();
        else return null;
    }

    /** 파일을 로컬에 저장하지 않고 바로 업로드 */
    @Transactional
    public FileAttachment saveFile(FileAttachmentDto request) {
        MultipartFile file = request.file();
        try (InputStream in = file.getInputStream()) {
            String path = getPath(request);
            FileTargetType fileType = FileTargetType.fromType(request.type());
            FileAttachment inputFile = saveFileModel(path,file,fileType);

            String key = String.format("%s/%s",
                    path, inputFile.getStoredName()); // 예: uploads/uuid_name.png

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getSize());
            if (file.getContentType() != null) meta.setContentType(file.getContentType());
            meta.setHeader("x-oss-server-side-encryption", "AES256");
            //            meta.setHeader(OSSHeaders.SERVER_SIDE_ENCRYPTION, "AES256");

            oss.putObject(bucket, key, in, meta);
            return inputFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload to OSS", e);
        }
    }

    @Transactional
    public FileAttachment saveFileModel(String path, MultipartFile file, FileTargetType type){
        FileAttachment inputFile = FileAttachment.builder()
                .path(path)
                .storedPath(path)
                .file(file)
                .filetype(type)
                .build();

        return fileAttachmentRepository.save(inputFile);
    }

    public String getKey(FileAttachment file){
        return file.getPath();
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

    public FileAttachment loadFileAttachment(String fileId,FileTargetType type){
        try{
            return fileAttachmentRepository.findByFileIdAndFiletype(fileId,type).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
            );
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public ResponseEntity<Void> loadFile(String fileId,String type) {
        try{
            FileTargetType fileType = FileTargetType.fromType(type);
            FileAttachment file = fileAttachmentRepository.findByFileIdAndFiletype(fileId,fileType)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));

            URL url =  getDownloadUrl(file);

            log.info("ket:{}",file.getPath());
            return ResponseEntity.status(HttpStatus.FOUND) // 302
                    .location(URI.create(url.toString()))
                    .build();

        }catch(Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public ResponseEntity<Void> viewFile(String fileId,String type){
        try{
            FileTargetType fileType = FileTargetType.fromType(type);
            FileAttachment file = fileAttachmentRepository.findByFileIdAndFiletype(fileId,fileType)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));

            URL url = getViewUrl(file);
            log.info("ket:{}",file.getPath());
            return ResponseEntity.status(HttpStatus.FOUND) // 302
                    .location(URI.create(url.toString()))
                    .build();

        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    public ResponseEntity<StreamingResponseBody> viewPdf(String fileId,String type){
        try{
            FileTargetType fileType = FileTargetType.fromType(type);
            FileAttachment file = fileAttachmentRepository.findByFileIdAndFiletype(fileId,fileType)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."));

            URL url = getViewUrl(file);
            log.info("ket:{}",file.getPath());

            final String key = file.getPath();
            final String filename = file.getStoredName();
            final String contentType = (file.getContentType() != null) ? file.getContentType() : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));       // 예: application/pdf
            headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(filename));
            headers.setCacheControl("public, max-age=600");

            StreamingResponseBody body = outputStream -> {
                try (InputStream in = openObject(key)) {
                    in.transferTo(outputStream);
                }
            };

            return new ResponseEntity<>(body, headers, HttpStatus.OK);
        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

    private String contentDispositionInline(String filename) {
        String enc = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "inline; filename=\"" + enc + "\"; filename*=UTF-8''" + enc;
    }

    public InputStream openObject(String key) {
        var obj = oss.getObject(bucket, key);
        return obj.getObjectContent(); // 반드시 호출 측에서 close
    }

    @Transactional
    public void deleteFile(FileAttachment fileAttachment){
        try{
            if (!oss.doesObjectExist(bucket, fileAttachment.getPath())) return; // 혹은 로그만
            oss.deleteObject(bucket, fileAttachment.getPath());

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

    public void deleteFileById(String fileId,FileTargetType type){
        try{
            FileAttachment file = loadFileAttachment(fileId,type);
            deleteFile(file);
        }catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public URL getViewUrl(FileAttachment file){
        return ossService.presignedGetUrl(file.getPath(), 15, false, file.getStoredName(), null);
    }

    public URL getViewUrl(String path,String fileName){
        log.info("path:{}, fileName:{}",path,fileName);
        return ossService.presignedGetUrl(path, 15, false, null, null);
    }

    public URL getDownloadUrl(FileAttachment file){
        return ossService.presignedGetUrl(file.getPath(), 15, true, file.getStoredName(), file.getContentType());
    }

    public  ResponseEntity<Void> saveSchoolImgAndReturnUrl(String type, MultipartFile file){
        log.info("save image enter...");
        String path = FileTargetType.SCHOOL.getFilePath()+"img";
        FileTargetType fileType = FileTargetType.fromType(type);

        FileAttachment attachment = saveFileModel(path,file,fileType);

        URL url = getViewUrl(attachment);
        return ResponseEntity.status(HttpStatus.FOUND) // 302
                .location(URI.create(url.toString()))
                .build();
    }

    public ResponseEntity<Void> viewDefaultImg(String type){
        try{
            String fileName = null;
            if(type.equalsIgnoreCase("agency")) fileName = "agency_default.png";
            else if(type.equalsIgnoreCase("school")) fileName = "school_default.jpg";
            URL url = getViewUrl(fileName,fileName);
            log.info("url log:{}",url.toString());
            log.info("url log:{}",fileName);
            return ResponseEntity.status(HttpStatus.FOUND) // 302
                    .location(URI.create(url.toString()))
                    .build();

        }catch (Exception e){
            log.warn(e.getMessage());
            return null;
        }
    }

}
