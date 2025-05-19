package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.EventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/getList")
    public DataResponse getList(@Valid @RequestBody DataRequest dataRequest) {
        return eventService.getEventList(dataRequest);
    }

    @PostMapping("/save")
    public DataResponse save(@Valid @RequestBody DataRequest dataRequest) {
        return eventService.eventSave(dataRequest);
    }

    @PostMapping("/delete")
    public DataResponse delete(@Valid @RequestBody DataRequest dataRequest) {
        return eventService.eventDelete(dataRequest);
    }

    @PostMapping("/join")
    public DataResponse join(@Valid @RequestBody DataRequest dataRequest) {
        return eventService.joinEvent(dataRequest);
    }
}