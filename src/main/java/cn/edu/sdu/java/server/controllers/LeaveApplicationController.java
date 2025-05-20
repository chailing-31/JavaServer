package cn.edu.sdu.java.server.controllers;


import cn.edu.sdu.java.server.models.LeaveApplication;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.services.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/leave-applications")
public class LeaveApplicationController {
    private final LeaveApplicationService leaveApplicationService;
    private final StudentRepository studentRepository;

    @Autowired
    public LeaveApplicationController(LeaveApplicationService leaveApplicationService,
                                      StudentRepository studentRepository) {
        this.leaveApplicationService = leaveApplicationService;
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public ResponseEntity<LeaveApplication> submitLeaveApplication(
            @RequestBody LeaveApplication leaveApplication) {

        // 验证学生是否存在
        studentRepository.findById(leaveApplication.getStudent().getPersonId())
                .orElseThrow(() -> new RuntimeException("学生不存在"));

        return ResponseEntity.ok(leaveApplicationService.submitLeaveApplication(leaveApplication));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveApplication> approveLeaveApplication(
            @PathVariable Integer id,
            @RequestParam Integer teacherId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(
                leaveApplicationService.approveLeaveApplication(id, teacherId, comment));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveApplication> rejectLeaveApplication(
            @PathVariable Integer id,
            @RequestParam Integer teacherId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(
                leaveApplicationService.rejectLeaveApplication(id, teacherId, comment));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelLeaveApplication(@PathVariable Integer id) {
        leaveApplicationService.cancelLeaveApplication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<LeaveApplication>> getStudentLeaveApplications(
            @PathVariable Integer studentId) {
        return ResponseEntity.ok(
                leaveApplicationService.getStudentLeaveApplications(studentId));
    }

    @GetMapping("/class/{className}/pending")
    public ResponseEntity<List<LeaveApplication>> getPendingApplicationsByClass(
            @PathVariable String className) {
        return ResponseEntity.ok(
                leaveApplicationService.getPendingApplicationsByClass(className));
    }

    @GetMapping("/class/{className}/approved")
    public ResponseEntity<List<LeaveApplication>> getApprovedApplicationsByClass(
            @PathVariable String className,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(
                leaveApplicationService.getApprovedApplicationsByClass(className, date));
    }

    @GetMapping("/conflict-check")
    public ResponseEntity<Boolean> checkTimeConflict(
            @RequestParam Integer studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(
                leaveApplicationService.hasOverlappingLeaves(studentId, startTime, endTime));
    }
}
