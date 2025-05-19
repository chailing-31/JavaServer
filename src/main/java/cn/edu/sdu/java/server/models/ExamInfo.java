package cn.edu.sdu.java.server.models;


import lombok.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(
        name = "examInfo",
        uniqueConstraints = {}
)
public class ExamInfo {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer examInfoId;
    @ManyToOne
    @JoinColumn(
            name = "personId"
    )
    private Student student;
    @ManyToOne
    @JoinColumn(
            name = "courseId"
    )
    private Course course;
    private String address;
    private String examKind;
    private Date examTime;

    public ExamInfo() {
    }



}


