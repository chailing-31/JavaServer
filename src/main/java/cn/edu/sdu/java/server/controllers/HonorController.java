package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.services.HonorService;
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
 * HonorController 荣誉管理控制器
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/honor")
public class HonorController {

    @Autowired
    private HonorService honorService;

    @Autowired
    private BaseService baseService;

    /**
     * 获取荣誉列表
     */
    @PostMapping("/getHonorList")
    public ResponseEntity<DataResponse> getHonorList(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("numName", requestData.getString("numName"));
            params.put("honorType", requestData.getString("honorType"));
            params.put("honorLevel", requestData.getString("honorLevel"));
            params.put("status", requestData.getString("status"));

            List<Map<String, Object>> data = honorService.getHonorList(params);
            return ResponseEntity.ok(CommonMethod.getReturnData(data));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("查询荣誉列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取荣誉详细信息
     */
    @PostMapping("/getHonorInfo")
    public ResponseEntity<DataResponse> getHonorInfo(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Integer honorId = requestData.getInteger("honorId");
            if (honorId == null) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError("荣誉ID不能为空"));
            }
            Map<String, Object> data = honorService.getHonorInfo(honorId);
            return ResponseEntity.ok(CommonMethod.getReturnData(data));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("获取荣誉信息失败: " + e.getMessage()));
        }
    }

    /**
     * 保存荣誉信息
     */
    @PostMapping("/honorEditSave")
    public ResponseEntity<DataResponse> honorEditSave(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            // 构造Service层需要的参数格式
            Map<String, Object> serviceParams = new HashMap<>();
            serviceParams.put("honorId", requestData.getInteger("honorId"));
            serviceParams.put("form", requestData.getMap("form"));

            Map<String, String> result = honorService.honorEditSave(serviceParams);
            if ("success".equals(result.get("result"))) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageOK(result.get("message")));
            } else {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError(result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("保存荣誉信息失败: " + e.getMessage()));
        }
    }

    /**
     * 删除荣誉记录
     */
    @PostMapping("/honorDelete")
    public ResponseEntity<DataResponse> honorDelete(@RequestBody cn.edu.sdu.java.server.payload.request.DataRequest requestData) {
        try {
            Integer honorId = requestData.getInteger("honorId");
            if (honorId == null) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError("荣誉ID不能为空"));
            }
            Map<String, String> result = honorService.honorDelete(honorId);
            if ("success".equals(result.get("result"))) {
                return ResponseEntity.ok(CommonMethod.getReturnMessageOK(result.get("message")));
            } else {
                return ResponseEntity.ok(CommonMethod.getReturnMessageError(result.get("message")));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("删除荣誉记录失败: " + e.getMessage()));
        }
    }

    /**
     * 导出荣誉列表Excel
     */
    @PostMapping("/getHonorListExcel")
    public ResponseEntity<StreamingResponseBody> getHonorListExcel(@RequestBody Map<String, String> requestData) {
        try {
            byte[] data = honorService.getHonorListExcel(requestData);
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
            List<Map<String, Object>> data = honorService.getStudentList();
            return ResponseEntity.ok(CommonMethod.getReturnData(data));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonMethod.getReturnMessageError("获取学生列表失败: " + e.getMessage()));
        }
    }
}