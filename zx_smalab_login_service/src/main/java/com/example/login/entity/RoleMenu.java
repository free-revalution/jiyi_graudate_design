package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 角色菜单实体类
 * 对应数据库表 vanx_platf_role_menu
 */
@Entity
@Table(name = "vanx_platf_role_menu")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "menu_id", length = 100)
    private String menuId;

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
