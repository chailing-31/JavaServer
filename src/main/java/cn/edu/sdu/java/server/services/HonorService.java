package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.repositorys.HonorRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * HonorService 荣誉奖项服务类
 */
@Service
public class HonorService {

    @Autowired
    private HonorRepository honorRepository;

    @Autowired
    private cn.edu.sdu.java.server.repositorys.StudentRepository studentRepository;

    /**
     * 获取荣誉列表
     */
    public List<Map<String, Object>> getHonorList(Map<String, String> form) {
        String numName = form.get("numName");
        String honorType = form.get("honorType");
        String honorLevel = form.get("honorLevel");
        String status = form.get("status");

        List<Honor> honors = honorRepository.findByConditions(numName, honorType, honorLevel, status);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Honor honor : honors) {
            Map<String, Object> data = new HashMap<>();
            data.put("honorId", honor.getHonorId());
            data.put("num", honor.getNum());
            data.put("studentId", honor.getStudentId());
            data.put("studentName",
                    honor.getStudent() != null && honor.getStudent().getPerson() != null
                            ? honor.getStudent().getPerson().getName()
                            : "");
            data.put("honorName", honor.getHonorName());
            data.put("honorType", honor.getHonorType());
            data.put("honorLevel", honor.getHonorLevel());
            data.put("awardingOrganization", honor.getAwardingOrganization());
            data.put("awardDate", honor.getAwardDate() != null ? honor.getAwardDate().toString() : "");
            data.put("certificateNumber", honor.getCertificateNumber());
            data.put("description", honor.getDescription());
            data.put("status", honor.getStatus());
            result.add(data);
        }

        return result;
    }

    /**
     * 获取荣誉详细信息
     */
    public Map<String, Object> getHonorInfo(Integer honorId) {
        Optional<Honor> optionalHonor = honorRepository.findById(honorId);
        if (optionalHonor.isPresent()) {
            Honor honor = optionalHonor.get();
            Map<String, Object> data = new HashMap<>();
            data.put("honorId", honor.getHonorId());
            data.put("num", honor.getNum());
            data.put("studentId", honor.getStudentId());
            data.put("honorName", honor.getHonorName());
            data.put("honorType", honor.getHonorType());
            data.put("honorLevel", honor.getHonorLevel());
            data.put("awardingOrganization", honor.getAwardingOrganization());
            data.put("awardDate", honor.getAwardDate() != null ? honor.getAwardDate().toString() : "");
            data.put("certificateNumber", honor.getCertificateNumber());
            data.put("description", honor.getDescription());
            data.put("status", honor.getStatus());
            return data;
        }
        return null;
    }

    /**
     * 保存荣誉信息
     */
    public Map<String, String> honorEditSave(Map<String, Object> form) {
        Map<String, String> result = new HashMap<>();
        try {
            Honor honor;
            Object honorIdObj = form.get("honorId");
            if (honorIdObj != null && !honorIdObj.toString().isEmpty() && !"null".equals(honorIdObj.toString())) {
                Integer honorId = CommonMethod.getInteger(form, "honorId");
                Optional<Honor> optionalHonor = honorRepository.findById(honorId);
                if (optionalHonor.isPresent()) {
                    honor = optionalHonor.get();
                } else {
                    result.put("result", "error");
                    result.put("message", "荣誉记录不存在！");
                    return result;
                }
            } else {
                honor = new Honor();
                // 检查编号是否已存在
                Map<String, Object> honorForm = (Map<String, Object>) form.get("form");
                String num = CommonMethod.getString(honorForm, "num");
                if (honorRepository.findByNum(num) != null) {
                    result.put("result", "error");
                    result.put("message", "荣誉编号已存在！");
                    return result;
                }
            }

            // 填充荣誉信息
            Map<String, Object> honorForm = (Map<String, Object>) form.get("form");
            honor.setNum(CommonMethod.getString(honorForm, "num"));
            honor.setStudentId(CommonMethod.getInteger(honorForm, "studentId"));
            honor.setHonorName(CommonMethod.getString(honorForm, "honorName"));
            honor.setHonorType(CommonMethod.getString(honorForm, "honorType"));
            honor.setHonorLevel(CommonMethod.getString(honorForm, "honorLevel"));
            honor.setAwardingOrganization(CommonMethod.getString(honorForm, "awardingOrganization"));

            // 处理获奖日期
            String awardDateStr = CommonMethod.getString(honorForm, "awardDate");
            if (!awardDateStr.isEmpty()) {
                try {
                    honor.setAwardDate(LocalDate.parse(awardDateStr));
                } catch (DateTimeParseException e) {
                    result.put("result", "error");
                    result.put("message", "获奖日期格式不正确！");
                    return result;
                }
            }

            honor.setCertificateNumber(CommonMethod.getString(honorForm, "certificateNumber"));
            honor.setDescription(CommonMethod.getString(honorForm, "description"));
            honor.setStatus(CommonMethod.getString(honorForm, "status"));

            honorRepository.save(honor);
            result.put("result", "success");
            result.put("message", "保存成功！");

        } catch (Exception e) {
            result.put("result", "error");
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除荣誉记录
     */
    public Map<String, String> honorDelete(Integer honorId) {
        Map<String, String> result = new HashMap<>();
        try {
            Optional<Honor> optionalHonor = honorRepository.findById(honorId);
            if (optionalHonor.isPresent()) {
                honorRepository.deleteById(honorId);
                result.put("result", "success");
                result.put("message", "删除成功！");
            } else {
                result.put("result", "error");
                result.put("message", "荣誉记录不存在！");
            }
        } catch (Exception e) {
            result.put("result", "error");
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 导出Excel
     */
    public byte[] getHonorListExcel(Map<String, String> form) {
        String numName = form.get("numName");
        List<Honor> honors = honorRepository.findByConditions(numName, "", "", "");

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("荣誉列表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = { "荣誉编号", "学生姓名", "荣誉名称", "荣誉类型", "荣誉等级",
                    "颁奖机构", "获奖日期", "证书编号", "状态", "描述" };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            int rowIndex = 1;
            for (Honor honor : honors) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(honor.getNum());
                row.createCell(1)
                        .setCellValue(honor.getStudent() != null && honor.getStudent().getPerson() != null
                                ? honor.getStudent().getPerson().getName()
                                : "");
                row.createCell(2).setCellValue(honor.getHonorName());
                row.createCell(3).setCellValue(honor.getHonorType());
                row.createCell(4).setCellValue(honor.getHonorLevel());
                row.createCell(5).setCellValue(honor.getAwardingOrganization());
                row.createCell(6).setCellValue(honor.getAwardDate() != null ? honor.getAwardDate().toString() : "");
                row.createCell(7).setCellValue(honor.getCertificateNumber());
                row.createCell(8).setCellValue(honor.getStatus());
                row.createCell(9).setCellValue(honor.getDescription());
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
    public List<Map<String, Object>> getStudentList() {
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
            return result;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}