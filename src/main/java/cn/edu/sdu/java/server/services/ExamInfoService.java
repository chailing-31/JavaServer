package cn.edu.sdu.java.server.services;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.ExamInfo;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ExamInfoRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ExamInfoService {
    private final CourseRepository courseRepository;
    private final ExamInfoRepository examInfoRepository;
    private final StudentRepository studentRepository;

    public ExamInfoService(CourseRepository courseRepository, ExamInfoRepository examInfoRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.examInfoRepository = examInfoRepository;
        this.studentRepository = studentRepository;
    }
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getPersonId(),s.getPersonId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> sList = courseRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : sList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getNum()+"-"+c.getName()));
        }
        return new OptionItemList(0, itemList);
    }
    public DataResponse getExamInfoList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        if (personId == null)
            personId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null)
            courseId = 0;
        List<ExamInfo> eList = examInfoRepository.findByStudentCourse(personId, courseId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (ExamInfo e : eList) {
            m = new HashMap<>();
            m.put("examInfoId", e.getExamInfoId() + "");
            m.put("personId", e.getStudent().getPersonId() + "");
            m.put("courseId", e.getCourse().getCourseId() + "");
            m.put("studentNum", e.getStudent().getPerson().getNum());
            m.put("studentName", e.getStudent().getPerson().getName());
            m.put("className", e.getStudent().getClassName());
            m.put("courseNum", e.getCourse().getNum());
            m.put("courseName", e.getCourse().getName());
            m.put("credit", "" + e.getCourse().getCredit());
            m.put("examKind", e.getExamKind());
            m.put("examAddress", e.getAddress());
            m.put("examTime", e.getExamTime());
            dataList.add(m);
        }
            return CommonMethod.getReturnData(dataList);

        }
    public DataResponse examInfoSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Integer courseId = dataRequest.getInteger("courseId");
        Date examTime = dataRequest.getTime("Time");
        Integer examInfoId = dataRequest.getInteger("examInfoId");
        String examKind = dataRequest.getString("examKind");
        String examAddress = dataRequest.getString("examAddress");
        Optional<ExamInfo> op;
        ExamInfo e = null;
        if(examInfoId != null) {
            op= examInfoRepository.findById(examInfoId);
            if(op.isPresent())
                e = op.get();
        }
        if(e == null) {
            e = new ExamInfo();
            e.setStudent(studentRepository.findById(personId).get());
            e.setCourse(courseRepository.findById(courseId).get());
        }
        e.setExamKind(examKind);
        e.setExamTime(examTime);
        e.setAddress(examAddress);
        examInfoRepository.save(e);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse examInfoDelete(DataRequest dataRequest) {
        Integer examInfoId = dataRequest.getInteger("examInfoId");
        Optional<ExamInfo> op;
        ExamInfo e = null;
        if(examInfoId != null) {
            op= examInfoRepository.findById(examInfoId);
            if(op.isPresent()) {
                e = op.get();
                examInfoRepository.delete(e);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }
}
