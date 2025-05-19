package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
    @Setter
    @Entity
    @Table(	name = "homework",
            uniqueConstraints = {
            })
    public class Homework {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer homeworkId;

        @ManyToOne
        @JoinColumn(name = "personId")
        private Student student;

        @ManyToOne
        @JoinColumn(name = "courseId")
        private Course course;

        private Integer mark;
        private String  request ;
        private Date deadline;
        private String result;
    }

