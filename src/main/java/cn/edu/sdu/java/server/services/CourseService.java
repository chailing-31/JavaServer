package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@Service
public class CourseService {
    private static final Logger log = LoggerFactory.getLogger(CourseService.class);
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * 将Course对象转换为Map
     */
    public Map<String, Object> getMapFromCourse(Course course) {
        Map<String, Object> m = new HashMap<>();
        if (course == null)
            return m;

        m.put("courseId", course.getCourseId());
        m.put("num", course.getNum());
        m.put("name", course.getName());
        Integer credit = course.getCredit();
        m.put("credit", credit != null ? credit.toString() : "0");
        m.put("coursePath", course.getCoursePath());

        Course preCourse = course.getPreCourse();
        if (preCourse != null) {
            m.put("preCourse", preCourse.getName());
            m.put("preCourseId", preCourse.getCourseId());
            m.put("preCourseNum", preCourse.getNum());
        } else {
            m.put("preCourse", "");
            m.put("preCourseId", null);
            m.put("preCourseNum", "");
        }

        return m;
    }

    /**
     * 获取课程列表
     */
    public DataResponse getCourseList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null)
            numName = "";

        List<Course> cList = courseRepository.findCourseListByNumName(numName);
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (Course c : cList) {
            dataList.add(getMapFromCourse(c));
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取课程详细信息
     */
    public DataResponse getCourseInfo(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        Course course = null;

        if (courseId != null) {
            Optional<Course> op = courseRepository.findById(courseId);
            if (op.isPresent()) {
                course = op.get();
            }
        }

        return CommonMethod.getReturnData(getMapFromCourse(course));
    }

    /**
     * 保存课程信息
     */
    public DataResponse courseSave(DataRequest dataRequest) {
        try {
            Integer courseId = dataRequest.getInteger("courseId");
            Map<String, Object> form = dataRequest.getMap("form");

            if (form == null) {
                // 兼容旧版本API调用方式
                String num = dataRequest.getString("num");
                String name = dataRequest.getString("name");
                String coursePath = dataRequest.getString("coursePath");
                Integer credit = dataRequest.getInteger("credit");
                Integer preCourseId = dataRequest.getInteger("preCourseId");

                form = new HashMap<>();
                form.put("num", num);
                form.put("name", name);
                form.put("coursePath", coursePath);
                form.put("credit", credit);
                form.put("preCourseId", preCourseId);
            }

            String num = CommonMethod.getString(form, "num");
            String name = CommonMethod.getString(form, "name");
            String coursePath = CommonMethod.getString(form, "coursePath");
            Integer credit = CommonMethod.getInteger(form, "credit");
            Integer preCourseId = CommonMethod.getInteger(form, "preCourseId");

            // 数据验证
            if (num == null || num.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("课程编号不能为空");
            }
            if (name == null || name.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("课程名称不能为空");
            }
            if (credit == null || credit < 0) {
                return CommonMethod.getReturnMessageError("学分必须是非负整数");
            }
            if (credit > 20) {
                return CommonMethod.getReturnMessageError("学分不能超过20");
            }

            Optional<Course> op;
            Course course = null;

            if (courseId != null) {
                op = courseRepository.findById(courseId);
                if (op.isPresent())
                    course = op.get();
            }

            // 检查课程编号是否已存在（排除当前编辑的课程）
            Optional<Course> existingCourse = courseRepository.findByNum(num.trim());
            if (existingCourse.isPresent()) {
                if (course == null || !existingCourse.get().getCourseId().equals(course.getCourseId())) {
                    return CommonMethod.getReturnMessageError("课程编号已经存在，不能添加或修改！");
                }
            }

            if (course == null) {
                course = new Course();
                log.info("创建新课程: {}", name);
            } else {
                log.info("更新课程: {} (ID: {})", name, courseId);
            }

            // 处理前序课程，避免循环依赖
            Course preCourse = null;
            if (preCourseId != null) {
                if (courseId != null && preCourseId.equals(courseId)) {
                    return CommonMethod.getReturnMessageError("课程不能将自己设置为前序课程");
                }

                op = courseRepository.findById(preCourseId);
                if (op.isPresent()) {
                    preCourse = op.get();

                    // 检查是否会形成循环依赖
                    if (wouldCreateCircularDependency(course, preCourse)) {
                        return CommonMethod.getReturnMessageError("设置前序课程会形成循环依赖，不允许此操作");
                    }
                }
            }

            course.setNum(num.trim());
            course.setName(name.trim());
            course.setCredit(credit);
            course.setCoursePath(coursePath);
            course.setPreCourse(preCourse);

            courseRepository.save(course);
            log.info("课程保存成功: {} (ID: {})", course.getName(), course.getCourseId());

            return CommonMethod.getReturnData(course.getCourseId());
        } catch (Exception e) {
            log.error("保存课程失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("保存课程失败: " + e.getMessage());
        }
    }

    /**
     * 检查是否会形成循环依赖
     */
    private boolean wouldCreateCircularDependency(Course currentCourse, Course preCourse) {
        if (currentCourse == null || preCourse == null) {
            return false;
        }

        // 使用深度优先搜索检查循环依赖
        Set<Integer> visited = new HashSet<>();
        return hasCircularDependency(preCourse, currentCourse.getCourseId(), visited);
    }

    private boolean hasCircularDependency(Course course, Integer targetCourseId, Set<Integer> visited) {
        if (course == null) {
            return false;
        }

        Integer courseId = course.getCourseId();
        if (courseId.equals(targetCourseId)) {
            return true;
        }

        if (visited.contains(courseId)) {
            return false; // 已访问过，避免无限循环
        }

        visited.add(courseId);

        Course preCourse = course.getPreCourse();
        return hasCircularDependency(preCourse, targetCourseId, visited);
    }

    /**
     * 删除课程
     */
    public DataResponse courseDelete(DataRequest dataRequest) {
        try {
            Integer courseId = dataRequest.getInteger("courseId");

            if (courseId == null) {
                return CommonMethod.getReturnMessageError("课程ID不能为空");
            }

            Optional<Course> op = courseRepository.findById(courseId);
            if (op.isPresent()) {
                Course course = op.get();

                // 检查是否有其他课程依赖于这个课程作为前序课程
                List<Course> dependentCourses = courseRepository.findAll();
                for (Course c : dependentCourses) {
                    if (c.getPreCourse() != null && c.getPreCourse().getCourseId().equals(courseId)) {
                        return CommonMethod.getReturnMessageError(
                                "无法删除课程：课程「" + course.getName() + "」被课程「" + c.getName() + "」引用为前序课程");
                    }
                }

                courseRepository.delete(course);
                log.info("课程删除成功: {} (ID: {})", course.getName(), courseId);
            } else {
                return CommonMethod.getReturnMessageError("找不到要删除的课程");
            }

            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            log.error("删除课程失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("删除课程失败: " + e.getMessage());
        }
    }

    /**
     * 导出课程列表到Excel
     */
    public ResponseEntity<StreamingResponseBody> getCourseListExcel(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getCourseMapList(numName);

        Integer[] widths = { 8, 15, 20, 10, 20, 15 };
        String[] titles = { "序号", "课程编号", "课程名称", "学分", "前序课程", "课程路径" };
        String outputSheetName = "course.xlsx";

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
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "credit"));
                cell[4].setCellValue(CommonMethod.getString(m, "preCourse"));
                cell[5].setCellValue(CommonMethod.getString(m, "coursePath"));
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
     * 获取课程Map列表
     */
    private List<Map<String, Object>> getCourseMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Course> sList = courseRepository.findCourseListByNumName(numName);

        if (sList == null || sList.isEmpty())
            return dataList;

        for (Course course : sList) {
            dataList.add(getMapFromCourse(course));
        }

        return dataList;
    }

    /**
     * 获取所有课程列表（用于前序课程选择）
     */
    public DataResponse getAllCourses(DataRequest dataRequest) {
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
