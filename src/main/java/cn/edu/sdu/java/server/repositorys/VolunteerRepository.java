package cn.edu.sdu.java.server.repositorys;

/*
 * Volunteer 数据操作接口，主要实现Volunteer数据的查询操作
 * List<Volunteer> findByStudentPersonId(Integer personId);  根据关联的Student的student_id查询获得List<Volunteer>对象集合,  命名规范
 */

import cn.edu.sdu.java.server.models.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer,Integer> {
    List<Volunteer> findByStudentPersonId(Integer personId);
    @Query(value="from Volunteer where (?1=0 or student.personId=?1) and (?2=0 or course.courseId=?2)" )
    List<Volunteer> findByStudentActivity(Integer personId, Integer activityId);

    @Query(value="from Volunteer where student.personId=?1 and (?2=0 or volunteer.name like %?2%)" )
    List<Volunteer> findByStudentVolunteer(Integer personId, String activityName);

}
