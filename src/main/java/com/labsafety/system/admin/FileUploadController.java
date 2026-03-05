package com.labsafety.system.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class FileUploadController {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    // ===============================
    // ADMIN 上传
    // ===============================
    @PostMapping(value = "/api/admin/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadAdmin(@RequestPart("file") MultipartFile file) throws Exception {
        return saveFile(file);
    }

    // ===============================
    // STUDENT 上传
    // ===============================
    @PostMapping(value = "/api/student/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadStudent(@RequestPart("file") MultipartFile file) throws Exception {
        return saveFile(file);
    }

    // ===============================
    // 统一文件保存逻辑
    // ===============================
    private Map<String, Object> saveFile(MultipartFile file) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        String ext = StringUtils.getFilenameExtension(original);
        ext = (ext == null) ? "" : ("." + ext.toLowerCase());

        List<String> allow = Arrays.asList(
                ".jpg", ".jpeg", ".png", ".webp", ".gif", ".heic", ".jfif"
        );

        if (!allow.contains(ext)) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String ts = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String filename = "visitor_" + ts + "_" +
                UUID.randomUUID().toString().replace("-", "") + ext;

        Path target = dir.resolve(filename);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String url = "/uploads/" + filename;

        Map<String, Object> resp = new HashMap<>();
        resp.put("url", url);
        resp.put("filename", filename);

        return resp;
    }
}