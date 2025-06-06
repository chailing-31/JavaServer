package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);


}
