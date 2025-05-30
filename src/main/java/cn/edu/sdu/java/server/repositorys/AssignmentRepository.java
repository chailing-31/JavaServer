package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Assignment 数据操作接口，主要实现Assignment数据的查询操作
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {

    Optional<Assignment> findByNum(String num);

    List<Assignment> findByStatus(String status);

    List<Assignment> findByCourseId(Integer courseId);

    List<Assignment> findByTeacherId(Integer teacherId);

    @Query("from Assignment where ?1 = '' or num like %?1% or title like %?1%")
    List<Assignment> findAssignmentListByNumTitle(String numTitle);

    @Query("from Assignment where courseId = ?1 and (?2 = '' or num like %?2% or title like %?2%)")
    List<Assignment> findAssignmentListByCourseAndNumTitle(Integer courseId, String numTitle);

    @Query("from Assignment order by publishTime desc")
    List<Assignment> findAllOrderByPublishTimeDesc();
}