package com.example.login.service;

import com.example.login.dto.request.LoginRequest;
import com.example.login.dto.request.RefreshTokenRequest;
import com.example.login.dto.request.RegisterRequest;
import com.example.login.dto.response.LoginResponse;

/**
 * 登录服务接口
 */
public interface LoginService {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @param ip           客户端IP
     * @return 登录响应
     */
    LoginResponse login(LoginRequest loginRequest, String ip);

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     */
    void register(RegisterRequest registerRequest);

    /**
     * 刷新令牌
     *
     * @param refreshTokenRequest 刷新令牌请求
     * @return 登录响应
     */
    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * 退出登录
     *
     * @param accessToken 访问令牌
     */
    void logout(String accessToken);
}