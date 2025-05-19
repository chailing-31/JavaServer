package cn.edu.sdu.java.server.controllers;

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

    /*
     * 增加一条考勤记录
     * 提供attendance类信息
     * 返回的响应体是attendance类信息
     */
    @PostMapping
    public ResponseEntity<Attendance> recordAttendance(@RequestBody Attendance attendance) {
        return ResponseEntity.ok(attendanceService.recordAttendance(attendance));
    }

    /*
    * 根据学生ID查询该学生所有的考勤记录
    * 提供学生ID
    * 返回多条考勤记录
    */
    @GetMapping("/student/{personId}")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@Valid @PathVariable Integer personId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(personId));
    }

    /*
    * 根据班级名和考勤时间查询该班级所有符合条件的考勤记录
    * 提供班级名、考勤时间
    * 返回多条考勤记录
    */
    @GetMapping("/class")
    public ResponseEntity<List<Attendance>> getClassAttendance(
            @Valid @RequestParam String className,
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getClassAttendance(className, date));
    }

    /*
    * 根据学生ID、开始时间、结束时间查询该学生在该时间段的所有考勤记录
    * 提供学生ID、开始时间、结束时间
    * 返回考勤状态以及对应的次数
    */
    @GetMapping("/statistics/{personId}")
    public ResponseEntity<Map<Attendance.AttendanceStatus, Long>> getAttendanceStatistics(
            @Valid @PathVariable Integer personId,
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Valid @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceStatistics(personId, startDate, endDate));
    }

    /*
    * 根据已有考勤记录ID更新该条考勤记录的状态以及备注
    * 提供考勤记录ID、更改后的状态以及备注
    * 返回一条完整的更改后的考勤记录
    */
    @PutMapping("/{attendanceId}")
    public ResponseEntity<Attendance> updateAttendance(
            @Valid @PathVariable Integer attendanceId,
            @Valid @RequestParam Attendance.AttendanceStatus status,
            @Valid @RequestParam(required = false) String remark) {
        return ResponseEntity.ok(attendanceService.updateAttendance(attendanceId, status, remark));
    }

    /*
    * 删除一条考勤记录
    * 提供想要删除的考勤记录的ID
    * 返回 HTTP 204 No Content 响应
    */
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@Valid @PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.noContent().build();
    }
}
