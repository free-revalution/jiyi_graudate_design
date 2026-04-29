package com.example.login.repository;

import com.example.login.entity.UserAuthorize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户授权数据访问层
 */
@Repository
public interface UserAuthorizeRepository extends JpaRepository<UserAuthorize, Long> {

    /**
     * 根据标识和身份类型查找用户授权信息
     *
     * @param identifier   标识(手机号/邮箱/学号/工号)
     * @param identityType 身份类型
     * @return 用户授权信息
     */
    Optional<UserAuthorize> findByIdentifierAndIdentityType(String identifier, String identityType);

    /**
     * 根据用户ID查找用户授权信息
     *
     * @param userId 用户ID
     * @return 用户授权信息
     */
    Optional<UserAuthorize> findByUserId(Long userId);

    /**
     * 根据AccessToken查找用户授权信息
     *
     * @param accessToken 访问令牌
     * @return 用户授权信息
     */
    Optional<UserAuthorize> findByAccessToken(String accessToken);

    /**
     * 根据RefreshToken查找用户授权信息
     *
     * @param refreshToken 刷新令牌
     * @return 用户授权信息
     */
    Optional<UserAuthorize> findByRefreshToken(String refreshToken);

    /**
     * 检查标识是否存在
     *
     * @param identifier   标识
     * @param identityType 身份类型
     * @return 是否存在
     */
    boolean existsByIdentifierAndIdentityType(String identifier, String identityType);

    /**
     * 获取最大的用户ID
     *
     * @return 最大用户ID
     */
    @Query("SELECT MAX(u.userId) FROM UserAuthorize u")
    Long findMaxUserId();
}