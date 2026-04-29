package com.example.login.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * 访问令牌
     */
    private String access_token;

    /**
     * 刷新令牌
     */
    private String refresh_token;
    private String token;

    /**
     * 过期时间（秒）
     */
    private Long expires_in;

    /**
     * 用户ID
     */
    private Long user_id;

    /**
     * 用户状态
     */
    private String user_status;

    /**
     * 身份类型
     */
    private String identity_type;
}
