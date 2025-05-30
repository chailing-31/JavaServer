package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Competition;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CompetitionRepository;
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
public class CompetitionService {
    private static final Logger log = LoggerFactory.getLogger(CompetitionService.class);
    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    /**
     * 将Competition对象转换为Map
     */
    public Map<String, Object> getMapFromCompetition(Competition competition) {
        Map<String, Object> m = new HashMap<>();
        if (competition == null)
            return m;

        m.put("competitionId", competition.getCompetitionId());
        m.put("num", competition.getNum());
        m.put("name", competition.getName());
        m.put("type", competition.getType());
        m.put("startTime", competition.getStartTime());
        m.put("endTime", competition.getEndTime());
        m.put("registrationDeadline", competition.getRegistrationDeadline());
        m.put("location", competition.getLocation());
        m.put("organizer", competition.getOrganizer());
        m.put("awards", competition.getAwards());
        m.put("requirements", competition.getRequirements());
        m.put("description", competition.getDescription());
        m.put("status", competition.getStatus());
        m.put("maxParticipants", competition.getMaxParticipants());
        m.put("createTime", competition.getCreateTime());

        return m;
    }

    /**
     * 获取竞赛列表
     */
    public DataResponse getCompetitionList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if (numName == null)
            numName = "";

        List<Competition> competitionList;
        if (numName.isEmpty()) {
            competitionList = competitionRepository.findAllOrderByCreateTimeDesc();
        } else {
            competitionList = competitionRepository.findCompetitionListByNumName(numName);
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Competition competition : competitionList) {
            dataList.add(getMapFromCompetition(competition));
        }

