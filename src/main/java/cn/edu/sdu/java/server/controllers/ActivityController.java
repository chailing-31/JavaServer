package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ActivityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * 获取活动列表
     */
    @PostMapping("/getActivityList")
    public DataResponse getActivityList(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityList(dataRequest);
    }

    /**
     * 获取活动详细信息
     */
    @PostMapping("/getActivityInfo")
    public DataResponse getActivityInfo(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityInfo(dataRequest);
    }

    /**
     * 保存活动信息
     */
    @PostMapping("/activityEditSave")
    public DataResponse activityEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.activitySave(dataRequest);
    }

    /**
     * 保存活动信息（兼容旧版本）
     */
    @PostMapping("/activitySave")
    public DataResponse activitySave(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.activitySave(dataRequest);
    }

    /**
     * 删除活动
     */
    @PostMapping("/activityDelete")
    public DataResponse activityDelete(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.activityDelete(dataRequest);
    }

    /**
     * 导出活动列表Excel
     */
    @PostMapping("/getActivityListExcel")
    public ResponseEntity<StreamingResponseBody> getActivityListExcel(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityListExcel(dataRequest);
    }

    /**
     * 获取所有活动列表（用于前序活动选择）
     */
    @PostMapping("/getAllActivities")
    public DataResponse getAllActivities(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getAllActivities(dataRequest);
    }
}
