package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Assignment;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.AssignmentRepository;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AssignmentService {
    private static final Logger log = LoggerFactory.getLogger(AssignmentService.class);
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    /**
     * 将Assignment对象转换为Map
     */
    public Map<String, Object> getMapFromAssignment(Assignment assignment) {
        Map<String, Object> m = new HashMap<>();
        if (assignment == null)
            return m;

        m.put("assignmentId", assignment.getAssignmentId());
        m.put("num", assignment.getNum());
        m.put("title", assignment.getTitle());
        m.put("courseId", assignment.getCourseId());
        m.put("teacherId", assignment.getTeacherId());
        m.put("publishTime", assignment.getPublishTime());
        m.put("deadline", assignment.getDeadline());
        m.put("content", assignment.getContent());
        m.put("requirements", assignment.getRequirements());
        m.put("totalScore", assignment.getTotalScore());
        m.put("status", assignment.getStatus());
        m.put("attachmentPath", assignment.getAttachmentPath());
        m.put("submissionType", assignment.getSubmissionType());

        // 添加课程信息
        if (assignment.getCourse() != null) {
            m.put("courseName", assignment.getCourse().getName());
            m.put("courseNum", assignment.getCourse().getNum());
        } else if (assignment.getCourseId() != null) {
            Optional<Course> courseOp = courseRepository.findById(assignment.getCourseId());
            if (courseOp.isPresent()) {
                Course course = courseOp.get();
                m.put("courseName", course.getName());
                m.put("courseNum", course.getNum());
            }
        }

        return m;
    }

    /**
     * 获取作业列表
     */
    public DataResponse getAssignmentList(DataRequest dataRequest) {
        String numTitle = dataRequest.getString("numTitle");
        Integer courseId = dataRequest.getInteger("courseId");
        if (numTitle == null)
            numTitle = "";

        List<Assignment> assignmentList;
        if (courseId != null && courseId > 0) {
            assignmentList = assignmentRepository.findAssignmentListByCourseAndNumTitle(courseId, numTitle);
        } else if (numTitle.isEmpty()) {
            assignmentList = assignmentRepository.findAllOrderByPublishTimeDesc();
        } else {
            assignmentList = assignmentRepository.findAssignmentListByNumTitle(numTitle);
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Assignment assignment : assignmentList) {
            dataList.add(getMapFromAssignment(assignment));
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取作业详细信息
     */
    public DataResponse getAssignmentInfo(DataRequest dataRequest) {
        Integer assignmentId = dataRequest.getInteger("assignmentId");
        Assignment assignment = null;

        if (assignmentId != null) {
            Optional<Assignment> op = assignmentRepository.findById(assignmentId);
            if (op.isPresent()) {
                assignment = op.get();
            }
        }

        return CommonMethod.getReturnData(getMapFromAssignment(assignment));
    }

    /**
     * 保存作业信息
     */
    public DataResponse assignmentSave(DataRequest dataRequest) {
        try {
            Integer assignmentId = dataRequest.getInteger("assignmentId");
            Map<String, Object> form = dataRequest.getMap("form");

            if (form == null) {
                return CommonMethod.getReturnMessageError("表单数据不能为空");
            }

            String num = CommonMethod.getString(form, "num");
            String title = CommonMethod.getString(form, "title");
            Integer courseId = CommonMethod.getInteger(form, "courseId");
            Integer teacherId = CommonMethod.getInteger(form, "teacherId");
            String publishTime = CommonMethod.getString(form, "publishTime");
            String deadline = CommonMethod.getString(form, "deadline");
            String content = CommonMethod.getString(form, "content");
            String requirements = CommonMethod.getString(form, "requirements");
            Integer totalScore = CommonMethod.getInteger(form, "totalScore");
            String status = CommonMethod.getString(form, "status");
            String submissionType = CommonMethod.getString(form, "submissionType");

            // 数据验证
            if (num == null || num.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("作业编号不能为空");
            }
            if (title == null || title.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("作业标题不能为空");
            }

            Optional<Assignment> op;
            Assignment assignment = null;

            if (assignmentId != null) {
                op = assignmentRepository.findById(assignmentId);
                if (op.isPresent()) {
                    assignment = op.get();
                }
            }

            // 检查作业编号是否已存在
            Optional<Assignment> existingAssignment = assignmentRepository.findByNum(num.trim());
            if (existingAssignment.isPresent()) {
                if (assignment == null
                        || !existingAssignment.get().getAssignmentId().equals(assignment.getAssignmentId())) {
                    return CommonMethod.getReturnMessageError("作业编号已经存在，不能添加或修改！");
                }
            }

            if (assignment == null) {
                assignment = new Assignment();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (publishTime == null || publishTime.trim().isEmpty()) {
                    assignment.setPublishTime(sdf.format(new Date()));
                }
                log.info("创建新作业: {}", title);
            } else {
                log.info("更新作业: {} (ID: {})", title, assignmentId);
            }

            assignment.setNum(num.trim());
            assignment.setTitle(title.trim());
            assignment.setCourseId(courseId);
            assignment.setTeacherId(teacherId);
            if (publishTime != null && !publishTime.trim().isEmpty()) {
                assignment.setPublishTime(publishTime.trim());
            }
            assignment.setDeadline(deadline != null ? deadline.trim() : "");
            assignment.setContent(content != null ? content.trim() : "");
            assignment.setRequirements(requirements != null ? requirements.trim() : "");
            assignment.setTotalScore(totalScore != null ? totalScore : 100);
            assignment.setStatus(status != null ? status.trim() : "已发布");
            assignment.setSubmissionType(submissionType != null ? submissionType.trim() : "在线提交");

            assignmentRepository.save(assignment);
            log.info("作业保存成功: {} (ID: {})", assignment.getTitle(), assignment.getAssignmentId());

            return CommonMethod.getReturnData(assignment.getAssignmentId());
        } catch (Exception e) {
            log.error("保存作业失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("保存作业失败: " + e.getMessage());
        }
    }

    /**
     * 删除作业
     */
    public DataResponse assignmentDelete(DataRequest dataRequest) {
        try {
            Integer assignmentId = dataRequest.getInteger("assignmentId");

            if (assignmentId == null) {
                return CommonMethod.getReturnMessageError("作业ID不能为空");
            }

            Optional<Assignment> op = assignmentRepository.findById(assignmentId);
            if (op.isPresent()) {
                Assignment assignment = op.get();
                assignmentRepository.delete(assignment);
                log.info("作业删除成功: {} (ID: {})", assignment.getTitle(), assignmentId);
            } else {
                return CommonMethod.getReturnMessageError("找不到要删除的作业");
            }

            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            log.error("删除作业失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("删除作业失败: " + e.getMessage());
        }
    }

    /**
     * 导出作业列表到Excel
     */
    public ResponseEntity<StreamingResponseBody> getAssignmentListExcel(DataRequest dataRequest) {
        String numTitle = dataRequest.getString("numTitle");
        List<Map<String, Object>> list = getAssignmentMapList(numTitle);

        Integer[] widths = { 8, 15, 30, 15, 20, 20, 40, 30, 10, 15, 15 };
        String[] titles = { "序号", "作业编号", "作业标题", "所属课程", "发布时间", "截止时间", "作业内容", "作业要求", "总分", "状态", "提交方式" };
        String outputSheetName = "assignment.xlsx";

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFSheet sheet = wb.createSheet(outputSheetName);

        // 设置列宽
        for (int j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }

        // 创建标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell[] cell = new XSSFCell[widths.length];
        for (int j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
        }

        // 填充数据
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }

                Map<String, Object> m = list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "title"));
                cell[3].setCellValue(CommonMethod.getString(m, "courseName"));
                cell[4].setCellValue(CommonMethod.getString(m, "publishTime"));
                cell[5].setCellValue(CommonMethod.getString(m, "deadline"));
                cell[6].setCellValue(CommonMethod.getString(m, "content"));
                cell[7].setCellValue(CommonMethod.getString(m, "requirements"));
                cell[8].setCellValue(CommonMethod.getString(m, "totalScore"));
                cell[9].setCellValue(CommonMethod.getString(m, "status"));
                cell[10].setCellValue(CommonMethod.getString(m, "submissionType"));
            }
        }

        try {
            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取作业Map列表
     */
    private List<Map<String, Object>> getAssignmentMapList(String numTitle) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Assignment> assignmentList;

        if (numTitle == null || numTitle.isEmpty()) {
            assignmentList = assignmentRepository.findAllOrderByPublishTimeDesc();
        } else {
            assignmentList = assignmentRepository.findAssignmentListByNumTitle(numTitle);
        }

        if (assignmentList == null || assignmentList.isEmpty())
            return dataList;

        for (Assignment assignment : assignmentList) {
            dataList.add(getMapFromAssignment(assignment));
        }

        return dataList;
    }

    /**
     * 获取所有作业列表（用于其他模块选择）
     */
    public DataResponse getAllAssignments(DataRequest dataRequest) {
        try {
            List<Assignment> assignments = assignmentRepository.findAll();
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Assignment assignment : assignments) {
                Map<String, Object> m = new HashMap<>();
                m.put("value", assignment.getAssignmentId());
                m.put("title", assignment.getTitle() + " (" + assignment.getNum() + ")");
                dataList.add(m);
            }

            return CommonMethod.getReturnData(dataList);
        } catch (Exception e) {
            log.error("获取作业列表失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("获取作业列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程列表（用于作业管理中选择课程）
     */
    public DataResponse getCourseList(DataRequest dataRequest) {
        try {
            List<Course> courses = courseRepository.findAll();
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Course course : courses) {
                Map<String, Object> m = new HashMap<>();
                m.put("value", course.getCourseId());
                m.put("title", course.getName() + " (" + course.getNum() + ")");
                dataList.add(m);
            }

            return CommonMethod.getReturnData(dataList);
        } catch (Exception e) {
            log.error("获取课程列表失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("获取课程列表失败: " + e.getMessage());
        }
    }
}