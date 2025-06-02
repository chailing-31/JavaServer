package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.TeacherService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * 获取教师列表
     */
    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherList(dataRequest);
    }


    @PostMapping("/getTeacherOptionItemList")
    public OptionItemList  getTeacherOptionItemList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherOptionItemList(dataRequest);
    }

    /**
     * 获取教师详细信息
     */
    @PostMapping("/getTeacherInfo")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherInfo(dataRequest);
    }

    /**
     * 保存教师信息
     */
    @PostMapping("/teacherEditSave")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherSave(dataRequest);
    }

    /**
     * 保存教师信息（兼容旧版本）
     */
    @PostMapping("/teacherSave")
    public DataResponse teacherSave(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherSave(dataRequest);
    }

    /**
     * 删除教师
     */
    @PostMapping("/teacherDelete")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherDelete(dataRequest);
    }

    /**
     * 导出教师列表Excel
     */
    @PostMapping("/getTeacherListExcel")
    public ResponseEntity<StreamingResponseBody> getTeacherListExcel(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherListExcel(dataRequest);
    }

    /**
     * 获取所有教师列表（用于其他模块选择教师）
     */
    @PostMapping("/getAllTeachers")
    public DataResponse getAllTeachers(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getAllTeachers(dataRequest);
    }
}