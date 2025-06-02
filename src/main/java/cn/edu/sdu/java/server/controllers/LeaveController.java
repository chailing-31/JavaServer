package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.services.LeaveService;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.services.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * LeaveController 请假管理控制器
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private BaseService baseService;

    /**
     * 获取请假列表
     */
    @PostMapping("/getLeaveList")
    public ResponseEntity<DataResponse> getLeaveList(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("numName", requestData.getString("numName"));
            params.put("leaveType", requestData.getString("leaveType"));
            params.put("status", requestData.getString("status"));
            
            List<Map<String, Object>> data = leaveService.getLeaveList(params);
            return ResponseEntity.ok(CommonMethod.getReturnData(data));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("查询请假列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取请假详细信息
     */
    @PostMapping("/getLeaveInfo")
    public ResponseEntity<DataResponse> getLeaveInfo(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Integer leaveId = requestData.getInteger("leaveId");
            if (leaveId == null) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError("请假ID不能为空"));
            }
            Map<String, Object> data = leaveService.getLeaveInfo(leaveId);
            return ResponseEntity.ok(CommonMethod.getReturnData(data));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("获取请假信息失败: " + e.getMessage()));
        }
    }

    /**
     * 保存请假信息
     */
    @PostMapping("/leaveEditSave")
    public ResponseEntity<DataResponse> leaveEditSave(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            // 构造Service层需要的参数格式
            Map<String, Object> serviceParams = new HashMap<>();
            serviceParams.put("leaveId", requestData.getInteger("leaveId"));
            serviceParams.put("form", requestData.getMap("form"));
            
            Map<String, String> result = leaveService.leaveEditSave(serviceParams);
            if ("success".equals(result.get("result"))) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageOK(result.get("message")));
            } else {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError(result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("保存请假信息失败: " + e.getMessage()));
        }
    }

    /**
     * 删除请假记录
     */
    @PostMapping("/leaveDelete")
    public ResponseEntity<DataResponse> leaveDelete(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Integer leaveId = requestData.getInteger("leaveId");
            if (leaveId == null) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError("请假ID不能为空"));
            }
            Map<String, String> result = leaveService.leaveDelete(leaveId);
            if ("success".equals(result.get("result"))) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageOK(result.get("message")));
            } else {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError(result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("删除请假记录失败: " + e.getMessage()));
        }
    }

    /**
     * 批准请假
     */
    @PostMapping("/approveLeave")
    public ResponseEntity<DataResponse> approveLeave(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Integer leaveId = requestData.getInteger("leaveId");
            String comment = requestData.getString("comment");
            if (leaveId == null) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError("请假ID不能为空"));
            }
            Map<String, String> result = leaveService.approveLeave(leaveId, comment);
            if ("success".equals(result.get("result"))) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageOK(result.get("message")));
            } else {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError(result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("批准请假失败: " + e.getMessage()));
        }
    }

    /**
     * 拒绝请假
     */
    @PostMapping("/rejectLeave")
    public ResponseEntity<DataResponse> rejectLeave(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Integer leaveId = requestData.getInteger("leaveId");
            String comment = requestData.getString("comment");
            if (leaveId == null) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError("请假ID不能为空"));
            }
            Map<String, String> result = leaveService.rejectLeave(leaveId, comment);
            if ("success".equals(result.get("result"))) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageOK(result.get("message")));
            } else {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError(result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("拒绝请假失败: " + e.getMessage()));
        }
    }

    /**
     * 导出请假列表Excel
     */
    @PostMapping("/getLeaveListExcel")
    public ResponseEntity<StreamingResponseBody> getLeaveListExcel(@RequestBody Map<String, String> requestData) {
        try {
            byte[] data = leaveService.getLeaveListExcel(requestData);
            if (data != null) {
                return CommonMethod.getByteDataResponseBodyPdf(data);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取学生列表
     */
    @PostMapping("/getStudentList")
    public ResponseEntity<DataResponse> getStudentList(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            List<Map<String, Object>> data = leaveService.getStudentList();
            return ResponseEntity.ok(CommonMethod.getReturnData(data));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("获取学生列表失败: " + e.getMessage()));
        }
    }


    /**
     * 获取指定教师需要审批的请假记录列表
     */
    @PostMapping("/getLeaveListByTeacher")
    public ResponseEntity<DataResponse> getLeaveListByTeacher(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            // 从请求数据中获取教师工号和姓名
            String teacherNum = requestData.getString("teacherNum");
            String teacherName = requestData.getString("teacherName");

            // 参数校验
            if (teacherNum == null || teacherNum.isEmpty() || teacherName == null || teacherName.isEmpty()) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError("教师工号和姓名不能为空"));
            }

            // 调用服务层方法
            List<Map<String, Object>> data = leaveService.getLeaveListByTeacher(teacherNum, teacherName);

            // 返回成功响应
            return ResponseEntity.ok(CommonMethod.getReturnData(data));

        } catch (Exception e) {
            // 异常处理
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("获取教师审批列表失败: " + e.getMessage()));
        }
    }

}