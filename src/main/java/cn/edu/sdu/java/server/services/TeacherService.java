package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.PersonRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;

@Service
public class TeacherService {
    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);
    private final PersonRepository personRepository;

    public TeacherService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * 将Teacher(Person)对象转换为Map
     */
    public Map<String, Object> getMapFromTeacher(Person teacher) {
        Map<String, Object> m = new HashMap<>();
        if (teacher == null)
            return m;

        m.put("personId", teacher.getPersonId());
        m.put("num", teacher.getNum());
        m.put("name", teacher.getName());
        m.put("dept", teacher.getDept());
        m.put("card", teacher.getCard());
        m.put("gender", teacher.getGender());
        m.put("email", teacher.getEmail());
        m.put("phone", teacher.getPhone());
        m.put("address", teacher.getAddress());
        m.put("introduce", teacher.getIntroduce());

        return m;
    }

    /**
     * 获取教师列表
     */
    public OptionItemList getTeacherList(DataRequest dataRequest) {
        String name = dataRequest.getString("name");
        if (name == null)
            name = "";

        List<Person> teacherList;
        if (name.isEmpty()) {
            teacherList = personRepository.findPersonListByType("2"); // 教师类型
        } else {
            teacherList = personRepository.findPersonListByNameAndType(name, "2");
        }

        /*List<Map<String, Object>> dataList = new ArrayList<>();
        for (Person teacher : teacherList) {
            dataList.add(getMapFromTeacher(teacher));
        }

        return CommonMethod.getReturnData(dataList);
         */

        List<OptionItem> dataList = new ArrayList<>();
        for (Person teacher : teacherList) {
            dataList.add(new OptionItem( teacher.getPersonId(),teacher.getPersonId()+"", teacher.getNum()+"-"+teacher.getName()));
        }
        return new OptionItemList(0, dataList);
    }


    /**
     * 获取教师详细信息
     */
    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        Person teacher = null;

        if (personId != null) {
            Optional<Person> op = personRepository.findById(personId);
            if (op.isPresent() && "2".equals(op.get().getType())) {
                teacher = op.get();
            }
        }

        return CommonMethod.getReturnData(getMapFromTeacher(teacher));
    }

    /**
     * 保存教师信息
     */
    public DataResponse teacherSave(DataRequest dataRequest) {
        try {
            Integer personId = dataRequest.getInteger("personId");
            Map<String, Object> form = dataRequest.getMap("form");

            if (form == null) {
                return CommonMethod.getReturnMessageError("表单数据不能为空");
            }

            String num = CommonMethod.getString(form, "num");
            String name = CommonMethod.getString(form, "name");
            String dept = CommonMethod.getString(form, "dept");
            String card = CommonMethod.getString(form, "card");
            String gender = CommonMethod.getString(form, "gender");
            String birthday = CommonMethod.getString(form, "birthday");
            String email = CommonMethod.getString(form, "email");
            String phone = CommonMethod.getString(form, "phone");
            String address = CommonMethod.getString(form, "address");
            String introduce = CommonMethod.getString(form, "introduce");

            // 数据验证
            if (num == null || num.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("教师编号不能为空");
            }
            if (name == null || name.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("教师姓名不能为空");
            }
            if (dept == null || dept.trim().isEmpty()) {
                return CommonMethod.getReturnMessageError("学院不能为空");
            }

            // 验证邮箱格式
            if (email != null && !email.trim().isEmpty()) {
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    return CommonMethod.getReturnMessageError("邮箱格式不正确");
                }
            }

            // 验证身份证格式
            if (card != null && !card.trim().isEmpty()) {
                if (!card.matches(
                        "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$")) {
                    return CommonMethod.getReturnMessageError("身份证号格式不正确");
                }
            }

            Optional<Person> op;
            Person teacher = null;

            if (personId != null) {
                op = personRepository.findById(personId);
                if (op.isPresent() && "2".equals(op.get().getType())) {
                    teacher = op.get();
                }
            }

            // 检查教师编号是否已存在（排除当前编辑的教师）
            Optional<Person> existingTeacher = personRepository.findByNum(num.trim());
            if (existingTeacher.isPresent()) {
                if (teacher == null || !existingTeacher.get().getPersonId().equals(teacher.getPersonId())) {
                    return CommonMethod.getReturnMessageError("教师编号已经存在，不能添加或修改！");
                }
            }

            if (teacher == null) {
                teacher = new Person();
                teacher.setType("2"); // 设置为教师类型
                log.info("创建新教师: {}", name);
            } else {
                log.info("更新教师: {} (ID: {})", name, personId);
            }

            teacher.setNum(num.trim());
            teacher.setName(name.trim());
            teacher.setDept(dept.trim());
            teacher.setCard(card != null ? card.trim() : "");
            teacher.setGender(gender);
            teacher.setBirthday(birthday);
            teacher.setEmail(email != null ? email.trim() : "");
            teacher.setPhone(phone != null ? phone.trim() : "");
            teacher.setAddress(address != null ? address.trim() : "");
            teacher.setIntroduce(introduce != null ? introduce.trim() : "");

            personRepository.save(teacher);
            log.info("教师保存成功: {} (ID: {})", teacher.getName(), teacher.getPersonId());

            return CommonMethod.getReturnData(teacher.getPersonId());
        } catch (Exception e) {
            log.error("保存教师失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("保存教师失败: " + e.getMessage());
        }
    }

    /**
     * 删除教师
     */
    public DataResponse teacherDelete(DataRequest dataRequest) {
        try {
            Integer personId = dataRequest.getInteger("personId");

            if (personId == null) {
                return CommonMethod.getReturnMessageError("教师ID不能为空");
            }

            Optional<Person> op = personRepository.findById(personId);
            if (op.isPresent() && "2".equals(op.get().getType())) {
                Person teacher = op.get();

                // 可以在这里添加额外的业务逻辑检查
                // 比如检查该教师是否有相关的课程或其他关联数据

                personRepository.delete(teacher);
                log.info("教师删除成功: {} (ID: {})", teacher.getName(), personId);
            } else {
                return CommonMethod.getReturnMessageError("找不到要删除的教师");
            }

            return CommonMethod.getReturnMessageOK();
        } catch (Exception e) {
            log.error("删除教师失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("删除教师失败: " + e.getMessage());
        }
    }

    /**
     * 导出教师列表到Excel
     */
    public ResponseEntity<StreamingResponseBody> getTeacherListExcel(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String, Object>> list = getTeacherMapList(numName);

        Integer[] widths = { 8, 15, 15, 20, 20, 10, 15, 15, 15, 30, 20 };
        String[] titles = { "序号", "教师编号", "姓名", "学院", "身份证号", "性别", "生日", "邮箱", "电话", "地址", "个人简介" };
        String outputSheetName = "teacher.xlsx";

        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFSheet sheet = wb.createSheet(outputSheetName);

        // 设置列宽
        for (int j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }

        // 创建标题行
        XSSFRow row = sheet.createRow(0);
        XSSFCell[] cell = new XSSFCell[widths.length];
        for (int j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
        }

        // 填充数据
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }

                Map<String, Object> m = list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "dept"));
                cell[4].setCellValue(CommonMethod.getString(m, "card"));
                cell[5].setCellValue("1".equals(CommonMethod.getString(m, "gender")) ? "男"
                        : "2".equals(CommonMethod.getString(m, "gender")) ? "女" : "");
                cell[6].setCellValue(CommonMethod.getString(m, "birthday"));
                cell[7].setCellValue(CommonMethod.getString(m, "email"));
                cell[8].setCellValue(CommonMethod.getString(m, "phone"));
                cell[9].setCellValue(CommonMethod.getString(m, "address"));
                cell[10].setCellValue(CommonMethod.getString(m, "introduce"));
            }
        }

        try {
            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取教师Map列表
     */
    private List<Map<String, Object>> getTeacherMapList(String numName) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Person> teacherList;

        if (numName == null || numName.isEmpty()) {
            teacherList = personRepository.findPersonListByType("2");
        } else {
            teacherList = personRepository.findPersonListByNameAndType(numName, "2");
        }

        if (teacherList == null || teacherList.isEmpty())
            return dataList;

        for (Person teacher : teacherList) {
            dataList.add(getMapFromTeacher(teacher));
        }

        return dataList;
    }

    /**
     * 获取所有教师列表（用于其他模块选择教师）
     */
    public DataResponse getAllTeachers(DataRequest dataRequest) {
        try {
            List<Person> teachers = personRepository.findPersonListByType("2");
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Person teacher : teachers) {
                Map<String, Object> m = new HashMap<>();
                m.put("value", teacher.getPersonId());
                m.put("title", teacher.getName() + " (" + teacher.getNum() + ")");
                dataList.add(m);
            }

            return CommonMethod.getReturnData(dataList);
        } catch (Exception e) {
            log.error("获取教师列表失败: {}", e.getMessage(), e);
            return CommonMethod.getReturnMessageError("获取教师列表失败: " + e.getMessage());
        }
    }
}