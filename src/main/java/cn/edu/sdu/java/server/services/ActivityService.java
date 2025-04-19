package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public DataResponse getActivityList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Activity> cList = activityRepository.findActivityListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        Activity pa;
        for (Activity c : cList) {
            m = new HashMap<>();
            m.put("activityId", c.getActivityId()+"");
            m.put("num",c.getNum());
            m.put("name",c.getName());
            m.put("time",c.getTime()+"");
            m.put("duration",c.getDuration());
            m.put("activityPath",c.getActivityPath());
            pa =c.getPreActivity();
            if(pa != null) {
                m.put("preActivity",pa.getName());
                m.put("preActivityId",pa.getActivityId());
            }
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse activitySave(DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        String num = dataRequest.getString("num");
        String name = dataRequest.getString("name");
        String time = dataRequest.getString("time");
        String activityPath = dataRequest.getString("activityPath");
        Integer duration = dataRequest.getInteger("duration");
        Integer preActivityId = dataRequest.getInteger("preActivityId");
        Optional<Activity> op;
        Activity c= null;

        if(activityId != null) {
            op = activityRepository.findById(activityId);
            if(op.isPresent())
                c= op.get();
        }
        if(c== null)
            c = new Activity();
        Activity pa =null;
        if(preActivityId != null) {
            op = activityRepository.findById(preActivityId);
            if(op.isPresent())
                pa = op.get();
        }
        c.setNum(num);
        c.setName(name);
        c.setTime(time);
        c.setDuration(duration);
        c.setActivityPath(activityPath);
        c.setPreActivity(pa);
        activityRepository.save(c);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse activityDelete(DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        Optional<Activity> op;
        Activity c= null;
        if(activityId != null) {
            op = activityRepository.findById(activityId);
            if(op.isPresent()) {
                c = op.get();
                activityRepository.delete(c);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
