package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@Service
public class ActivityService {
    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * 将Activity对象转换为Map
     */
    public Map<String, Object> getMapFromActivity(Activity activity) {
        Map<String, Object> m = new HashMap<>();
        if (activity == null)
            return m;

        m.put("activityId", activity.getActivityId());
        m.put("num", activity.getNum());
        m.put("name", activity.getName());
        m.put("time", activity.getTime());
        // 确保时长显示为整数字符串，避免小数点问题
        Integer duration = activity.getDuration();
        m.put("duration", duration != null ? duration.toString() : "0");
        m.put("activityPath", activity.getActivityPath());

        Activity preActivity = activity.getPreActivity();
        if (preActivity != null) {
            m.put("preActivity", preActivity.getName());
            m.put("preActivityId", preActivity.getActivityId());
            m.put("preActivityNum", preActivity.getNum());
        } else {
            m.put("preActivity", "");
            m.put("preActivityId", null);
            m.put("preActivityNum", "");
        }

        return m;
    }

    /**
     * 获取活动列表
     */
    public DataResponse getActivityList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null)
            numName = "";

        List<Activity> cList = activityRepository.findActivityListByNumName(numName);
        List<Map<String, Object>> dataList = new ArrayList<>();

        for (Activity c : cList) {
            dataList.add(getMapFromActivity(c));
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取活动详细信息
     */
    public DataResponse getActivityInfo(DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Activity activity = null;

        if (activityId != null) {
            Optional<Activity> op = activityRepository.findById(activityId);
            if (op.isPresent()) {
                activity = op.get();
            }
        }

        return CommonMethod.getReturnData(getMapFromActivity(activity));
    }

    /**
     * 保存活动信息
     */
    public DataResponse activitySave(DataRequest dataRequest) {
        try {
            Integer activityId = dataRequest.getInteger("activityId");
            Map<String, Object> form = dataRequest.getMap("form");

            if (form == null) {
                // 兼容旧版本API调用方式
                String num = dataRequest.getString("num");
                String name = dataRequest.getString("name");
                String time = dataRequest.getString("time");
                String activityPath = dataRequest.getString("activityPath");
                Integer duration = dataRequest.getInteger("duration");
                Integer preActivityId = dataRequest.getInteger("preActivityId");

                form = new HashMap<>();
                form.put("num", num);
                form.put("name", name);
                form.put("time", time);
                form.put("activityPath", activityPath);
                form.put("duration", duration);
                form.put("preActivityId", preActivityId);
            }

            String num = CommonMethod.getString(form, "num");
            String name = CommonMethod.getString(form, "name");
            String time = CommonMethod.getString(form, "time");
            String activityPath = CommonMethod.getString(form, "activityPath");
            Integer duration = CommonMethod.getInteger(form, "duration");
            Integer preActivityId = CommonMethod.getInteger(form, "preActivityId");

            // 数据验证
            if (num == null || num.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("活动编号不能为空");
            }
            if (name == null || name.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("活动名称不能为空");
            }

            Optional<Activity> op;
            Activity activity = null;

            if (activityId != null) {
                op = activityRepository.findById(activityId);
                if (op.isPresent())
                    activity = op.get();
            }

            // 检查活动编号是否已存在（排除当前编辑的活动）
            Optional<Activity> existingActivity = activityRepository.findByNum(num.trim());
            if (existingActivity.isPresent()) {
                if (activity == null || !existingActivity.get().getActivityId().equals(activity.getActivityId())) {
                    return CommonMethod.getReturnMessageError("活动编号已经存在，不能添加或修改！");
                }
            }

            if (activity == null) {
                activity = new Activity();
                log.info("创建新活动: {}", name);
            } else {
                log.info("更新活动: {} (ID: {})", name, activityId);
            }

            Activity preActivity = null;
            if (preActivityId != null) {
                op = activityRepository.findById(preActivityId);
                if (op.isPresent())
                    preActivity = op.get();
            }

            activity.setNum(num.trim());
            activity.setName(name.trim());
            activity.setTime(time);
            activity.setDuration(duration);
            activity.setActivityPath(activityPath);
            activity.setPreActivity(preActivity);

            activityRepository.save(activity);
            log.info("活动保存成功: {} (ID: {})", activity.getName(), activity.getActivityId());

            return CommonMethod.getReturnData(activity.getActivityId());
        } catch (Exception e) {
            log.error("保存活动失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("保存活动失败: " + e.getMessage());
        }
    }

    /**
     * 删除活动
     */
    public DataResponse activityDelete(DataRequest dataRequest) {
        try {
            Integer activityId = dataRequest.getInteger("activityId");

            if (activityId == null) {
                return CommonMethod.getReturnMessageError("活动ID不能为空");
            }

            Optional<Activity> op = activityRepository.findById(activityId);
            if (op.isPresent()) {
                Activity activity = op.get();

                // 检查是否有其他活动依赖于这个活动作为前序活动
                List<Activity> dependentActivities = activityRepository.findAll();
                for (Activity a : dependentActivities) {
                    if (a.getPreActivity() != null && a.getPreActivity().getActivityId().equals(activityId)) {
                        return CommonMethod.getReturnMessageError(
                                "无法删除活动：活动「" + activity.getName() + "」被活动「" + a.getName() + "」引用为前序活动");
                    }
                }

                activityRepository.delete(activity);
                log.info("活动删除成功: {} (ID: {})", activity.getName(), activityId);
            } else {
                return CommonMethod.getReturnMessageError("找不到要删除的活动");
            }

            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            log.error("删除活动失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("删除活动失败: " + e.getMessage());
        }
    }

    /**
     * 导出活动列表到Excel
     */
    public ResponseEntity<StreamingResponseBody> getActivityListExcel(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getActivityMapList(numName);

        Integer[] widths = { 8, 15, 20, 15, 10, 20, 15 };
        String[] titles = { "序号", "活动编号", "活动名称", "活动时间", "时长(小时)", "前序活动", "活动路径" };
        String outputSheetName = "activity.xlsx";

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
                cell[3].setCellValue(CommonMethod.getString(m, "time"));
                cell[4].setCellValue(CommonMethod.getString(m, "duration"));
                cell[5].setCellValue(CommonMethod.getString(m, "preActivity"));
                cell[6].setCellValue(CommonMethod.getString(m, "activityPath"));
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
     * 获取活动Map列表
     */
    private List<Map<String, Object>> getActivityMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Activity> sList = activityRepository.findActivityListByNumName(numName);

        if (sList == null || sList.isEmpty())
            return dataList;

        for (Activity activity : sList) {
            dataList.add(getMapFromActivity(activity));
        }

        return dataList;
    }

    /**
     * 获取所有活动列表（用于前序活动选择）
     */
    public DataResponse getAllActivities(DataRequest dataRequest) {
        try {
            List<Activity> activities = activityRepository.findAll();
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Activity activity : activities) {
                Map<String, Object> m = new HashMap<>();
                m.put("value", activity.getActivityId());
                m.put("title", activity.getName() + " (" + activity.getNum() + ")");
                dataList.add(m);
            }

            return CommonMethod.getReturnData(dataList);
        } catch (Exception e) {
            log.error("获取活动列表失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("获取活动列表失败: " + e.getMessage());
        }
    }
}
