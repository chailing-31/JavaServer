package cn.edu.sdu.java.server.controllers;

/*
import cn.edu.sdu.java.server.models.Attendance;
import cn.edu.sdu.java.server.services.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final ValidationAutoConfiguration validationAutoConfiguration;

    @Autowired
    public AttendanceController(AttendanceService attendanceService, ValidationAutoConfiguration validationAutoConfiguration) {
        this.attendanceService = attendanceService;
        this.validationAutoConfiguration = validationAutoConfiguration;
    }



    @PostMapping
    public ResponseEntity<Attendance> recordAttendance(@RequestBody Attendance attendance) {
        return ResponseEntity.ok(attendanceService.recordAttendance(attendance));
    }



    @GetMapping("/student/{personId}")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@Valid @PathVariable Integer personId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(personId));
    }



    @GetMapping("/class")
    public ResponseEntity<List<Attendance>> getClassAttendance(
            @Valid @RequestParam String className,
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getClassAttendance(className, date));
    }



    @GetMapping("/statistics/{personId}")
    public ResponseEntity<Map<Attendance.AttendanceStatus, Long>> getAttendanceStatistics(
            @Valid @PathVariable Integer personId,
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceStatistics(personId, startDate, endDate));
    }



    @PutMapping("/{attendanceId}")
    public ResponseEntity<Attendance> updateAttendance(
            @Valid @PathVariable Integer attendanceId,
            @Valid @RequestParam Attendance.AttendanceStatus status,
            @Valid @RequestParam(required = false) String remark) {
        return ResponseEntity.ok(attendanceService.updateAttendance(attendanceId, status, remark));
    }



    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@Valid @PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.noContent().build();
    }
}*/



import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * 记录考勤信息
     */
    @PostMapping("/record")
    public DataResponse recordAttendance(@RequestBody Map<String, Object> form) {
        return attendanceService.recordAttendance(form);
    }

    /**
     * 获取学生所有考勤记录
     */
    @GetMapping("/student/{personId}")
    public DataResponse getStudentAttendance(@PathVariable Integer personId) {
        return attendanceService.getStudentAttendance(personId);
    }

    /**
     * 获取班级某天的考勤记录
     */
    @GetMapping("/class")
    public DataResponse getClassAttendance(
            @RequestParam String className,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return attendanceService.getClassAttendance(className, localDate);
    }

    /**
     * 获取学生考勤统计
     */
    @GetMapping("/statistics")
    public DataResponse getAttendanceStatistics(
            @RequestParam Integer personId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return attendanceService.getAttendanceStatistics(personId, start, end);
    }

    /**
     * 更新考勤记录
     */
    @PutMapping("/update")
    public DataResponse updateAttendance(@RequestBody Map<String, Object> form) {
        return attendanceService.updateAttendance(form);
    }

    /**
     * 删除考勤记录
     */
    @DeleteMapping("/delete/{attendanceId}")
    public DataResponse deleteAttendance(@PathVariable Integer attendanceId) {
        return attendanceService.deleteAttendance(attendanceId);
    }

    /**
     * 批量导入考勤记录
     */
    @PostMapping("/batch")
    public DataResponse batchRecordAttendance(@RequestBody List<Map<String, Object>> attendanceList) {
        return attendanceService.batchRecordAttendance(attendanceList);
    }

    /**
     * 获取考勤状态选项列表
     */
    @GetMapping("/status-options")
    public DataResponse getAttendanceStatusOptions() {
        return attendanceService.getAttendanceStatusOptionList();
    }



}
