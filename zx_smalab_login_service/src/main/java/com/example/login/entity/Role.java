package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 角色实体类
 * 对应数据库表 vanx_platf_roles
 */
@Entity
@Table(name = "vanx_platf_roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_key", length = 100)
    private String roleKey;

    @Column(name = "role_name", length = 100)
    private String roleName;

    @Column(name = "frontend_show_name", length = 100)
    private String frontendShowName;

    @Column(name = "role_categ_name", length = 100)
    private String roleCategName;

    @Column(name = "role_sort")
    private Integer roleSort;

    @Column(name = "role_status", length = 10)
    private String roleStatus;

    @Column(name = "add_source", length = 20)
    private String addSource;

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
