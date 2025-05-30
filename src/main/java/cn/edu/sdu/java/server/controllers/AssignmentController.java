package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    /**
     * 获取作业列表
     */
    @PostMapping("/getAssignmentList")
    public DataResponse getAssignmentList(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.getAssignmentList(dataRequest);
    }

    /**
     * 获取作业详细信息
     */
    @PostMapping("/getAssignmentInfo")
    public DataResponse getAssignmentInfo(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.getAssignmentInfo(dataRequest);
    }

    /**
     * 保存作业信息
     */
    @PostMapping("/assignmentEditSave")
    public DataResponse assignmentEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.assignmentSave(dataRequest);
    }

    /**
     * 保存作业信息（兼容旧版本）
     */
    @PostMapping("/assignmentSave")
    public DataResponse assignmentSave(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.assignmentSave(dataRequest);
    }

    /**
     * 删除作业
     */
    @PostMapping("/assignmentDelete")
    public DataResponse assignmentDelete(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.assignmentDelete(dataRequest);
    }

    /**
     * 导出作业列表Excel
     */
    @PostMapping("/getAssignmentListExcel")
    public ResponseEntity<StreamingResponseBody> getAssignmentListExcel(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.getAssignmentListExcel(dataRequest);
    }

    /**
     * 获取所有作业列表（用于其他模块选择）
     */
    @PostMapping("/getAllAssignments")
    public DataResponse getAllAssignments(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.getAllAssignments(dataRequest);
    }

    /**
     * 获取课程列表（用于作业管理中选择课程）
     */
    @PostMapping("/getCourseList")
    public DataResponse getCourseList(@Valid @RequestBody DataRequest dataRequest) {
        return assignmentService.getCourseList(dataRequest);
    }
}