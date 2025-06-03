package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import cn.edu.sdu.java.server.repositorys.TechnologyRepository;
import cn.edu.sdu.java.server.models.Technology;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TechnologyService {
    private static final Logger log = LoggerFactory.getLogger(TechnologyService.class);
    private final TechnologyRepository technologyRepository;

    public TechnologyService(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    /**
     * 将Technology对象转换为Map
     */
    public Map<String, Object> getMapFromTechnology(Technology technology) {
        Map<String, Object> m = new HashMap<>();
        if (technology == null)
            return m;

        m.put("technologyId", technology.getTechnologyId());
        m.put("num", technology.getNum());
        m.put("name", technology.getName());
        m.put("type", technology.getType());
//        m.put("startTime", competition.getStartTime());
//        m.put("endTime", competition.getEndTime());
//        m.put("registrationDeadline", competition.getRegistrationDeadline());
//        m.put("location", competition.getLocation());
//        m.put("organizer", competition.getOrganizer());
//        m.put("awards", competition.getAwards());
//        m.put("requirements", competition.getRequirements());
        m.put("description", technology.getDescription());
//        m.put("status", competition.getStatus());
//        m.put("maxParticipants", competition.getMaxParticipants());
        m.put("createTime", technology.getCreateTime());

        return m;
    }


    /**
     * 获取科技成果列表
     */
    public DataResponse getTechnologyList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null)
            numName = "";

        List<Technology> technologyList;
        if (numName.isEmpty()) {
            technologyList = technologyRepository.findAllOrderByCreateTimeDesc();
        } else {
            technologyList = technologyRepository.findTechnologyListByNumName(numName);
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Technology technology : technologyList) {
            dataList.add(getMapFromTechnology(technology));
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取科技成果详细信息
     */
    public DataResponse getTechnologyInfo(DataRequest dataRequest) {
        Integer technologyId = dataRequest.getInteger("technologyId");
        Technology technology = null;

        if (technologyId != null) {
            Optional<Technology> op = technologyRepository.findById(technologyId);
            if (op.isPresent()) {
                technology = op.get();
            }
        }

        return CommonMethod.getReturnData(getMapFromTechnology(technology));
    }

    /**
     * 保存竞赛信息
     */
    public DataResponse technologySave(DataRequest dataRequest) {
        try {
            Integer technologyId = dataRequest.getInteger("technologyId");
            Map<String, Object> form = dataRequest.getMap("form");

            if (form == null) {
                return CommonMethod.getReturnMessageError("表单数据不能为空");
            }

            String num = CommonMethod.getString(form, "num");
            String name = CommonMethod.getString(form, "name");
            String type = CommonMethod.getString(form, "type");
//            String startTime = CommonMethod.getString(form, "startTime");
//            String endTime = CommonMethod.getString(form, "endTime");
//            String registrationDeadline = CommonMethod.getString(form, "registrationDeadline");
//            String location = CommonMethod.getString(form, "location");
//            String organizer = CommonMethod.getString(form, "organizer");
//            String awards = CommonMethod.getString(form, "awards");
//            String requirements = CommonMethod.getString(form, "requirements");
            String description = CommonMethod.getString(form, "description");
//            String status = CommonMethod.getString(form, "status");
//            Integer maxParticipants = CommonMethod.getInteger(form, "maxParticipants");

            // 数据验证
            if (num == null || num.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("科技成果编号不能为空");
            }
            if (name == null || name.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("科技成果名称不能为空");
            }

            Optional<Technology> op;
            Technology technology = null;

            if (technologyId != null) {
                op = technologyRepository.findById(technologyId);
                if (op.isPresent()) {
                    technology = op.get();
                }
            }

            // 检查编号是否已存在（排除当前编辑的科技成果）
            Optional<Technology> existingTechnology = technologyRepository.findByNum(num.trim());
            if (existingTechnology.isPresent()) {
                if (technology == null
                        || !existingTechnology.get().getTechnologyId().equals(technology.getTechnologyId())) {
                    return CommonMethod.getReturnMessageError("科技成果编号已经存在，不能添加或修改！");
                }
            }

            if (technology == null) {
                technology = new Technology();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                technology.setCreateTime(sdf.format(new Date()));
                log.info("创建新科技成果: {}", name);
            } else {
                log.info("更新科技成果: {} (ID: {})", name, technologyId);
            }

            technology.setNum(num.trim());
            technology.setName(name.trim());
            technology.setType(type != null ? type.trim() : "");
//            competition.setStartTime(startTime != null ? startTime.trim() : "");
//            competition.setEndTime(endTime != null ? endTime.trim() : "");
//            competition.setRegistrationDeadline(registrationDeadline != null ? registrationDeadline.trim() : "");
//            competition.setLocation(location != null ? location.trim() : "");
//            competition.setOrganizer(organizer != null ? organizer.trim() : "");
//            competition.setAwards(awards != null ? awards.trim() : "");
//            competition.setRequirements(requirements != null ? requirements.trim() : "");
            technology.setDescription(description != null ? description.trim() : "");
//            competition.setStatus(status != null ? status.trim() : "未开始");
//            competition.setMaxParticipants(maxParticipants);

            technologyRepository.save(technology);
            log.info("科技成果保存成功: {} (ID: {})", technology.getName(), technology.getTechnologyId());

            return CommonMethod.getReturnData(technology.getTechnologyId());
        } catch (Exception e) {
            log.error("保存科技成果失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("保存科技成果失败: " + e.getMessage());
        }
    }

    /**
     * 删除科技成果
     */
    public DataResponse technologyDelete(DataRequest dataRequest) {
        try {
            Integer technologyId = dataRequest.getInteger("technologyId");

            if (technologyId == null) {
                return CommonMethod.getReturnMessageError("竞赛ID不能为空");
            }

            Optional<Technology> op = technologyRepository.findById(technologyId);
            if (op.isPresent()) {
                Technology technology = op.get();
                technologyRepository.delete(technology);
                log.info("科技成果删除成功: {} (ID: {})", technology.getName(), technologyId);
            } else {
                return CommonMethod.getReturnMessageError("找不到要删除的科技成果");
            }

            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            log.error("删除科技成果失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("删除科技成果失败: " + e.getMessage());
        }
    }

    /**
     * 导出竞赛列表到Excel
     */
    /*public ResponseEntity<StreamingResponseBody> getTechnologyListExcel(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getTechnologyMapList(numName);

        Integer[] widths = { 8, 15, 20, 15, 20, 20, 20, 20, 15, 30, 30, 50, 15, 15 };
        String[] titles = { "序号", "科技成果编号", "科技成果名称", "科技成果类型",  "科技成果简介",};
        String outputSheetName = "technology.xlsx";

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
                cell[3].setCellValue(CommonMethod.getString(m, "type"));
                cell[4].setCellValue(CommonMethod.getString(m, "description"));
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
*/
    /**
     * 获取竞赛Map列表
     */
    private List<Map<String, Object>> getTechnologyMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Technology> technologyList;

        if (numName == null || numName.isEmpty()) {
            technologyList = technologyRepository.findAllOrderByCreateTimeDesc();
        } else {
            technologyList = technologyRepository.findTechnologyListByNumName(numName);
        }

        if (technologyList == null || technologyList.isEmpty())
            return dataList;

        for (Technology technology : technologyList) {
            dataList.add(getMapFromTechnology(technology));
        }

        return dataList;
    }

    /**
     * 获取所有竞赛列表（用于其他模块选择）
     */
    public DataResponse getAllTechnologies(DataRequest dataRequest) {
        try {
            List<Technology> technologies = technologyRepository.findAll();
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Technology technology : technologies) {
                Map<String, Object> m = new HashMap<>();
                m.put("value", technology.getTechnologyId());
                m.put("title", technology.getName() + " (" + technology.getNum() + ")");
                dataList.add(m);
            }

            return CommonMethod.getReturnData(dataList);
        } catch (Exception e) {
            log.error("获取科技成果列表失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("获取列表科技成果失败: " + e.getMessage());
        }
    }


}
