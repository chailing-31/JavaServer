
    package cn.edu.sdu.java.server.services;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Homework;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.HomeworkRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;

    @Service
    public class HomeworkService {
        private final CourseRepository courseRepository;
        private final HomeworkRepository homeworkRepository;
        private final StudentRepository studentRepository;

        public HomeworkService(CourseRepository courseRepository, HomeworkRepository homeworkRepository, StudentRepository studentRepository) {
            this.courseRepository = courseRepository;
            this.homeworkRepository = homeworkRepository;
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

        public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
            List<Course> sList = courseRepository.findAll();  //数据库查询操作
            List<OptionItem> itemList = new ArrayList<>();
            for (Course c : sList) {
                itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getNum()+"-"+c.getName()));
            }
            return new OptionItemList(0, itemList);
        }
        public DataResponse getHomeworkList(DataRequest dataRequest) {
            Integer personId = dataRequest.getInteger("personId");
            if (personId == null)
                personId = 0;
            Integer courseId = dataRequest.getInteger("courseId");
            if (courseId == null)
                courseId = 0;
            List<Homework> hList = homeworkRepository.findByStudentCourse(personId, courseId);  //数据库查询操作
            List<Map<String,Object>> dataList = new ArrayList<>();
            Map<String,Object> m;
            for (Homework h : hList) {
                m = new HashMap<>();
                m.put("personId", h.getStudent().getPersonId() + "");
                m.put("courseId", h.getCourse().getCourseId() + "");
                m.put("studentNum", h.getStudent().getPerson().getNum());
                m.put("studentName", h.getStudent().getPerson().getName());
                m.put("className", h.getStudent().getClassName());
                m.put("courseNum", h.getCourse().getNum());
                m.put("courseName", h.getCourse().getName());
                m.put("deadline" , h.getDeadline());
                m.put("homeworkId", h.getHomeworkId());
                m.put("request", h.getRequest());
                m.put("result", h.getResult());
                dataList.add(m);
            }
            return CommonMethod.getReturnData(dataList);

        }
        public DataResponse homeworkSave(DataRequest dataRequest) {
            Integer personId = dataRequest.getInteger("personId");
            Integer courseId = dataRequest.getInteger("courseId");
            Date deadline = dataRequest.getTime("deadline");
            Integer HomeworkId = dataRequest.getInteger("HomeworkId");
            Integer mark = dataRequest.getInteger("mark");
            String request = dataRequest.getString("request");
            String result = dataRequest.getString("result");

            Optional<Homework> op;
            Homework h = null;
            if(HomeworkId != null) {
                op= homeworkRepository.findById(HomeworkId);
                if(op.isPresent())
                    h = op.get();
            }
            if(h == null) {
                h = new Homework();
                h.setStudent(studentRepository.findById(personId).get());
                h.setCourse(courseRepository.findById(courseId).get());
            }
            h.setDeadline(deadline);
            h.setRequest(request);
            h.setResult(result);
            h.setMark(mark);
            homeworkRepository.save(h);
            return CommonMethod.getReturnMessageOK();
        }
        public DataResponse homeworkDelete(DataRequest dataRequest) {
            Integer HomeworkId = dataRequest.getInteger("HomeworkId");
            Optional<Homework> op;
            Homework h = null;
            if(HomeworkId != null) {
                op= homeworkRepository.findById(HomeworkId);
                if(op.isPresent()) {
                    h = op.get();
                    homeworkRepository.delete(h);
                }
            }
            return CommonMethod.getReturnMessageOK();
        }
    }

