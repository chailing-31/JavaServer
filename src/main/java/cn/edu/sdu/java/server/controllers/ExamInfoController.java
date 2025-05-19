package cn.edu.sdu.java.server.controllers;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.ExamInfoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/examInfo")
public class ExamInfoController {
    private final ExamInfoService examInfoService;
    public ExamInfoController(ExamInfoService examInfoService) {
        this.examInfoService = examInfoService;
    }
    @PostMapping("/getStudentItemOptionList")
    public OptionItemList getStudentItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return examInfoService.getStudentItemOptionList(dataRequest);
    }

    @PostMapping("/getCourseItemOptionList")
    public OptionItemList getCourseItemOptionList(@Valid @RequestBody DataRequest dataRequest) {
        return examInfoService.getCourseItemOptionList(dataRequest);
    }

    @PostMapping("/getExamInfoList")
    public DataResponse getExamInfoList(@Valid @RequestBody DataRequest dataRequest) {
        return examInfoService.getExamInfoList(dataRequest);
    }
    @PostMapping("/examInfoSave")
    public DataResponse examInfoSave(@Valid @RequestBody DataRequest dataRequest) {
        return examInfoService.examInfoSave(dataRequest);
    }
    @PostMapping("/examInfoDelete")
    public DataResponse examInfoDelete(@Valid @RequestBody DataRequest dataRequest) {
        return examInfoService.examInfoDelete(dataRequest);
    }

}


