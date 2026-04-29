package com.example.login.service.impl;

import com.example.login.entity.CheckIn;
import com.example.login.entity.CheckInRecord;
import com.example.login.entity.CourseStudent;
import com.example.login.exception.BusinessException;
import com.example.login.repository.CheckInRecordRepository;
import com.example.login.repository.CheckInRepository;
import com.example.login.repository.CourseStudentRepository;
import com.example.login.service.CheckInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 签到服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepository checkInRepository;
    private final CheckInRecordRepository checkInRecordRepository;
    private final CourseStudentRepository courseStudentRepository;

    @Override
    public List<CheckIn> getCheckInList(Long courseId) {
        return checkInRepository.findByCourseIdAndIsDeletedOrderByCreatedTimeDesc(courseId, 0);
    }

    @Override
    @Transactional
    public CheckIn createCheckIn(CheckIn checkIn) {
        checkIn.setStatus("not_started");
        checkIn.setTotalCount(0);
        checkIn.setAbsentCount(0);
        checkIn.setIsDeleted(0);
        checkIn.setCreatedTime(new Date());
        return checkInRepository.save(checkIn);
    }

    @Override
    public CheckIn getCheckInById(Long id) {
        return checkInRepository.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BusinessException(404, "签到不存在"));
    }

    @Override
    @Transactional
    public void startCheckIn(Long id) {
        CheckIn checkIn = getCheckInById(id);
        checkIn.setStatus("ongoing");
        checkInRepository.save(checkIn);
    }

    @Override
    @Transactional
    public void endCheckIn(Long id) {
        CheckIn checkIn = getCheckInById(id);
        checkIn.setStatus("finished");

        // Count records
        List<CheckInRecord> records = checkInRecordRepository.findByCheckInId(id);
        long signedCount = records.stream()
                .filter(r -> r.getSignTime() != null)
                .count();
        checkIn.setTotalCount((int) signedCount);

        // Get total students in the class
        long totalStudents = courseStudentRepository.countByClassIdAndIsDeleted(checkIn.getClassId(), 0);
        checkIn.setAbsentCount((int) (totalStudents - signedCount));

        checkInRepository.save(checkIn);
    }

    @Override
    public Map<String, Object> getCheckInRecords(Long checkInId) {
        getCheckInById(checkInId); // verify existence

        List<CheckInRecord> records = checkInRecordRepository.findByCheckInId(checkInId);

        List<CheckInRecord> signed = records.stream()
                .filter(r -> r.getSignTime() != null)
                .collect(Collectors.toList());

        List<CheckInRecord> unsigned = records.stream()
                .filter(r -> r.getSignTime() == null)
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("signed", signed);
        result.put("unsigned", unsigned);
        return result;
    }

    @Override
    public Map<String, Object> getCheckInStatistics(Long courseId, Long checkInId) {
        CheckIn checkIn = getCheckInById(checkInId);

        // Get all students in the course
        List<CourseStudent> students = courseStudentRepository.findByCourseIdAndIsDeleted(courseId, 0);

        // Get all records for this check-in
        List<CheckInRecord> records = checkInRecordRepository.findByCheckInId(checkInId);

        // Build member list with attendance statistics
        List<Map<String, Object>> memberList = new ArrayList<>();
        for (CourseStudent student : students) {
            Map<String, Object> member = new LinkedHashMap<>();
            member.put("id", student.getId());
            member.put("name", student.getName());
            member.put("studentId", student.getStudentId());
            member.put("avatar", "");

            // Find record for this student
            Optional<CheckInRecord> recordOpt = records.stream()
                    .filter(r -> r.getUserId().equals(student.getUserId()))
                    .findFirst();

            if (recordOpt.isPresent()) {
                CheckInRecord record = recordOpt.get();
                member.put("absent", record.getIsAbsent() != null ? record.getIsAbsent() : 0);
                member.put("personalLeave", record.getPersonalLeave() != null ? record.getPersonalLeave() : 0);
                member.put("sickLeave", record.getSickLeave() != null ? record.getSickLeave() : 0);
                member.put("late", record.getIsLate() != null ? record.getIsLate() : 0);
                member.put("earlyLeave", record.getEarlyLeave() != null ? record.getEarlyLeave() : 0);
                member.put("officialLeave", record.getOfficialLeave() != null ? record.getOfficialLeave() : 0);
                member.put("attendanceRate", record.getAttendanceRate() != null ? record.getAttendanceRate() : BigDecimal.ZERO);
            } else {
                member.put("absent", 1);
                member.put("personalLeave", 0);
                member.put("sickLeave", 0);
                member.put("late", 0);
                member.put("earlyLeave", 0);
                member.put("officialLeave", 0);
                member.put("attendanceRate", BigDecimal.ZERO);
            }
            memberList.add(member);
        }

        // Calculate summary
        int attendanceCount = (int) records.stream()
                .filter(r -> r.getSignTime() != null)
                .count();
        int absentCount = students.size() - attendanceCount;
        BigDecimal avgRate = BigDecimal.ZERO;
        if (!memberList.isEmpty()) {
            BigDecimal totalRate = memberList.stream()
                    .map(m -> (BigDecimal) m.get("attendanceRate"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            avgRate = totalRate.divide(BigDecimal.valueOf(memberList.size()), 1, RoundingMode.HALF_UP);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("avgAttendanceRate", avgRate);
        summary.put("attendanceCount", attendanceCount);
        summary.put("absentCount", absentCount);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("summary", summary);
        result.put("memberList", memberList);
        return result;
    }
}
