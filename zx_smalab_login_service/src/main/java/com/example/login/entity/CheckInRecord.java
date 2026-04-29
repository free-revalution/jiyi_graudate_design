package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 签到记录实体类
 * 对应数据库表 check_in_record
 */
@Entity
@Table(name = "check_in_record")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "check_in_id", nullable = false)
    private Long checkInId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "student_id", length = 50)
    private String studentId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "avatar", length = 500)
    private String avatar;

    @Column(name = "sign_time")
    private Date signTime;

    @Column(name = "sign_out_time")
    private Date signOutTime;

    @Column(name = "is_late")
    private Integer isLate;

    @Column(name = "is_absent")
    private Integer isAbsent;

    @Column(name = "personal_leave")
    private Integer personalLeave;

    @Column(name = "sick_leave")
    private Integer sickLeave;

    @Column(name = "early_leave")
    private Integer earlyLeave;

    @Column(name = "official_leave")
    private Integer officialLeave;

    @Column(name = "attendance_rate", precision = 5, scale = 2)
    private BigDecimal attendanceRate;

    @Column(name = "created_time")
    private Date createdTime;

}
