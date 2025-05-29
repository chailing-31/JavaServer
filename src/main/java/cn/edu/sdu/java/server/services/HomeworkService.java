
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
                m.put("deadline" , h.getDeadline());//获取作业截止时间
                m.put("homeworkId", h.getHomeworkId());
                m.put("request", h.getRequest());//获取作业要求
                m.put("result", h.getResult());//获取作业提交结果
                m.put("mark",""+h.getMark());//获取作业成绩
                dataList.add(m);
            }
            return CommonMethod.getReturnData(dataList);
        }

        // 添加获取单个作业详情的方法
        public DataResponse getHomeworkInfo(DataRequest dataRequest) {
            Integer homeworkId = dataRequest.getInteger("homeworkId");
            if(homeworkId == null) {
                return CommonMethod.getReturnMessageError("作业ID不能为空");
            }

            Optional<Homework> op = homeworkRepository.findById(homeworkId);
            if(!op.isPresent()) {
                return CommonMethod.getReturnMessageError("作业不存在");
            }

            Homework h = op.get();
            Map<String, Object> m = new HashMap<>();
            // 字段映射
            m.put("homeworkId", h.getHomeworkId());
            m.put("num", h.getHomeworkId().toString()); // 暂时用作业ID作为编号
            m.put("title", h.getRequest()); // 用作业要求作为标题
            m.put("courseId", h.getCourse().getCourseId());
            m.put("publishTime", new Date()); // 添加发布日期
            m.put("deadline", h.getDeadline());
            m.put("content", h.getRequest()); // 内容也使用作业要求
            m.put("requirements", h.getRequest());
            m.put("totalScore", 100); // 默认总分
            m.put("status", "已发布"); // 默认状态
            m.put("submissionType", "在线提交"); // 默认提交方式

            return CommonMethod.getReturnData(m);
        }

        public DataResponse homeworkSave(DataRequest dataRequest) {
            Map form = dataRequest.getMap("form");
            if(form == null) {
                form = new HashMap();
            }

            Integer homeworkId = dataRequest.getInteger("homeworkId");
            Integer courseId = CommonMethod.getInteger(form, "courseId");
            String request = CommonMethod.getString(form, "requirements");
            Date deadline = CommonMethod.getDate(form, "deadline");

            Optional<Homework> op;
            Homework h = null;
            if(homeworkId != null) {
                op = homeworkRepository.findById(homeworkId);
                if(op.isPresent()) {
                    h = op.get();
                }
            }

            if(h == null) {
                h = new Homework();
                // 默认学生 - 可能需要修改
                h.setStudent(studentRepository.findById(1).get());
                h.setCourse(courseRepository.findById(courseId).get());
            }

            h.setRequest(request);
            h.setDeadline(deadline);
            // 设置其他字段默认值
            h.setMark(0);
            h.setResult("");

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
                    homeworkRepository.delete(h);//删除功能
                }
            }
            return CommonMethod.getReturnMessageOK();
        }
    }

