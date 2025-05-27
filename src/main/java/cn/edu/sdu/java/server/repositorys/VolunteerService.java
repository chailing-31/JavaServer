package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VolunteerService {
    private final ActivityRepository activityRepository;
    private final VolunteerRepository volunteerRepository;
    private final StudentRepository studentRepository;

    public VolunteerService(ActivityRepository activityRepository, VolunteerRepository volunteerRepository, StudentRepository studentRepository) {
        this.activityRepository = activityRepository;
        this.volunteerRepository = volunteerRepository;
        this.studentRepository = studentRepository;
    }
    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public OptionItemList getActivityItemOptionList(DataRequest dataRequest) {
        List<Activity> sList = activityRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Activity a : sList) {
            itemList.add(new OptionItem(a.getActivityId(),a.getActivityId()+"", a.getNum()+"-"+a.getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getVolunteerList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if(personId == null)
            personId = 0;
        Integer activityId = dataRequest.getInteger("activityId");
        if(activityId == null)
            activityId = 0;
        List<Volunteer> sList = volunteerRepository.findByStudentActivity(personId, activityId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Volunteer s : sList) {
            m = new HashMap<>();
            m.put("volunteerId", s.getVolunteerId()+"");
            m.put("personId",s.getStudent().getPersonId()+"");
            m.put("activityId",s.getActivity().getActivityId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("activityNum",s.getActivity().getNum());
            m.put("activityName",s.getActivity().getName());
            m.put("time",s.getActivity().getTime());
            m.put("duration",""+s.getActivity().getDuration());
            m.put("degree",""+s.getDegree());
            m.put("type",""+s.getType());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse volunteerSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer activityId = dataRequest.getInteger("activityId");
//        Integer degree = dataRequest.getInteger("mark");
        Integer volunteerId = dataRequest.getInteger("volunteerId");
        Optional<Volunteer> op;
        Volunteer s = null;
        if(volunteerId != null) {
            op= volunteerRepository.findById(volunteerId);
            if(op.isPresent())
                s = op.get();
        }
        if(s == null) {
            s = new Volunteer();
            s.setStudent(studentRepository.findById(personId).get());
            s.setActivity(activityRepository.findById(activityId).get());
        }
//        s.setMark(mark);
        volunteerRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse volunteerDelete(DataRequest dataRequest) {
        Integer volunteerId = dataRequest.getInteger("volunteerId");
        Optional<Volunteer> op;
        Volunteer s = null;
        if(volunteerId != null) {
            op= volunteerRepository.findById(volunteerId);
            if(op.isPresent()) {
                s = op.get();
                volunteerRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

}
