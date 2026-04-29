package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户角色实体类
 * 对应数据库表 vanx_platf_user_roles
 */
@Entity
@Table(name = "vanx_platf_user_roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long userRoleId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "user_role_status", length = 10)
    private String userRoleStatus;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "platform_id")
    private Long platformId;

    @Column(name = "platform_user_id")
    private Long platformUserId;

    @Column(name = "pass_time")
    private Date passTime;

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
