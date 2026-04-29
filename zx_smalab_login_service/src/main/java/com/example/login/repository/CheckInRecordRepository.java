package com.example.login.repository;

import com.example.login.entity.CheckInRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 签到记录数据访问层
 */
@Repository
public interface CheckInRecordRepository extends JpaRepository<CheckInRecord, Long> {

    List<CheckInRecord> findByCheckInId(Long checkInId);
}
