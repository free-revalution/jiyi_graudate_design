package com.example.login.controller;

import com.example.login.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequiredArgsConstructor
public class FileController {

    @Value("${file.upload-dir:./uploads}")
    private String uploadPath;

    @PostMapping("/user_permiss/minio/upload-system-user-file")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error("文件不能为空"));
        }

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String filename = UUID.randomUUID() + ext;

        Path uploadDir = Paths.get(uploadPath);
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, file.getBytes());

        String fileUrl = "/uploads/" + filename;
        Map<String, String> result = new HashMap<>();
        result.put("fileUrl", fileUrl);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
