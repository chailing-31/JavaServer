package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.LeaveApplication;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Integer> {
    List<LeaveApplication> findByStudentPersonPersonId(Integer personId);

    List<LeaveApplication> findByStatus(LeaveApplication.ApprovalStatus status);

    List<LeaveApplication> findByStudentPersonPersonIdAndStatus(Integer personId, LeaveApplication.ApprovalStatus status);

    List<LeaveApplication> findByStudentClassNameAndStatus(String className, LeaveApplication.ApprovalStatus status);

    @Query("SELECT la FROM LeaveApplication la WHERE la.student.className = :className " +
            "AND la.startTime <= :endDate AND la.endTime >= :startDate")
    List<LeaveApplication> findByClassNameAndDateRange(
            @Param("className") String className,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<LeaveApplication> findByStudentIdAndStatusNot(Integer personId, LeaveApplication.ApprovalStatus status);

}