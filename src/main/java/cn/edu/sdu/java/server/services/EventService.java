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
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Event> elist = eventRepository.findEventListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Event e : elist) {
            m = new HashMap<>();
            m.put("eventId", e.getEventId()+"");
            m.put("eventName", e.getEventName());
            m.put("eventType",e.getEventType());
            m.put("location",e.getLocation());
            m.put("startTime",e.getStartTime());
            m.put("endTime",e.getEndTime());
            m.put("maxParticipants", e.getMaxParticipants()+"");
            m.put("currentParticipants", e.getCurrentParticipants()+"");
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    // 新增/修改活动
    public DataResponse eventSave(DataRequest dataRequest) {
        Integer eventId = dataRequest.getInteger("eventId");
        String eventName = dataRequest.getString("eventName");
        String eventType = dataRequest.getString("eventType");
        String location = dataRequest.getString("location");
        String startTime = dataRequest.getString("startTime");
        String endTime = dataRequest.getString("endTime");
        Integer maxParticipants = dataRequest.getInteger("maxParticipants");
        Integer currentParticipants = dataRequest.getInteger("currentParticipants");

        Optional<Event> op;
        Event event = null;

        // 检查是否存在现有活动
        if(eventId != null) {
            op = eventRepository.findById(eventId);
            if(op.isPresent())
                event = op.get();
        }

        // 创建新活动对象
        if(event == null)
            event = new Event();



        // 设置属性
        event.setEventId(eventId);
        event.setEventName(eventName);
        event.setEventType(eventType);
        event.setLocation(location);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setMaxParticipants(maxParticipants);
        event.setCurrentParticipants(currentParticipants);


        // 保存到数据库
        eventRepository.save(event);

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
        Integer eventId = dataRequest.getInteger("eventId");

        Optional<Event> eventOp = eventRepository.findById(eventId);
        if(!eventOp.isPresent()) {
            return CommonMethod.getReturnMessageError("活动不存在");
        }

        Event event = eventOp.get();
        if(event.getCurrentParticipants() >= event.getMaxParticipants()) {
            return CommonMethod.getReturnMessageError("活动人数已满");
        }

        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);
        return CommonMethod.getReturnMessageOK();
    }


}