        return CommonMethod.getReturnData(dataList);
    }

    /**
     * 获取竞赛详细信息
     */
    public DataResponse getCompetitionInfo(DataRequest dataRequest) {
        Integer competitionId = dataRequest.getInteger("competitionId");
        Competition competition = null;

        if (competitionId != null) {
            Optional<Competition> op = competitionRepository.findById(competitionId);
            if (op.isPresent()) {
                competition = op.get();
            }
        }

        return CommonMethod.getReturnData(getMapFromCompetition(competition));
    }

    /**
     * 保存竞赛信息
     */
    public DataResponse competitionSave(DataRequest dataRequest) {
        try {
            Integer competitionId = dataRequest.getInteger("competitionId");
            Map<String, Object> form = dataRequest.getMap("form");

            if (form == null) {
                return CommonMethod.getReturnMessageError("表单数据不能为空");
            }

            String num = CommonMethod.getString(form, "num");
            String name = CommonMethod.getString(form, "name");
            String type = CommonMethod.getString(form, "type");
            String startTime = CommonMethod.getString(form, "startTime");
            String endTime = CommonMethod.getString(form, "endTime");
            String registrationDeadline = CommonMethod.getString(form, "registrationDeadline");
            String location = CommonMethod.getString(form, "location");
            String organizer = CommonMethod.getString(form, "organizer");
            String awards = CommonMethod.getString(form, "awards");
            String requirements = CommonMethod.getString(form, "requirements");
            String description = CommonMethod.getString(form, "description");
            String status = CommonMethod.getString(form, "status");
            Integer maxParticipants = CommonMethod.getInteger(form, "maxParticipants");

            // 数据验证
            if (num == null || num.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("竞赛编号不能为空");
            }
            if (name == null || name.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("竞赛名称不能为空");
            }

            Optional<Competition> op;
            Competition competition = null;

            if (competitionId != null) {
                op = competitionRepository.findById(competitionId);
                if (op.isPresent()) {
                    competition = op.get();
                }
            }

            // 检查竞赛编号是否已存在（排除当前编辑的竞赛）
            Optional<Competition> existingCompetition = competitionRepository.findByNum(num.trim());
            if (existingCompetition.isPresent()) {
                if (competition == null
                        || !existingCompetition.get().getCompetitionId().equals(competition.getCompetitionId())) {
                    return CommonMethod.getReturnMessageError("竞赛编号已经存在，不能添加或修改！");
                }
            }

            if (competition == null) {
                competition = new Competition();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                competition.setCreateTime(sdf.format(new Date()));
                log.info("创建新竞赛: {}", name);
            } else {
                log.info("更新竞赛: {} (ID: {})", name, competitionId);
            }

            competition.setNum(num.trim());
            competition.setName(name.trim());
            competition.setType(type != null ? type.trim() : "");
            competition.setStartTime(startTime != null ? startTime.trim() : "");
            competition.setEndTime(endTime != null ? endTime.trim() : "");
            competition.setRegistrationDeadline(registrationDeadline != null ? registrationDeadline.trim() : "");
            competition.setLocation(location != null ? location.trim() : "");
            competition.setOrganizer(organizer != null ? organizer.trim() : "");
            competition.setAwards(awards != null ? awards.trim() : "");
            competition.setRequirements(requirements != null ? requirements.trim() : "");
            competition.setDescription(description != null ? description.trim() : "");
            competition.setStatus(status != null ? status.trim() : "未开始");
            competition.setMaxParticipants(maxParticipants);

            competitionRepository.save(competition);
            log.info("竞赛保存成功: {} (ID: {})", competition.getName(), competition.getCompetitionId());

            return CommonMethod.getReturnData(competition.getCompetitionId());
        } catch (Exception e) {
            log.error("保存竞赛失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("保存竞赛失败: " + e.getMessage());
        }
    }

    /**
     * 删除竞赛
     */
    public DataResponse competitionDelete(DataRequest dataRequest) {
        try {
            Integer competitionId = dataRequest.getInteger("competitionId");

            if (competitionId == null) {
                return CommonMethod.getReturnMessageError("竞赛ID不能为空");
            }

            Optional<Competition> op = competitionRepository.findById(competitionId);
            if (op.isPresent()) {
                Competition competition = op.get();
                competitionRepository.delete(competition);
                log.info("竞赛删除成功: {} (ID: {})", competition.getName(), competitionId);
            } else {
                return CommonMethod.getReturnMessageError("找不到要删除的竞赛");
            }

            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            log.error("删除竞赛失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("删除竞赛失败: " + e.getMessage());
        }
    }

    /**
     * 导出竞赛列表到Excel
     */
    public ResponseEntity<StreamingResponseBody> getCompetitionListExcel(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getCompetitionMapList(numName);

        Integer[] widths = { 8, 15, 20, 15, 20, 20, 20, 20, 15, 30, 30, 50, 15, 15 };
        String[] titles = { "序号", "竞赛编号", "竞赛名称", "竞赛类型", "开始时间", "结束时间", "报名截止", "地点", "主办方", "奖项设置", "参赛要求", "竞赛简介",
                "状态", "最大参赛人数" };
        String outputSheetName = "competition.xlsx";

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
                cell[4].setCellValue(CommonMethod.getString(m, "startTime"));
                cell[5].setCellValue(CommonMethod.getString(m, "endTime"));
                cell[6].setCellValue(CommonMethod.getString(m, "registrationDeadline"));
                cell[7].setCellValue(CommonMethod.getString(m, "location"));
                cell[8].setCellValue(CommonMethod.getString(m, "organizer"));
                cell[9].setCellValue(CommonMethod.getString(m, "awards"));
                cell[10].setCellValue(CommonMethod.getString(m, "requirements"));
                cell[11].setCellValue(CommonMethod.getString(m, "description"));
                cell[12].setCellValue(CommonMethod.getString(m, "status"));
                cell[13].setCellValue(CommonMethod.getString(m, "maxParticipants"));
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
     * 获取竞赛Map列表
     */
    private List<Map<String, Object>> getCompetitionMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Competition> competitionList;

        if (numName == null || numName.isEmpty()) {
            competitionList = competitionRepository.findAllOrderByCreateTimeDesc();
        } else {
            competitionList = competitionRepository.findCompetitionListByNumName(numName);
        }

        if (competitionList == null || competitionList.isEmpty())
            return dataList;

        for (Competition competition : competitionList) {
            dataList.add(getMapFromCompetition(competition));
        }

        return dataList;
    }

    /**
     * 获取所有竞赛列表（用于其他模块选择）
     */
    public DataResponse getAllCompetitions(DataRequest dataRequest) {
        try {
            List<Competition> competitions = competitionRepository.findAll();
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Competition competition : competitions) {
                Map<String, Object> m = new HashMap<>();
                m.put("value", competition.getCompetitionId());
                m.put("title", competition.getName() + " (" + competition.getNum() + ")");
                dataList.add(m);
            }

            return CommonMethod.getReturnData(dataList);
        } catch (Exception e) {
            log.error("获取竞赛列表失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("获取竞赛列表失败: " + e.getMessage());
        }
    }
}