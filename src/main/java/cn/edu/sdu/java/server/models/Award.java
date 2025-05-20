package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "award")
public class Award {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer awardId;

    @NotBlank
    @Size(max = 100)
    private String awardName;

    @Size(max = 20)
    private String awardType; // 竞赛/评优/奖学金

    @Size(max = 20)
    private String awardLevel; // 一等奖/金奖

    private String awardDate;

    @Size(max = 50)
    private String organization;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Size(max = 12)
    private String awardPath;
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;
}