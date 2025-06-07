package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Person;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/*
 * Person 数据操作接口，主要实现Person数据的查询操作
 * Integer getMaxId()  person 表中的最大的person_id;    JPQL 注解
 * Optional<Person> findByNum(String num);  根据num查询获得Option<Person>对象,  命名规范
 */
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByNum(String num);

    List<Person> findPersonListByType(String type);

    List<Person> findPersonListByNameAndType(String Name, String type);


    @Query(value = "from Person where ?1='' or num like %?1% or name like %?1% ")
    List<Person> findPersonListByNumName(String numName);
}


