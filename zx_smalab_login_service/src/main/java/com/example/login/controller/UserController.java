package com.example.login.controller;

import com.example.login.dto.response.ApiResponse;
import com.example.login.entity.UserInfo;
import com.example.login.exception.BusinessException;
import com.example.login.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserInfoRepository userInfoRepository;

    @GetMapping("/user/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo() {
        Long userId = getCurrentUserId();
        UserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(404, "用户信息不存在"));

        Map<String, Object> data = new HashMap<>();
        data.put("avatar", userInfo.getUserPhoto());
        data.put("username", userInfo.getUserName());
        data.put("gender", userInfo.getSex());
        data.put("nickname", userInfo.getUserNickname());
        data.put("motto", userInfo.getUserMotto());
        data.put("school", userInfo.getSchool());
        data.put("following", 0);
        data.put("followers", 0);
        data.put("topics", 0);
        data.put("likes", 0);
        data.put("studyTime", 0);

        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PutMapping("/user/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUserInfo(
            @RequestBody Map<String, String> request) {
        Long userId = getCurrentUserId();
        UserInfo userInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(404, "用户信息不存在"));

        if (request.containsKey("nickname")) {
            userInfo.setUserNickname(request.get("nickname"));
        }
        if (request.containsKey("photo")) {
            userInfo.setUserPhoto(request.get("photo"));
        }
        if (request.containsKey("motto")) {
            userInfo.setUserMotto(request.get("motto"));
        }
        userInfo.setModifiedTime(new Date());
        userInfoRepository.save(userInfo);

        Map<String, Object> data = new HashMap<>();
        data.put("avatar", userInfo.getUserPhoto());
        data.put("username", userInfo.getUserName());
        data.put("gender", userInfo.getSex());
        data.put("nickname", userInfo.getUserNickname());
        data.put("motto", userInfo.getUserMotto());

        return ResponseEntity.ok(ApiResponse.success("更新成功", data));
    }

    protected Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(auth.getName());
    }
}
