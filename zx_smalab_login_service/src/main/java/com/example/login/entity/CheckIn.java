package com.example.login.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 签到实体类
 * 对应数据库表 check_in
 */
@Entity
@Table(name = "check_in")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "date")
    private Date date;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "enable_location")
    private Integer enableLocation;

    @Column(name = "location_range")
    private Integer locationRange;

    @Column(name = "auto_refresh_qrcode")
    private Integer autoRefreshQrcode;

    @Column(name = "qrcode_refresh_rate")
    private Integer qrcodeRefreshRate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "manual_end")
    private Integer manualEnd;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "enable_sign_out")
    private Integer enableSignOut;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "absent_count")
    private Integer absentCount;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "is_deleted")
    private Integer isDeleted;

}
