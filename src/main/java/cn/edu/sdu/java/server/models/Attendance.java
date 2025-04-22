package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Optional;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "attendances")
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;  // 考勤状态

    private String remark;  // 备注



    // 枚举定义考勤状态
    public enum AttendanceStatus {
        PRESENT,   // 出勤
        ABSENT,    // 缺勤
        LATE,      // 迟到
        LEAVE,     // 请假
        EARLY_LEAVE // 早退
    }

    // 构造方法、getter/setter...
}
