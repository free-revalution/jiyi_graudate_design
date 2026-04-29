package com.example.login.service;

import com.example.login.entity.CheckIn;

import java.util.List;
import java.util.Map;

/**
 * 签到服务接口
 */
public interface CheckInService {

    List<CheckIn> getCheckInList(Long courseId);

    CheckIn createCheckIn(CheckIn checkIn);

    CheckIn getCheckInById(Long id);

    void startCheckIn(Long id);

    void endCheckIn(Long id);

    Map<String, Object> getCheckInRecords(Long checkInId);

    Map<String, Object> getCheckInStatistics(Long courseId, Long checkInId);
}
