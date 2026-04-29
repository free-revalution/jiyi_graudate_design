package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户授权实体类
 * 对应数据库表 user_authorize
 */
@Entity
@Table(name = "user_authorize")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthorize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authorize_id")
    private Long authorizeId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_status", length = 5)
    private String userStatus;

    @Column(name = "identity_type", length = 50)
    private String identityType;

    @Column(name = "identifier", length = 500)
    private String identifier;

    @Column(name = "credential", length = 200)
    private String credential;

    @Column(name = "login_time")
    private Date loginTime;

    @Column(name = "login_ip", length = 20)
    private String loginIp;

    @Column(name = "login_note", length = 100)
    private String loginNote;

    @Column(name = "login_status", length = 5)
    private String loginStatus;

    @Column(name = "access_token", length = 500)
    private String accessToken;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "expires_in")
    private Long expiresIn;

    @Column(name = "oauth_info", columnDefinition = "TEXT")
    private String oauthInfo;

    @Column(name = "editor_id")
    private Long editorId;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "modified_time")
    private Date modifiedTime;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "other_info_one", length = 50)
    private String otherInfoOne;

    @Column(name = "other_info_two", length = 50)
    private String otherInfoTwo;

    @Column(name = "remark", length = 50)
    private String remark;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
