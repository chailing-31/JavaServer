package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * 获取课程列表
     */
    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseList(dataRequest);
    }

    /**
     * 获取课程详细信息
     */
    @PostMapping("/getCourseInfo")
    public DataResponse getCourseInfo(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseInfo(dataRequest);
    }

    /**
     * 保存课程信息
     */
    @PostMapping("/courseEditSave")
    public DataResponse courseEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseSave(dataRequest);
    }

    /**
     * 保存课程信息（兼容旧版本）
     */
    @PostMapping("/courseSave")
    public DataResponse courseSave(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseSave(dataRequest);
    }

    /**
     * 删除课程
     */
    @PostMapping("/courseDelete")
    public DataResponse courseDelete(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.courseDelete(dataRequest);
    }

    /**
     * 导出课程列表Excel
     */
    @PostMapping("/getCourseListExcel")
    public ResponseEntity<StreamingResponseBody> getCourseListExcel(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getCourseListExcel(dataRequest);
    }

    /**
     * 获取所有课程列表（用于前序课程选择）
     */
    @PostMapping("/getAllCourses")
    public DataResponse getAllCourses(@Valid @RequestBody DataRequest dataRequest) {
        return courseService.getAllCourses(dataRequest);
    }
}
