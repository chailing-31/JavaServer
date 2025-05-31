package cn.edu.sdu.java.server.services;

/*import cn.edu.sdu.java.server.models.Attendance;
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



    public List<Attendance> getStudentAttendance(Integer personId) {
        if (personId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }

        return attendanceRepository.findByStudentPersonId(personId);
    }



    public List<Attendance> getClassAttendance(String className, LocalDate date) {
        if (className == null || className.trim().isEmpty()) {
            throw new IllegalArgumentException("班级名称不能为空");
        }

        if (date == null) {
            throw new IllegalArgumentException("考勤日期不能为空");
        }

        return attendanceRepository.findByStudentClassNameAndDate(className, date);
    }



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




    public void deleteAttendance(Long attendanceId) {
        if (attendanceId == null) {
            throw new IllegalArgumentException("考勤记录ID不能为空");
        }

        if (!attendanceRepository.existsById(attendanceId)) {
            throw new IllegalArgumentException("考勤记录不存在");
        }

        attendanceRepository.deleteById(attendanceId);
    }



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
*/


import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.Attendance;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.AttendanceRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
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
     * 将Attendance对象转换为Map
     */
    public Map<String, Object> getMapFromAttendance(Attendance a) {
        Map<String, Object> m = new HashMap<>();
        if (a == null) {
            return m;
        }
        m.put("attendanceId", a.getId());
        m.put("personId", a.getStudent().getPersonId());
        m.put("studentNum", a.getStudent().getPerson().getNum());
        m.put("studentName", a.getStudent().getPerson().getName());
        m.put("className", a.getStudent().getClassName());
        m.put("date", a.getDate());
        m.put("status", a.getStatus().name());
        m.put("statusName", a.getStatus().getDisplayName());
        m.put("remark", a.getRemark());
        return m;
    }

    /**
     * 获取考勤状态选项列表
     */
    public OptionItemList getAttendanceStatusOptionList() {
        List<OptionItem> items = Arrays.stream(Attendance.AttendanceStatus.values())
                .map(status -> new OptionItem(status.name(), status.getDisplayName()))
                .collect(Collectors.toList());

        return new OptionItemList(0, items);
    }



    /**
     * 记录考勤信息
     */
    public DataResponse recordAttendance(Map<String, Object> form) {
        try {
            Integer personId = CommonMethod.getInteger(form, "personId");
            LocalDate date = CommonMethod.getLocalDate(form, "date");
            Attendance.AttendanceStatus status = Attendance.AttendanceStatus.valueOf(CommonMethod.getString(form, "status"));
            String remark = CommonMethod.getString(form, "remark");

            if (personId == null) {
                return CommonMethod.getReturnMessageError("学生ID不能为空");
            }

            if (date == null || date.isAfter(LocalDate.now())) {
                return CommonMethod.getReturnMessageError("考勤日期不合法");
            }

            if (status == null) {
                return CommonMethod.getReturnMessageError("考勤状态不能为空");
            }

            Student student = studentRepository.findById(personId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在"));

            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setDate(date);
            attendance.setStatus(status);
            attendance.setRemark(remark);

            attendance = attendanceRepository.save(attendance);
            return CommonMethod.getReturnData(getMapFromAttendance(attendance));
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("记录考勤失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生所有考勤记录
     */
    public DataResponse getStudentAttendance(Integer personId) {
        if (personId == null) {
            return CommonMethod.getReturnMessageError("学生ID不能为空");
        }

        List<Attendance> attendances = attendanceRepository.findByStudentPersonId(personId);
        List<Map<String, Object>> dataList = attendances.stream()
                .map(this::getMapFromAttendance)
                .collect(Collectors.toList());

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取班级某天的考勤记录
     */
    public DataResponse getClassAttendance(String className, LocalDate date) {
        if (className == null || className.trim().isEmpty()) {
            return CommonMethod.getReturnMessageError("班级名称不能为空");
        }

        if (date == null) {
            return CommonMethod.getReturnMessageError("考勤日期不能为空");
        }

        List<Attendance> attendances = attendanceRepository.findByStudentClassNameAndDate(className, date);
        List<Map<String, Object>> dataList = attendances.stream()
                .map(this::getMapFromAttendance)
                .collect(Collectors.toList());

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取学生考勤统计
     */
    public DataResponse getAttendanceStatistics(Integer personId, LocalDate startDate, LocalDate endDate) {
        if (personId == null) {
            return CommonMethod.getReturnMessageError("学生ID不能为空");
        }

        if (startDate == null || endDate == null) {
            return CommonMethod.getReturnMessageError("日期范围不能为空");
        }

        if (startDate.isAfter(endDate)) {
            return CommonMethod.getReturnMessageError("开始日期不能晚于结束日期");
        }

        List<Attendance> attendances = attendanceRepository.findByStudentPersonIdAndDateBetween(personId, startDate, endDate);

        Map<String, Long> statistics = attendances.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus().getDisplayName(),
                        Collectors.counting()
                ));

        return CommonMethod.getReturnData(statistics);
    }

    /**
     * 更新考勤记录
     */
    public DataResponse updateAttendance(Map<String, Object> form) {
        try {
            Integer attendanceId = CommonMethod.getInteger(form, "attendanceId");
            Attendance.AttendanceStatus status = Attendance.AttendanceStatus.valueOf(CommonMethod.getString(form, "status"));
            String remark = CommonMethod.getString(form, "remark");

            if (attendanceId == null) {
                return CommonMethod.getReturnMessageError("考勤记录ID不能为空");
            }

            if (status == null) {
                return CommonMethod.getReturnMessageError("考勤状态不能为空");
            }

            Attendance attendance = attendanceRepository.findById(attendanceId);
                    //.orElseThrow(() -> new IllegalArgumentException("考勤记录不存在"));

            attendance.setStatus(status);
            attendance.setRemark(remark);
            attendance = attendanceRepository.save(attendance);

            return CommonMethod.getReturnData(getMapFromAttendance(attendance));
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("更新考勤失败: " + e.getMessage());
        }
    }

    /**
     * 删除考勤记录
     */
    public DataResponse deleteAttendance(Integer attendanceId) {
        try {
            if (attendanceId == null) {
                return CommonMethod.getReturnMessageError("考勤记录ID不能为空");
            }

            if (!attendanceRepository.existsById(Long.valueOf(attendanceId))) {
                return CommonMethod.getReturnMessageError("考勤记录不存在");
            }

            attendanceRepository.deleteById(Long.valueOf(attendanceId));
            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("删除考勤失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入考勤记录
     */
    public DataResponse batchRecordAttendance(List<Map<String, Object>> attendanceList) {
        try {
            if (attendanceList == null || attendanceList.isEmpty()) {
                return CommonMethod.getReturnMessageError("考勤记录列表不能为空");
            }

            // 验证所有学生是否存在
            Set<Integer> studentIds = attendanceList.stream()
                    .map(a -> CommonMethod.getInteger(a, "personId"))
                    .collect(Collectors.toSet());

            Map<Integer, Student> students = studentRepository.findAllById(studentIds).stream()
                    .collect(Collectors.toMap(Student::getPersonId, s -> s));

            if (students.size() != studentIds.size()) {
                return CommonMethod.getReturnMessageError("部分学生不存在");
            }

            // 创建考勤记录
            List<Attendance> attendances = attendanceList.stream()
                    .map(a -> {
                        Attendance attendance = new Attendance();
                        attendance.setStudent(students.get(CommonMethod.getInteger(a, "personId")));
                        attendance.setDate(CommonMethod.getLocalDate(a, "date"));
                        attendance.setStatus(Attendance.AttendanceStatus.valueOf(CommonMethod.getString(a, "status")));
                        attendance.setRemark(CommonMethod.getString(a, "remark"));
                        return attendance;
                    })
                    .collect(Collectors.toList());

            List<Attendance> savedAttendances = attendanceRepository.saveAll(attendances);
            List<Map<String, Object>> dataList = savedAttendances.stream()
                    .map(this::getMapFromAttendance)
                    .collect(Collectors.toList());

            return CommonMethod.getReturnData(dataList);
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("批量导入考勤失败: " + e.getMessage());
        }
    }
}