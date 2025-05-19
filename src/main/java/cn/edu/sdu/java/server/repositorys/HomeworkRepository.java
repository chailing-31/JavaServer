package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
    @Repository
    public interface HomeworkRepository extends JpaRepository<Homework,Integer> {

        List<Homework> findByStudentPersonId(Integer personId);
        @Query(value="from Homework where (?1=0 or student.personId=?1) and (?2=0 or course.courseId=?2)" )
        List<Homework> findByStudentCourse(Integer personId, Integer courseId);

        @Query(value="from Homework where student.personId=?1 and (?2=0 or course.name like %?2%)" )
        List<Homework> findByStudentCourse(Integer personId, String courseName);

    }


