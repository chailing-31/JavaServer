package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.CompetitionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/competition")
public class CompetitionController {
    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    /**
     * 获取竞赛列表
     */
    @PostMapping("/getCompetitionList")
    public DataResponse getCompetitionList(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionList(dataRequest);
    }

    /**
     * 获取竞赛详细信息
     */
    @PostMapping("/getCompetitionInfo")
    public DataResponse getCompetitionInfo(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionInfo(dataRequest);
    }

    /**
     * 保存竞赛信息
     */
    @PostMapping("/competitionEditSave")
    public DataResponse competitionEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionSave(dataRequest);
    }

    /**
     * 保存竞赛信息（兼容旧版本）
     */
    @PostMapping("/competitionSave")
    public DataResponse competitionSave(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionSave(dataRequest);
    }

    /**
     * 删除竞赛
     */
    @PostMapping("/competitionDelete")
    public DataResponse competitionDelete(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.competitionDelete(dataRequest);
    }

    /**
     * 导出竞赛列表Excel
     */
    @PostMapping("/getCompetitionListExcel")
    public ResponseEntity<StreamingResponseBody> getCompetitionListExcel(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getCompetitionListExcel(dataRequest);
    }

    /**
     * 获取所有竞赛列表（用于其他模块选择）
     */
    @PostMapping("/getAllCompetitions")
    public DataResponse getAllCompetitions(@Valid @RequestBody DataRequest dataRequest) {
        return competitionService.getAllCompetitions(dataRequest);
    }
}