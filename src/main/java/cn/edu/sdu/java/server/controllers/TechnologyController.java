package cn.edu.sdu.java.server.controllers;

import jakarta.validation.Valid;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TechnologyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/technology")

public class TechnologyController {
    private final TechnologyService technologyService;

    public TechnologyController(TechnologyService technologyService) {
        this.technologyService = technologyService;
    }

    /**
     * 获取竞赛列表
     */
    @PostMapping("/getTechnologyList")
    public DataResponse getTechnologyList(@Valid @RequestBody DataRequest dataRequest) {
        return technologyService.getTechnologyList(dataRequest);
    }

    /**
     * 获取竞赛详细信息
     */
    @PostMapping("/getTechnologyInfo")
    public DataResponse getTechnologyInfo(@Valid @RequestBody DataRequest dataRequest) {
        return technologyService.getTechnologyInfo(dataRequest);
    }

    /**
     * 保存竞赛信息
     */
    @PostMapping("/technologyEditSave")
    public DataResponse technologyEditSave(@Valid @RequestBody DataRequest dataRequest) {
        return technologyService.technologySave(dataRequest);
    }

    /**
     * 保存竞赛信息（兼容旧版本）
     */
    @PostMapping("/technologySave")
    public DataResponse technologySave(@Valid @RequestBody DataRequest dataRequest) {
        return technologyService.technologySave(dataRequest);
    }

    /**
     * 删除竞赛
     */
    @PostMapping("/technologyDelete")
    public DataResponse technologyDelete(@Valid @RequestBody DataRequest dataRequest) {
        return technologyService.technologyDelete(dataRequest);
    }

    /**
     * 获取所有竞赛列表（用于其他模块选择）
     */
    @PostMapping("/getAllTechnology")
    public DataResponse getAllTechnologies(@Valid @RequestBody DataRequest dataRequest) {
        return technologyService.getAllTechnologies(dataRequest);

    }

    /**
     * 导出竞赛列表Excel
     */
/*    @PostMapping("/getTechnologyListExcel")
    public ResponseEntity<StreamingResponseBody> getTechnologyListExcel(@Valid @RequestBody DataRequest dataRequest) {
        return technologyService.getTechnologyListExcel(dataRequest);
    }*/
}




