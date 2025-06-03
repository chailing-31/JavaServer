package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Technology 数据操作接口，主要实现Technology数据的查询操作
 */
public interface TechnologyRepository extends JpaRepository<Technology, Integer> {

    Optional<Technology> findByNum(String num);

//    List<Technology> findByStatus(String status);

    @Query("from Technology where ?1 = '' or num like %?1% or name like %?1%")
    List<Technology> findTechnologyListByNumName(String numName);

    @Query("from Technology where type = ?1")
    List<Technology> findByType(String type);

    @Query("from Technology order by createTime desc")
    List<Technology> findAllOrderByCreateTimeDesc();
}
