package com.example.login.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 学校标识
     */
    private String school;

    /**
     * 用户类型：student/teacher
     */
    private String userType;

    /**
 * 手机号
 */
@NotBlank(message = "手机号不能为空")
private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}