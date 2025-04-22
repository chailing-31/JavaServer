package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Attendance;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.repositorys.AttendanceRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * 记录考勤信息
     * @param attendance 考勤记录对象
     * @return 保存后的考勤记录
     * @throws IllegalArgumentException 如果学生不存在
     */
    public Attendance recordAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("考勤记录不能为空");
        }

        // 验证学生是否存在
        Student student = studentRepository.findById(attendance.getStudent().getPersonId())
                .orElseThrow(() -> new IllegalArgumentException("学生不存在"));

        // 验证日期是否合理
        if (attendance.getDate() == null || attendance.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("考勤日期不合法");
        }

        // 验证考勤状态
        if (attendance.getStatus() == null) {
            throw new IllegalArgumentException("考勤状态不能为空");
        }

        attendance.setStudent(student);
        return attendanceRepository.save(attendance);
    }

    /**
     * 获取学生所有考勤记录
     * @param personId 学生ID
     * @return 该学生的考勤记录列表
     * @throws IllegalArgumentException 如果学生ID为空
     */
    public List<Attendance> getStudentAttendance(Integer personId) {
        if (personId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        return attendanceRepository.findByStudentPersonId(personId);
    }

    /**
     * 获取班级某天的考勤记录
     * @param className 班级名称
     * @param date 考勤日期
     * @return 考勤记录列表
     * @throws IllegalArgumentException 如果参数不合法
     */
    public List<Attendance> getClassAttendance(String className, LocalDate date) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("班级名称不能为空");
        }

        if (date == null) {
            throw new IllegalArgumentException("考勤日期不能为空");
        }

        return attendanceRepository.findByStudentClassNameAndDate(className, date);
    }

    /**
     * 获取学生考勤统计
     * @param personId 学生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 考勤状态统计Map
     * @throws IllegalArgumentException 如果参数不合法
     */
    public Map<Attendance.AttendanceStatus, Long> getAttendanceStatistics(Integer personId, LocalDate startDate, LocalDate endDate) {
        if (personId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("日期范围不能为空");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        List<Attendance> attendances = attendanceRepository.findByStudentPersonIdAndDateBetween(personId, startDate, endDate);

        return attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));
    }

    /**
     * 更新考勤记录
     * @param attendanceId 考勤记录ID
     * @param status 新的考勤状态
     * @param remark 备注信息
     * @return 更新后的考勤记录
     * @throws IllegalArgumentException 如果参数不合法或记录不存在
     */
    public Attendance updateAttendance(Integer attendanceId, Attendance.AttendanceStatus status, String remark) {
        if (attendanceId == null) {
            throw new IllegalArgumentException("考勤记录ID不能为空");
        }

        if (status == null) {
            throw new IllegalArgumentException("考勤状态不能为空");
        }

        Optional<Attendance> optionalAttendance = Optional.ofNullable(attendanceRepository.findById(attendanceId));
        Attendance attendance = optionalAttendance
                .orElseThrow(() -> new IllegalArgumentException("考勤记录不存在"));

        attendance.setStatus(status);
        attendance.setRemark(remark);
        return attendanceRepository.save(attendance);
    }

    /**
     * 删除考勤记录
     * @param attendanceId 考勤记录ID
     * @throws IllegalArgumentException 如果记录ID为空
     */
    public void deleteAttendance(Long attendanceId) {
        if (attendanceId == null) {
            throw new IllegalArgumentException("考勤记录ID不能为空");
        }

        if (!attendanceRepository.existsById(attendanceId)) {
            throw new IllegalArgumentException("考勤记录不存在");
        }

        attendanceRepository.deleteById(attendanceId);
    }

    /**
     * 批量导入考勤记录
     * @param attendances 考勤记录列表
     * @return 导入成功的记录列表
     */
    public List<Attendance> batchRecordAttendance(List<Attendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return Collections.emptyList();
        }

        // 验证所有学生是否存在
        Set<Integer> studentIds = attendances.stream()
                .map(a -> a.getStudent().getPersonId())
                .collect(Collectors.toSet());

        Map<Integer, Student> students = studentRepository.findAllById(studentIds).stream()
                .collect(Collectors.toMap(Student::getPersonId, s -> s));

        if (students.size() != studentIds.size()) {
            throw new IllegalArgumentException("部分学生不存在");
        }

        // 设置学生关系并保存
        attendances.forEach(a -> a.setStudent(students.get(a.getStudent().getPersonId())));
        return attendanceRepository.saveAll(attendances);
    }
}
