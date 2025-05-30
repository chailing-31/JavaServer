package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Competition 数据操作接口，主要实现Competition数据的查询操作
 */
public interface CompetitionRepository extends JpaRepository<Competition, Integer> {

    Optional<Competition> findByNum(String num);

    List<Competition> findByStatus(String status);

    @Query("from Competition where ?1 = '' or num like %?1% or name like %?1%")
    List<Competition> findCompetitionListByNumName(String numName);

    @Query("from Competition where type = ?1")
    List<Competition> findByType(String type);

    @Query("from Competition order by createTime desc")
    List<Competition> findAllOrderByCreateTimeDesc();
}