package cn.edu.sdu.java.server.repositorys;


import cn.edu.sdu.java.server.models.Award;
import cn.edu.sdu.java.server.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AwardRepository extends JpaRepository<Award,Integer> {
    @Query(value = "from Course where ?1='' or num like %?1% or name like %?1% ")
    List<Award> findAwardListByNumName(String numName);

    Optional<Award> findByNum(String num);
    List<Award> findByName(String name);
}