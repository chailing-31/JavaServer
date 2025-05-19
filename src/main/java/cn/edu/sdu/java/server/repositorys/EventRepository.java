package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query("SELECT a FROM Activity a WHERE " +
            "(COALESCE(:keyword, '') = '' OR a.activityName LIKE %:keyword% OR a.description LIKE %:keyword%) " +
            "AND (:type IS NULL OR a.activityType = :type) " +
            "AND (:startStart IS NULL OR a.startTime >= :startStart) " +
            "AND (:endStart IS NULL OR a.startTime <= :endStart)")
    List<Event> findComplexActivities(
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("startStart") LocalDateTime startStart,
            @Param("endStart") LocalDateTime endStart
    );
}
