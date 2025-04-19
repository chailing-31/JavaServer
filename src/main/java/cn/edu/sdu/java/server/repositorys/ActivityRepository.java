package cn.edu.sdu.java.server.repositorys;

/*
 * Activity 数据操作接口，主要实现Activity数据的查询操作
 */

import cn.edu.sdu.java.server.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    @Query(value = "from Activity where ?1='' or num like %?1% or name like %?1% ")
    List<Activity> findActivityListByNumName(String numName);

    Optional<Activity> findByNum(String num);
    List<Activity> findByName(String name);

}
