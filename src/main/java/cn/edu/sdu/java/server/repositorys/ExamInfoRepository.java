package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.ExamInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ExamInfoRepository extends JpaRepository<ExamInfo,Integer>{

        List<ExamInfo> findByStudentPersonId(Integer personId);
        @Query(value="from ExamInfo where (?1=0 or student.personId=?1) and (?2=0 or course.courseId=?2)" )
        List<ExamInfo> findByStudentCourse(Integer personId, Integer courseId);

        @Query(value="from ExamInfo where student.personId=?1 and (?2=0 or course.name like %?2%)" )
        List<ExamInfo> findByStudentCourse(Integer personId, String courseName);

    }

