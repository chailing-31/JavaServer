package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Award;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.AwardRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AwardService {

    private final AwardRepository awardRepository;
    private final StudentRepository studentRepository;

    public AwardService(AwardRepository awardRepository, StudentRepository studentRepository) {
        this.awardRepository = awardRepository;
        this.studentRepository = studentRepository;
    }

    public DataResponse getAwardList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        if(numName == null)
            numName = "";
        List<Award> aList = awardRepository.findAwardListByNumName(numName);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Award a : aList) {
            m = new HashMap<>();
            m.put("awardId", a.getAwardId()+"");
            m.put("awardName", a.getAwardName());
            m.put("awardType",a.getAwardType());
            m.put("awardLevel",a.getAwardLevel());
            m.put("awardDate",a.getAwardDate());
            m.put("organization",a.getOrganization());
            m.put("awardPath",a.getAwardPath());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse awardSave(DataRequest dataRequest) {
        Integer awardId = dataRequest.getInteger("awardId");
        String awardName = dataRequest.getString("awardName");
        String awardType = dataRequest.getString("awardType");
        String awardLevel= dataRequest.getString("awardLevel");
        String awardDate = dataRequest.getString("awardDate");
        String awardPath = dataRequest.getString("awardPath");
        String organization = dataRequest.getString("organization");
        Optional<Award> op;
        Award award= null;

        // 检查是否存在现有获奖记录
        if(awardId != null) {
            op = awardRepository.findById(awardId);
            if(op.isPresent())
                award = op.get();
        }

        // 不存在则创建新对象
        if(award == null)
            award = new Award();

        // 处理关联学生
        Student student = null;
        if(student.getPersonId() != null) {
            Optional<Student> studentOp = studentRepository.findById(student.getPersonId());
            if(studentOp.isPresent())
                student = studentOp.get();
        }

        // 设置属性
        award.setAwardId(awardId);
        award.setAwardName(awardName);
        award.setAwardDate(awardDate);
        award.setAwardLevel(awardLevel);
        award.setOrganization(organization);
        award.setStudent(student);  // 设置关联学生

        // 保存到数据库
        awardRepository.save(award);

        return CommonMethod.getReturnMessageOK();
    }


    public DataResponse awardDelete(DataRequest dataRequest) {
        Integer awardId = dataRequest.getInteger("awardId");
        if(awardId != null) {
            awardRepository.deleteById(awardId);
        }
        return CommonMethod.getReturnMessageOK();
    }

}