package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.LeaveApplication;
import cn.edu.sdu.java.server.models.Teacher;
import cn.edu.sdu.java.server.repositorys.LeaveApplicationRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
@Transactional
public class LeaveApplicationService {
    private final LeaveApplicationRepository leaveApplicationRepository;
    @Getter
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository,
                                   StudentRepository studentRepository,
                                   TeacherRepository teacherRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    //提交请假申请

    public LeaveApplication submitLeaveApplication(LeaveApplication leaveApplication) {
        // 检查时间冲突
        if (hasOverlappingLeaves(
                leaveApplication.getStudent().getPersonId(),
                leaveApplication.getStartTime(),
                leaveApplication.getEndTime())) {
            throw new IllegalStateException("该时间段已有请假申请");
        }

        leaveApplication.setStatus(LeaveApplication.ApprovalStatus.PENDING);
        return leaveApplicationRepository.save(leaveApplication);
    }

    /**
     * 批准请假申请
     */


    public LeaveApplication approveLeaveApplication(Integer applicationId, Integer teacherId, String comment) {
        LeaveApplication application = getValidApplication(applicationId);
        Teacher teacher = getTeacher(teacherId);

        application.setStatus(LeaveApplication.ApprovalStatus.APPROVED);
        application.setApprover(teacher);
        application.setApprovalComment(comment);
        application.setApprovalTime(LocalDateTime.now());

        return leaveApplicationRepository.save(application);
    }



    /**
     * 拒绝请假申请
     */


    public LeaveApplication rejectLeaveApplication(Integer applicationId, Integer teacherId, String comment) {
        LeaveApplication application = getValidApplication(applicationId);
        Teacher teacher = getTeacher(teacherId);

        application.setStatus(LeaveApplication.ApprovalStatus.REJECTED);
        application.setApprover(teacher);
        application.setApprovalComment(comment);
        application.setApprovalTime(LocalDateTime.now());

        return leaveApplicationRepository.save(application);
    }

    /*
     * 取消请假申请
     */



    public void cancelLeaveApplication(Integer applicationId) {
        LeaveApplication application = getValidApplication(applicationId);

        application.setStatus(LeaveApplication.ApprovalStatus.CANCELLED);
        leaveApplicationRepository.save(application);
    }

    /*
     * 获取学生的所有请假申请
     */


    public List<LeaveApplication> getStudentLeaveApplications(Integer personId) {
        return leaveApplicationRepository.findByStudentPersonPersonId(personId);
    }

    /**
     * 获取班级待审批的请假申请
     */


    public List<LeaveApplication> getPendingApplicationsByClass(String className) {
        return leaveApplicationRepository.findByStudentClassNameAndStatus(className, LeaveApplication.ApprovalStatus.PENDING);
    }

    /**
     * 获取班级某天已批准的请假申请
     */


    public List<LeaveApplication> getApprovedApplicationsByClass(String className, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return leaveApplicationRepository.findByClassNameAndDateRange(
                className, startOfDay, endOfDay);
    }

    /**
     * 检查时间冲突
     */


    public boolean hasOverlappingLeaves(Integer studentId, LocalDateTime startTime, LocalDateTime endTime) {
        List<LeaveApplication> overlappingLeaves = leaveApplicationRepository
                .findByStudentPersonPersonIdAndStatusNot(studentId, LeaveApplication.ApprovalStatus.REJECTED)
                .stream()
                .filter(la -> la.getStartTime().isBefore(endTime) && la.getEndTime().isAfter(startTime))
                .toList();

        return !overlappingLeaves.isEmpty();
    }

    // 私有辅助方法

    private LeaveApplication getValidApplication(Integer applicationId) {
        LeaveApplication application = leaveApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("请假申请不存在"));

        if (application.getStatus() != LeaveApplication.ApprovalStatus.PENDING) {
            throw new IllegalStateException("请假申请状态不符合要求");
        }

        return application;
    }

    private Teacher getTeacher(Integer teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("教师不存在"));
    }

}
