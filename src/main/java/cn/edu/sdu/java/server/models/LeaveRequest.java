package cn.edu.sdu.java.server.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import cn.edu.sdu.java.server.models.Student;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * LeaveRequest 请假申请实体类
 */
@Getter
@Setter
@Entity
@Table(name = "leave_request")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    private Integer leaveId;

    @NotBlank(message = "请假编号不能为空")
    @Column(name = "num", length = 20, unique = true)
    private String num;

    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "请假类型不能为空")
    @Column(name = "leave_type", length = 20)
    private String leaveType;

    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @NotNull(message = "结束日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "days")
    private Integer days;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "status", length = 20)
    private String status = "待审批";

    @Column(name = "approver_id")
    private Integer approverId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "approve_time")
    private LocalDateTime approveTime;

    @Column(name = "approve_comment", columnDefinition = "TEXT")
    private String approveComment;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;


    // 关联学生信息
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    //关联老师信息
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public LeaveRequest() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

//    // Getters and Setters
//    public Integer getLeaveId() {
//        return leaveId;
//    }
//
//    public void setLeaveId(Integer leaveId) {
//        this.leaveId = leaveId;
//    }
//
//    public String getNum() {
//        return num;
//    }
//
//    public void setNum(String num) {
//        this.num = num;
//    }
//
//    public Integer getStudentId() {
//        return studentId;
//    }
//
//    public void setStudentId(Integer studentId) {
//        this.studentId = studentId;
//    }
//
//    public String getLeaveType() {
//        return leaveType;
//    }
//
//    public void setLeaveType(String leaveType) {
//        this.leaveType = leaveType;
//    }
//
//    public LocalDateTime getStartDate() {
//        return startDate;
//    }
//
//    public void setStartDate(LocalDateTime startDate) {
//        this.startDate = startDate;
//    }
//
//    public LocalDateTime getEndDate() {
//        return endDate;
//    }
//
//    public void setEndDate(LocalDateTime endDate) {
//        this.endDate = endDate;
//    }
//
//    public Integer getDays() {
//        return days;
//    }
//
//    public void setDays(Integer days) {
//        this.days = days;
//    }
//
//    public String getReason() {
//        return reason;
//    }
//
//    public void setReason(String reason) {
//        this.reason = reason;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public Integer getApproverId() {
//        return approverId;
//    }
//
//    public void setApproverId(Integer approverId) {
//        this.approverId = approverId;
//    }
//
//    public LocalDateTime getApproveTime() {
//        return approveTime;
//    }
//
//    public void setApproveTime(LocalDateTime approveTime) {
//        this.approveTime = approveTime;
//    }
//
//    public String getApproveComment() {
//        return approveComment;
//    }
//
//    public void setApproveComment(String approveComment) {
//        this.approveComment = approveComment;
//    }
//
//    public String getAttachmentPath() {
//        return attachmentPath;
//    }
//
//    public void setAttachmentPath(String attachmentPath) {
//        this.attachmentPath = attachmentPath;
//    }

//    public LocalDateTime getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(LocalDateTime createTime) {
//        this.createTime = createTime;
//    }
//
//    public LocalDateTime getUpdateTime() {
//        return updateTime;
//    }
//
//    public void setUpdateTime(LocalDateTime updateTime) {
//        this.updateTime = updateTime;
//    }
//
//    public Student getStudent() {
//        return student;
//    }
//
//    public void setStudent(Student student) {
//        this.student = student;
//    }
}