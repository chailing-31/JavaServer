package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LeaveRequestRepository 请假申请数据访问接口
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

        /**
         * 根据请假编号查找
         */
        LeaveRequest findByNum(String num);

        /**
         * 根据学生ID查找请假记录
         */
        List<LeaveRequest> findByStudentId(Integer studentId);

        /**
         * 根据请假类型查找
         */
        List<LeaveRequest> findByLeaveType(String leaveType);

        /**
         * 根据状态查找
         */
        List<LeaveRequest> findByStatus(String status);

        /**
         * 根据编号或学生姓名模糊查询
         */
        @Query("SELECT lr FROM LeaveRequest lr LEFT JOIN lr.student s " +
                        "WHERE (:numName IS NULL OR :numName = '' OR lr.num LIKE %:numName% OR s.person.name LIKE %:numName%) "
                        +
                        "AND (:leaveType IS NULL OR :leaveType = '' OR lr.leaveType = :leaveType) " +
                        "AND (:status IS NULL OR :status = '' OR lr.status = :status) " +
                        "ORDER BY lr.createTime DESC")
        List<LeaveRequest> findByConditions(@Param("numName") String numName,
                        @Param("leaveType") String leaveType,
                        @Param("status") String status);

        /**
         * 查找指定学生的请假记录数量
         */
        @Query("SELECT COUNT(lr) FROM LeaveRequest lr WHERE lr.studentId = :studentId")
        Long countByStudentId(@Param("studentId") Integer studentId);

        /**
         * 查找指定时间段内的请假记录
         */
        @Query("SELECT lr FROM LeaveRequest lr WHERE lr.startDate >= :startDate AND lr.endDate <= :endDate")
        List<LeaveRequest> findByDateRange(@Param("startDate") java.time.LocalDateTime startDate,
                        @Param("endDate") java.time.LocalDateTime endDate);

        //根据批准老师ID查询请假记录
        List<LeaveRequest> findByPersonPersonId (Integer personId);

        //根据老师教工号和姓名查询请假记录
        List<LeaveRequest> findByPersonNumAndPersonName(String num, String name);

}