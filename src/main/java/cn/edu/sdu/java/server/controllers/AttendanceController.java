package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.models.Attendance;
import cn.edu.sdu.java.server.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<Attendance> recordAttendance(@RequestBody Attendance attendance) {
        return ResponseEntity.ok(attendanceService.recordAttendance(attendance));
    }

    @GetMapping("/student/{personId}")
    public ResponseEntity<List<Attendance>> getStudentAttendance(@PathVariable Integer personId) {
        return ResponseEntity.ok(attendanceService.getStudentAttendance(personId));
    }

    @GetMapping("/class")
    public ResponseEntity<List<Attendance>> getClassAttendance(
            @RequestParam String className,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getClassAttendance(className, date));
    }

    @GetMapping("/statistics/{personId}")
    public ResponseEntity<Map<Attendance.AttendanceStatus, Long>> getAttendanceStatistics(
            @PathVariable Integer personId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceStatistics(personId, startDate, endDate));
    }

    @PutMapping("/{attendanceId}")
    public ResponseEntity<Attendance> updateAttendance(
            @PathVariable Integer attendanceId,
            @RequestParam Attendance.AttendanceStatus status,
            @RequestParam(required = false) String remark) {
        return ResponseEntity.ok(attendanceService.updateAttendance(attendanceId, status, remark));
    }

    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        return ResponseEntity.noContent().build();
    }
}
