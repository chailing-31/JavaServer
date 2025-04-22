package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Attendance findById(Integer id);

    List<Attendance> findByStudentPersonId(Integer personId);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStudentPersonIdAndDateBetween(Integer personId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByStudentClassNameAndDate(String className, LocalDate date);
}
