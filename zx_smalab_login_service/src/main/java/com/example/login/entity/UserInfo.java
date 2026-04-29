package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户信息实体类
 * 对应数据库表 user_info
 */
@Entity
@Table(name = "user_info")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_type", length = 20)
    private String userType;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "user_nickname", length = 100)
    private String userNickname;

    @Column(name = "user_photo", length = 500)
    private String userPhoto;

    @Column(name = "user_motto", length = 500)
    private String userMotto;

    @Column(name = "sex", length = 10)
    private String sex;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "age")
    private Integer age;

    @Column(name = "tel", length = 50)
    private String tel;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "school", length = 100)
    private String school;

    @Column(name = "role_type", length = 50)
    private String roleType;

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
