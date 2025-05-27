package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "leave_applications")
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 500)
    private String reason;

    //请假类型
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveType leaveType;

    //审批状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Teacher approver;

    //审批评价
    @Column(length = 500)
    private String approvalComment;

    //审批时间
    private LocalDateTime approvalTime;


    // 枚举定义请假类型
    public enum LeaveType {
        SICK_LEAVE,      // 病假
        PERSONAL_LEAVE,  // 事假
        OFFICIAL_LEAVE   // 公假
    }


    // 枚举定义审批状态
    public enum ApprovalStatus {
        PENDING,        // 待审批
        APPROVED,       // 已批准
        REJECTED,       // 已拒绝
        CANCELLED       // 已取消
    }

}
