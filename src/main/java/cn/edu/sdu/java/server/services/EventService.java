package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Event;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.EventRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public DataResponse getEventList(DataRequest dataRequest) {
        String keyword = dataRequest.getString("keyword");
        String type = dataRequest.getString("type");
        String startStartStr = dataRequest.getString("startStart");
        String endStartStr = dataRequest.getString("endStart");
        return CommonMethod.getReturnMessageOK();
    }

    // 新增/修改活动
    public DataResponse eventSave(DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        String activityName = dataRequest.getString("activityName");
        String activityType = dataRequest.getString("activityType");
        String location = dataRequest.getString("location");
        String startTimeStr = dataRequest.getString("startTime");
        String endTimeStr = dataRequest.getString("endTime");
        String description = dataRequest.getString("description");
        Integer maxParticipants = dataRequest.getInteger("maxParticipants");
        return CommonMethod.getReturnMessageOK();
    }

    // 删除活动
    public DataResponse eventDelete(DataRequest dataRequest) {
        Integer eventId = dataRequest.getInteger("eventId");
        if(eventId != null) {
            eventRepository.deleteById(eventId);
        }
        return CommonMethod.getReturnMessageOK();
    }

    // 报名功能
    public DataResponse joinEvent(DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Integer userId = dataRequest.getInteger("userId");

        Optional<Event> activityOp = eventRepository.findById(activityId);
        if(!activityOp.isPresent()) {
            return CommonMethod.getReturnMessageError("活动不存在");
        }

        Event event = activityOp.get();
        if(event.getCurrentParticipants() >= event.getMaxParticipants()) {
            return CommonMethod.getReturnMessageError("活动人数已满");
        }

        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);
        return CommonMethod.getReturnMessageOK();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        if(dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
    }

    private List<Map<String, Object>> convertToMapList(List<Event> activities) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Event a : activities) {
            Map<String, Object> map = new HashMap<>();
            map.put("eventId", a.getEventId());
            map.put("eventName", a.getEventName());
            map.put("startTime", a.getStartTime().toString());
            map.put("endTime", a.getEndTime().toString());
            map.put("currentParticipants", a.getCurrentParticipants());
            map.put("maxParticipants", a.getMaxParticipants());
            result.add(map);
        }
        return result;
    }

}