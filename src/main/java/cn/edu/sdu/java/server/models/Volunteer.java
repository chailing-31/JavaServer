package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;

/*
 * Volunteer 志愿者表实体类  保存志愿者的基本信息，
 * Integer volunteerId 志愿者表 volunteer 主键 volunteer_id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * Activity activity 关联活动 activity_id 关联活动的主键 activity_id
 * Integer hours 志愿时长
 * String role 志愿者角色
 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(	name = "volunteer",
        uniqueConstraints = {
        })

public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer volunteerId;

    @ManyToOne
    @JoinColumn(name = "personId")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "activityId")
    private Activity activity;

    private Integer hours;
    private String role;
}
