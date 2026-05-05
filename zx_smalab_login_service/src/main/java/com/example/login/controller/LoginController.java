package com.example.login.controller;

import com.example.login.dto.request.LoginRequest;
import com.example.login.dto.request.RefreshTokenRequest;
import com.example.login.dto.request.RegisterRequest;
import com.example.login.dto.response.ApiResponse;
import com.example.login.dto.response.LoginResponse;
import com.example.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/user_permiss/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * 用户登录接口
     *
     * @param loginRequest 登录请求
     * @param request      HTTP请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        log.info("【登录请求】phone={}, userType={}, ip={}", loginRequest.getPhone(), loginRequest.getUserType(), getClientIp(request));
        String clientIp = getClientIp(request);
        LoginResponse response = loginService.login(loginRequest, clientIp);
        log.info("【登录成功】userId={}, token已生成", response.getUser_id());
        return ResponseEntity.ok(ApiResponse.success("登录成功", response));
    }

    /**
     * 刷新令牌接口
     *
     * @param refreshTokenRequest 刷新令牌请求
     * @return 登录响应
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        
        LoginResponse response = loginService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success("令牌刷新成功", response));
    }

    /**
     * 退出登录接口
     *
     * @param request HTTP请求
     * @return 响应
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = extractToken(request);
        loginService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("退出成功", null));
    }

    /**
     * 用户注册接口
     *
     * @param registerRequest 注册请求
     * @return 响应
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest registerRequest) {
        
        loginService.register(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("注册成功", null));
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 从请求头提取Token
     */
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}