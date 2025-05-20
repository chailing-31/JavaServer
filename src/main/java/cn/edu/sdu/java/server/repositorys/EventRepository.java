package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Integer> {
    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1% ")
    List<Event> findEventListByNumName(String numName);

    Optional<Event> findByNum(String num);
    List<Event> findByName(String name);
}