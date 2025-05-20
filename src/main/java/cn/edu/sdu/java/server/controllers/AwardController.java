package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.AwardService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/award")
public class AwardController {

    private final AwardService awardService;

    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    @PostMapping("/getList")
    public DataResponse getList(@Valid @RequestBody DataRequest dataRequest) {
        return awardService.getAwardList(dataRequest);
    }

    @PostMapping("/save")
    public DataResponse save(@Valid @RequestBody DataRequest dataRequest) {
        return awardService.awardSave(dataRequest);
    }

    @PostMapping("/delete")
    public DataResponse delete(@Valid @RequestBody DataRequest dataRequest) {
        return awardService.awardDelete(dataRequest);
    }
}