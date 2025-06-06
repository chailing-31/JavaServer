package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.LeaveRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.LeaveRequestRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * LeaveService 请假管理服务类
 */
@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private cn.edu.sdu.java.server.repositorys.StudentRepository studentRepository;

    /**
     * 获取请假列表
     */
    public DataResponse getLeaveList(Map<String, String> form) {
        String numName = form.get("numName");
        String leaveType = form.get("leaveType");
        String status = form.get("status");

        List<LeaveRequest> leaves = leaveRequestRepository.findByConditions(numName, leaveType, status);
        List<Map<String, Object>> result = new ArrayList<>();

        for (LeaveRequest leave : leaves) {
            Map<String, Object> data = new HashMap<>();
            data.put("leaveId", leave.getLeaveId());
            data.put("num", leave.getNum());
            data.put("studentId", leave.getStudent().getPersonId());
            data.put("studentName",
                    leave.getStudent() != null && leave.getStudent().getPerson() != null
                            ? leave.getStudent().getPerson().getName()
                            : "");
            data.put("leaveType", leave.getLeaveType());
            data.put("startDate", leave.getStartDate() != null ? leave.getStartDate().toString() : "");
            data.put("endDate", leave.getEndDate() != null ? leave.getEndDate().toString() : "");
            data.put("days", leave.getDays());
            data.put("reason", leave.getReason());
            data.put("status", leave.getStatus());
            data.put("approveComment", leave.getApproveComment());
            data.put("teacherId",leave.getStudent().getPersonId());
            result.add(data);
        }
        return CommonMethod.getReturnData(result);  //按照测试框架规范会送Map的list

    }

    /**
     * 获取请假详细信息
     */
    public DataResponse getLeaveInfo(Integer leaveId) {
        Optional<LeaveRequest> optionalLeave = leaveRequestRepository.findById(leaveId);
        if (optionalLeave.isPresent()) {
            LeaveRequest leave = optionalLeave.get();
            Map<String, Object> data = new HashMap<>();
            data.put("leaveId", leave.getLeaveId());
            data.put("num", leave.getNum());
            data.put("studentId", leave.getStudent().getPersonId());
            data.put("leaveType", leave.getLeaveType());
            data.put("startDate", leave.getStartDate() != null ? leave.getStartDate().toString() : "");
            data.put("endDate", leave.getEndDate() != null ? leave.getEndDate().toString() : "");
            data.put("days", leave.getDays());
            data.put("reason", leave.getReason());
            data.put("status", leave.getStatus());
            data.put("approveComment", leave.getApproveComment());
            data.put("teacherId",leave.getStudent().getPersonId());
            return CommonMethod.getReturnData(data);  //按照测试框架规范会送Map的list
        }
        return null;
    }

    /**
     * 保存请假信息
     */
    public Map<String, String> leaveEditSave(Map<String, Object> form) {
        Map<String, String> result = new HashMap<>();
        try {
            LeaveRequest leave;
            Object leaveIdObj = form.get("leaveId");
            if (leaveIdObj != null && !leaveIdObj.toString().isEmpty() && !"null".equals(leaveIdObj.toString())) {
                Integer leaveId = CommonMethod.getInteger(form, "leaveId");
                Optional<LeaveRequest> optionalLeave = leaveRequestRepository.findById(leaveId);
                if (optionalLeave.isPresent()) {
                    leave = optionalLeave.get();
                } else {
                    result.put("result", "error");
                    result.put("message", "请假记录不存在！");
                    return result;
                }
            } else {
                leave = new LeaveRequest();
                // 检查编号是否已存在
                Map<String, Object> leaveForm = (Map<String, Object>) form.get("form");
                String num = CommonMethod.getString(leaveForm, "num");
                if (leaveRequestRepository.findByNum(num) != null) {
                    result.put("result", "error");
                    result.put("message", "请假编号已存在！");
                    return result;
                }
            }

            // 填充请假信息
            Map<String, Object> leaveForm = (Map<String, Object>) form.get("form");
            leave.setNum(CommonMethod.getString(leaveForm, "num"));
            leave.setStudentId(CommonMethod.getInteger(leaveForm, "studentId"));
            leave.setLeaveType(CommonMethod.getString(leaveForm, "leaveType"));

            // 处理日期
            String startDateStr = CommonMethod.getString(leaveForm, "startDate");
            String endDateStr = CommonMethod.getString(leaveForm, "endDate");

            if (!startDateStr.isEmpty() && !endDateStr.isEmpty()) {
                try {
                    // 前端传来的格式是 "2025-05-23 08:00:00"，直接解析
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime startDate = LocalDateTime.parse(startDateStr, formatter);
                    LocalDateTime endDate = LocalDateTime.parse(endDateStr, formatter);

                    if (endDate.isBefore(startDate)) {
                        result.put("result", "error");
                        result.put("message", "结束时间不能早于开始时间！");
                        return result;
                    }

                    leave.setStartDate(startDate);
                    leave.setEndDate(endDate);

                    // 计算天数
                    long days = ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
                    leave.setDays((int) days);

                } catch (DateTimeParseException e) {
                    result.put("result", "error");
                    result.put("message", "日期格式不正确！请使用正确的日期格式");
                    return result;
                }
            }

            leave.setReason(CommonMethod.getString(leaveForm, "reason"));
            leave.setStatus(CommonMethod.getString(leaveForm, "status"));
            leave.setApproveComment(CommonMethod.getString(leaveForm, "approveComment"));

            leaveRequestRepository.save(leave);
            result.put("result", "success");
            result.put("message", "保存成功！");

        } catch (Exception e) {
            result.put("result", "error");
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除请假记录
     */
    public Map<String, String> leaveDelete(Integer leaveId) {
        Map<String, String> result = new HashMap<>();
        try {
            Optional<LeaveRequest> optionalLeave = leaveRequestRepository.findById(leaveId);
            if (optionalLeave.isPresent()) {
                leaveRequestRepository.deleteById(leaveId);
                result.put("result", "success");
                result.put("message", "删除成功！");
            } else {
                result.put("result", "error");
                result.put("message", "请假记录不存在！");
            }
        } catch (Exception e) {
            result.put("result", "error");
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 批准请假
     */
    public Map<String, String> approveLeave(Integer leaveId, String comment) {
        Map<String, String> result = new HashMap<>();
        try {
            Optional<LeaveRequest> optionalLeave = leaveRequestRepository.findById(leaveId);
            if (optionalLeave.isPresent()) {
                LeaveRequest leave = optionalLeave.get();
                leave.setStatus("已批准");
                leave.setApproveComment(comment);
                leaveRequestRepository.save(leave);
                result.put("result", "success");
                result.put("message", "批准成功！");
            } else {
                result.put("result", "error");
                result.put("message", "请假记录不存在！");
            }
        } catch (Exception e) {
            result.put("result", "error");
            result.put("message", "批准失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 拒绝请假
     */
    public Map<String, String> rejectLeave(Integer leaveId, String comment) {
        Map<String, String> result = new HashMap<>();
        try {
            Optional<LeaveRequest> optionalLeave = leaveRequestRepository.findById(leaveId);
            if (optionalLeave.isPresent()) {
                LeaveRequest leave = optionalLeave.get();
                leave.setStatus("已拒绝");
                leave.setApproveComment(comment);
                leaveRequestRepository.save(leave);
                result.put("result", "success");
                result.put("message", "拒绝成功！");
            } else {
                result.put("result", "error");
                result.put("message", "请假记录不存在！");
            }
        } catch (Exception e) {
            result.put("result", "error");
            result.put("message", "拒绝失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 导出Excel
     */
    public byte[] getLeaveListExcel(Map<String, String> form) {
        String numName = form.get("numName");
        List<LeaveRequest> leaves = leaveRequestRepository.findByConditions(numName, "", "");

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("请假列表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = { "请假编号", "学生姓名", "请假类型", "开始时间", "结束时间",
                    "请假天数", "请假原因", "状态", "审批意见" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            int rowIndex = 1;
            for (LeaveRequest leave : leaves) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(leave.getNum());
                row.createCell(1)
                        .setCellValue(leave.getStudent() != null && leave.getStudent().getPerson() != null
                                ? leave.getStudent().getPerson().getName()
                                : "");
                row.createCell(2).setCellValue(leave.getLeaveType());
                row.createCell(3).setCellValue(leave.getStartDate() != null ? leave.getStartDate().toString() : "");
                row.createCell(4).setCellValue(leave.getEndDate() != null ? leave.getEndDate().toString() : "");
                row.createCell(5).setCellValue(leave.getDays() != null ? leave.getDays().toString() : "");
                row.createCell(6).setCellValue(leave.getReason());
                row.createCell(7).setCellValue(leave.getStatus());
                row.createCell(8).setCellValue(leave.getApproveComment());
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取学生列表
     */
    public DataResponse getStudentList() {
        try {
            List<cn.edu.sdu.java.server.models.Student> students = studentRepository.findStudentListByNumName("");
            List<Map<String, Object>> result = new ArrayList<>();

            for (cn.edu.sdu.java.server.models.Student student : students) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", student.getPersonId());
                data.put("value", student.getPersonId().toString());
                data.put("title", student.getPerson().getNum() + "-" + student.getPerson().getName());
                result.add(data);
            }
            return CommonMethod.getReturnData(result);  //按照测试框架规范会送Map的list;
        } catch (Exception e) {
            return CommonMethod.getReturnData(new ArrayList<>());  //按照测试框架规范会送Map的list
        }
    }

    //获取要求该老师审批的所有记录
    /*public DataResponse  getLeaveListByTeacher(String num,String name) {
        List<LeaveRequest> leaves = leaveRequestRepository.findByTeacherPersonNumAndTeacherPersonName(num,name);
        List<Map<String, Object>> result = new ArrayList<>();
        for (LeaveRequest leave : leaves) {
            Map<String, Object> data = new HashMap<>();
            data.put("leaveId", leave.getLeaveId());
            data.put("num", leave.getNum());
            data.put("studentId", leave.getStudent().getPersonId());
            data.put("studentName",
                    leave.getStudent() != null && leave.getStudent().getPerson() != null
                            ? leave.getStudent().getPerson().getName()
                            : "");
            data.put("leaveType", leave.getLeaveType());
            data.put("startDate", leave.getStartDate() != null ? leave.getStartDate().toString() : "");
            data.put("endDate", leave.getEndDate() != null ? leave.getEndDate().toString() : "");
            data.put("days", leave.getDays());
            data.put("reason", leave.getReason());
            data.put("status", leave.getStatus());
            data.put("approveComment", leave.getApproveComment());
            data.put("teacherId",leave.getPerson().getNum()+"-"+leave.getPerson().getName());

            result.add(data);
        }
        return CommonMethod.getReturnData(result);  //按照测试框架规范会送Map的list;
    }*/
}