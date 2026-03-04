package com.cooperation.project.cooperationcenter.domain.file.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.cooperation.project.cooperationcenter.domain.file.dto.FileAttachmentDto;
import com.cooperation.project.cooperationcenter.domain.file.exception.FileHandler;
import com.cooperation.project.cooperationcenter.domain.file.exception.status.FileErrorStatus;
import com.cooperation.project.cooperationcenter.domain.file.model.FileAttachment;
import com.cooperation.project.cooperationcenter.domain.file.model.FileTargetType;
import com.cooperation.project.cooperationcenter.domain.file.repository.FileAttachmentRepository;
import com.cooperation.project.cooperationcenter.domain.oss.OssService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final OSS oss;
    private final OssService ossService;
    @Value("${oss.bucket}")
    private String bucket;

    public String getPath(FileAttachmentDto request) {
        FileTargetType fileType = resolveFileType(request.type());
        return switch (fileType) {
            case MEMBER -> FileTargetType.MEMBER.getFilePath() + request.memberId();
            case SCHOOL -> FileTargetType.SCHOOL.getFilePath() + request.postId();
            case SURVEY -> FileTargetType.SURVEY.getFilePath() + request.surveyId();
        };
    }

    /** 파일을 로컬에 저장하지 않고 바로 업로드 */
    @Transactional
    public FileAttachment saveFile(FileAttachmentDto request) {
        MultipartFile file = request.file();

        if (file == null || file.isEmpty()) {
            throw new FileHandler(FileErrorStatus.FILE_EMPTY);
        }

        FileTargetType fileType = resolveFileType(request.type());
        String path = getPath(request);

        FileAttachment inputFile = saveFileModel(path, file, fileType);
        uploadObject(path, file, inputFile.getStoredName());
        return inputFile;
    }

    @Transactional
    public FileAttachment saveFileModel(String path, MultipartFile file, FileTargetType type) {
        FileAttachment inputFile = FileAttachment.builder()
                .path(path)
                .storedPath(path)
                .file(file)
                .filetype(type)
                .build();

        return fileAttachmentRepository.save(inputFile);
    }

    public String getKey(FileAttachment file) {
        return file.getPath();
    }

    public FileAttachment loadFileAttachment(String fileId, String type) {
        FileTargetType fileType = resolveFileType(type);
        return fileAttachmentRepository.findByFileIdAndFiletype(fileId, fileType)
                .orElseThrow(() -> new FileHandler(FileErrorStatus.FILE_META_NOT_FOUND));
    }

    public FileAttachment loadFileAttachment(String fileId, FileTargetType type) {
        return fileAttachmentRepository.findByFileIdAndFiletype(fileId, type)
                .orElseThrow(() -> new FileHandler(FileErrorStatus.FILE_META_NOT_FOUND));
    }

    public ResponseEntity<Void> loadFile(String fileId, String type) {
        FileAttachment file = findAttachment(fileId, type);
        URL url = getDownloadUrl(file);

        log.info("key:{}", file.getPath());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.toString()))
                .build();
    }

    public ResponseEntity<Void> viewFile(String fileId, String type) {
        FileAttachment file = findAttachment(fileId, type);
        URL url = getViewUrl(file);

        log.info("key:{}", file.getPath());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.toString()))
                .build();
    }

    public ResponseEntity<StreamingResponseBody> viewPdf(String fileId, String type) {
        FileAttachment file = findAttachment(fileId, type);

        final String key = file.getPath();
        final String filename = file.getStoredName();
        final String contentType = (file.getContentType() != null) ? file.getContentType() : "application/octet-stream";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(filename));
        headers.setCacheControl("public, max-age=600");

        StreamingResponseBody body = outputStream -> {
            try (InputStream in = openObject(key)) {
                in.transferTo(outputStream);
            }
        };

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    private String contentDispositionInline(String filename) {
        String enc = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "inline; filename=\"" + enc + "\"; filename*=UTF-8''" + enc;
    }

    public InputStream openObject(String key) {
        var obj = oss.getObject(bucket, key);
        return obj.getObjectContent();
    }

    @Transactional
    public void deleteFile(FileAttachment fileAttachment) {
        try {
            if (fileAttachment == null) {
                return;
            }
            if (oss.doesObjectExist(bucket, fileAttachment.getPath())) {
                oss.deleteObject(bucket, fileAttachment.getPath());
            }
            fileAttachmentRepository.delete(fileAttachment);
        } catch (Exception e) {
            log.error("File delete error", e);
            throw new FileHandler(FileErrorStatus.FILE_DELETE_ERROR);
        }
    }

    @Transactional
    public void deleteFile(List<FileAttachment> fileAttachments) {
        if (fileAttachments == null || fileAttachments.isEmpty()) {
            return;
        }
        fileAttachments.forEach(this::deleteFile);
    }

    @Transactional
    public void deleteFileById(String fileId, FileTargetType type) {
        FileAttachment file = loadFileAttachment(fileId, type);
        deleteFile(file);
    }

    public URL getViewUrl(FileAttachment file) {
        try {
            return ossService.presignedGetUrl(file.getPath(), 15, false, file.getStoredName(), null);
        } catch (Exception e) {
            log.error("Presigned URL generation failed", e);
            throw new FileHandler(FileErrorStatus.FILE_URL_GENERATE_ERROR);
        }
    }

    public URL getViewUrl(String path, String fileName) {
        return getViewUrl(path);
    }

    public URL getViewUrl(String path) {
        try {
            return ossService.presignedGetUrl(path, 15, false, null, null);
        } catch (Exception e) {
            log.error("Presigned URL generation failed", e);
            throw new FileHandler(FileErrorStatus.FILE_URL_GENERATE_ERROR);
        }
    }

    public URL getDownloadUrl(FileAttachment file) {
        try {
            return ossService.presignedGetUrl(file.getPath(), 15, true, file.getStoredName(), file.getContentType());
        } catch (Exception e) {
            log.error("Presigned Download URL generation failed", e);
            throw new FileHandler(FileErrorStatus.FILE_URL_GENERATE_ERROR);
        }
    }

    public ResponseEntity<Void> saveSchoolImgAndReturnUrl(String type, MultipartFile file) {
        log.info("save image enter...");
        if (file == null || file.isEmpty()) {
            throw new FileHandler(FileErrorStatus.FILE_EMPTY);
        }

        String path = FileTargetType.SCHOOL.getFilePath() + "img";
        FileTargetType fileType = resolveFileType(type);

        FileAttachment attachment = saveFileModel(path, file, fileType);
        uploadObject(path, file, attachment.getStoredName());

        URL url = getViewUrl(attachment);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.toString()))
                .build();
    }

    public ResponseEntity<Void> viewDefaultImg(String type) {
        String fileName = null;
        if (type.equalsIgnoreCase("agency")) fileName = "agency_default.png";
        else if (type.equalsIgnoreCase("school")) fileName = "school_default.jpg";

        if (fileName == null) {
            throw new FileHandler(FileErrorStatus.FILE_TARGET_INVALID);
        }

        URL url = getViewUrl(fileName);
        log.info("url log:{}", url);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url.toString()))
                .build();
    }

    private FileTargetType resolveFileType(String type) {
        try {
            return FileTargetType.fromType(type);
        } catch (Exception e) {
            throw new FileHandler(FileErrorStatus.FILE_TARGET_INVALID);
        }
    }

    private FileAttachment findAttachment(String fileId, String type) {
        FileTargetType fileType = resolveFileType(type);
        return loadFileAttachment(fileId, fileType);
    }

    private void uploadObject(String path, MultipartFile file, String storedName) {
        String key = String.format("%s/%s", path, storedName);

        try (InputStream in = file.getInputStream()) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getSize());
            if (file.getContentType() != null) {
                meta.setContentType(file.getContentType());
            }
            meta.setHeader("x-oss-server-side-encryption", "AES256");

            oss.putObject(bucket, key, in, meta);
        } catch (IOException e) {
            log.error("File upload IO error", e);
            throw new FileHandler(FileErrorStatus.FILE_UPLOAD_ERROR);
        } catch (Exception e) {
            log.error("File save error", e);
            throw new FileHandler(FileErrorStatus.FILE_SAVE_ERROR);
        }
    }
}