package com.example.login.controller;

import com.example.login.dto.response.ApiResponse;
import com.example.login.entity.CourseMaterial;
import com.example.login.exception.BusinessException;
import com.example.login.repository.CourseMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 课程资料管理控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MaterialController {

    private final CourseMaterialRepository courseMaterialRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadPath;

    @GetMapping("/course/{courseId}/material/list")
    public ResponseEntity<ApiResponse<List<CourseMaterial>>> getMaterialList(
            @PathVariable Long courseId) {
        List<CourseMaterial> list = courseMaterialRepository.findByCourseIdAndIsDeleted(courseId, 0);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/course/{courseId}/material")
    public ResponseEntity<ApiResponse<CourseMaterial>> uploadMaterial(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
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
        String fileType = file.getContentType();

        CourseMaterial material = CourseMaterial.builder()
                .courseId(courseId)
                .name(originalFilename)
                .fileUrl(fileUrl)
                .fileType(fileType)
                .fileSize(file.getSize())
                .creatorId(getCurrentUserId())
                .isDeleted(0)
                .createdTime(new Date())
                .build();
        CourseMaterial saved = courseMaterialRepository.save(material);
        return ResponseEntity.ok(ApiResponse.success("上传成功", saved));
    }

    @DeleteMapping("/course/{courseId}/material/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(
            @PathVariable Long courseId,
            @PathVariable Long id) {
        CourseMaterial material = courseMaterialRepository.findById(id)
                .filter(m -> m.getIsDeleted() == 0)
                .orElseThrow(() -> new BusinessException(404, "资料不存在"));
        material.setIsDeleted(1);
        courseMaterialRepository.save(material);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
