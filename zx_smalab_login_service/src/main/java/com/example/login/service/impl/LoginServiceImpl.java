package com.example.login.service.impl;

import com.example.login.config.JwtConfig;
import com.example.login.config.JwtTokenProvider;
import com.example.login.dto.request.LoginRequest;
import com.example.login.dto.request.RefreshTokenRequest;
import com.example.login.dto.request.RegisterRequest;
import com.example.login.dto.response.LoginResponse;
import com.example.login.entity.UserAuthorize;
import com.example.login.exception.BusinessException;
import com.example.login.repository.UserAuthorizeRepository;
import com.example.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * 登录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserAuthorizeRepository userAuthorizeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest, String ip) {
        log.info("用户登录请求: phone={}, school={}, userType={}",
                loginRequest.getPhone(), loginRequest.getSchool(), loginRequest.getUserType());

        String identityType = determineIdentityType(loginRequest.getUserType());

        UserAuthorize userAuthorize = userAuthorizeRepository
                .findByIdentifierAndIdentityType(loginRequest.getPhone(), identityType)
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if ("2".equals(userAuthorize.getUserStatus())) {
            throw new BusinessException(403, "用户已被禁用");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), userAuthorize.getCredential())) {
            log.warn("用户登录失败: 密码不匹配, phone={}", loginRequest.getPhone());
            throw new BusinessException(401, "用户名或密码错误");
        }

        String accessToken = jwtTokenProvider.generateToken(userAuthorize.getUserId(), userAuthorize.getIdentifier());
        String refreshToken = generateRefreshToken();
        long expiresIn = jwtConfig.getExpiration();

        userAuthorize.setAccessToken(accessToken);
        userAuthorize.setRefreshToken(refreshToken);
        userAuthorize.setExpiresIn(expiresIn);
        userAuthorize.setLoginTime(new Date());
        userAuthorize.setLoginIp(ip);
        userAuthorize.setLoginStatus("1");
        userAuthorize.setModifiedTime(new Date());
        userAuthorizeRepository.save(userAuthorize);

        log.info("用户登录成功: userId={}, identifier={}", userAuthorize.getUserId(), userAuthorize.getIdentifier());

        return LoginResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .token(refreshToken)
                .expires_in(expiresIn)
                .user_id(userAuthorize.getUserId())
                .user_status(userAuthorize.getUserStatus())
                .identity_type(userAuthorize.getIdentityType())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        log.info("刷新令牌请求");

        UserAuthorize userAuthorize = userAuthorizeRepository
                .findByRefreshToken(refreshTokenRequest.getRefreshToken())
                .orElseThrow(() -> new BusinessException(401, "无效的刷新令牌"));

        String accessToken = jwtTokenProvider.generateToken(userAuthorize.getUserId(), userAuthorize.getIdentifier());
        String refreshToken = generateRefreshToken();
        long expiresIn = jwtConfig.getExpiration();

        userAuthorize.setAccessToken(accessToken);
        userAuthorize.setRefreshToken(refreshToken);
        userAuthorize.setExpiresIn(expiresIn);
        userAuthorize.setModifiedTime(new Date());
        userAuthorizeRepository.save(userAuthorize);

        log.info("令牌刷新成功: userId={}", userAuthorize.getUserId());

        return LoginResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .expires_in(expiresIn)
                .user_id(userAuthorize.getUserId())
                .user_status(userAuthorize.getUserStatus())
                .identity_type(userAuthorize.getIdentityType())
                .build();
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        log.info("用户退出登录");

        userAuthorizeRepository.findByAccessToken(accessToken)
                .ifPresent(userAuthorize -> {
                    userAuthorize.setAccessToken(null);
                    userAuthorize.setRefreshToken(null);
                    userAuthorize.setExpiresIn(null);
                    userAuthorize.setLoginStatus("2");
                    userAuthorize.setModifiedTime(new Date());
                    userAuthorizeRepository.save(userAuthorize);
                    log.info("用户退出成功: userId={}", userAuthorize.getUserId());
                });
    }

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        log.info("用户注册请求: username={}, phone={}, userType={}",
                registerRequest.getUsername(), registerRequest.getPhone(), registerRequest.getUserType());

        String identityType = determineIdentityType(registerRequest.getUserType());

        if (userAuthorizeRepository.existsByIdentifierAndIdentityType(registerRequest.getUsername(), identityType)) {
            throw new BusinessException(400, "该账号已存在");
        }

        if (userAuthorizeRepository.existsByIdentifierAndIdentityType(registerRequest.getPhone(), "phone")) {
            throw new BusinessException(400, "该手机号已被注册");
        }

        long maxUserId = Optional.ofNullable(userAuthorizeRepository.findMaxUserId()).orElse(0L) + 1;

        UserAuthorize userAuthorize = UserAuthorize.builder()
                .userId(maxUserId)
                .identityType(identityType)
                .identifier(registerRequest.getUsername())
                .credential(passwordEncoder.encode(registerRequest.getPassword()))
                .userStatus("1")
                .createdTime(new Date())
                .modifiedTime(new Date())
                .loginStatus("2")
                .otherInfoOne(registerRequest.getSchool())
                .otherInfoTwo(registerRequest.getPhone())
                .build();

        userAuthorizeRepository.save(userAuthorize);

        log.info("用户注册成功: userId={}, identifier={}", maxUserId, registerRequest.getUsername());
    }

    private String determineIdentityType(String userType) {
        if ("teacher".equals(userType)) {
            return "teacher_id";
        } else if ("student".equals(userType)) {
            return "student_id";
        }
        return "phone";
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
