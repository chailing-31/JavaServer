package cn.edu.sdu.java.server.repositorys;


import cn.edu.sdu.java.server.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * HonorRepository 荣誉奖项数据访问接口
 */
@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {

    /**
     * 根据荣誉编号查找
     */
    Honor findByNum(String num);

    /**
     * 根据学生ID查找荣誉记录
     */
    List<Honor> findByStudentId(Integer studentId);

    /**
     * 根据荣誉类型查找
     */
    List<Honor> findByHonorType(String honorType);

    /**
     * 根据荣誉等级查找
     */
    List<Honor> findByHonorLevel(String honorLevel);

    /**
     * 根据状态查找
     */
    List<Honor> findByStatus(String status);

    /**
     * 根据条件模糊查询
     */
    @Query("SELECT h FROM Honor h LEFT JOIN h.student s " +
            "WHERE (:numName IS NULL OR :numName = '' OR h.num LIKE %:numName% OR h.honorName LIKE %:numName% OR s.person.name LIKE %:numName%) "
            +
            "AND (:honorType IS NULL OR :honorType = '' OR h.honorType = :honorType) " +
            "AND (:honorLevel IS NULL OR :honorLevel = '' OR h.honorLevel = :honorLevel) " +
            "AND (:status IS NULL OR :status = '' OR h.status = :status) " +
            "ORDER BY h.awardDate DESC, h.createTime DESC")
    List<Honor> findByConditions(@Param("numName") String numName,
                                 @Param("honorType") String honorType,
                                 @Param("honorLevel") String honorLevel,
                                 @Param("status") String status);

    /**
     * 查找指定学生的荣誉记录数量
     */
    @Query("SELECT COUNT(h) FROM Honor h WHERE h.studentId = :studentId AND h.status = '有效'")
    Long countValidHonorsByStudentId(@Param("studentId") Integer studentId);

    /**
     * 查找指定时间段内的荣誉记录
     */
    @Query("SELECT h FROM Honor h WHERE h.awardDate >= :startDate AND h.awardDate <= :endDate")
    List<Honor> findByAwardDateRange(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    /**
     * 按荣誉类型统计
     */
    @Query("SELECT h.honorType, COUNT(h) FROM Honor h WHERE h.status = '有效' GROUP BY h.honorType")
    List<Object[]> countByHonorType();

    /**
     * 按荣誉等级统计
     */
    @Query("SELECT h.honorLevel, COUNT(h) FROM Honor h WHERE h.status = '有效' GROUP BY h.honorLevel")
    List<Object[]> countByHonorLevel();
}