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
@RequestMapping("/api/admin")
public class FileUploadController {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> upload(@RequestPart("file") MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }

        String original = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        String ext = StringUtils.getFilenameExtension(original);
        ext = (ext == null) ? "" : ("." + ext.toLowerCase());

        // 简单白名单：只允许图片
        List<String> allow = Arrays.asList(".jpg", ".jpeg", ".png", ".webp", ".gif", ".heic", ".jfif");
        if (!allow.contains(ext)) {
            throw new IllegalArgumentException("Only image files are allowed: jpg/jpeg/png/webp");
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = "building_" + ts + "_" + UUID.randomUUID().toString().replace("-", "") + ext;

        Path target = dir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // 返回前端可用的访问路径
        String url = "/uploads/" + filename;

        Map<String, Object> resp = new HashMap<>();
        resp.put("url", url);
        resp.put("filename", filename);
        return resp;
    }
}