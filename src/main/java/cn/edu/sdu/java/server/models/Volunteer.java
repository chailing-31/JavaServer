package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
/*
 * Volunteer 志愿活动表实体类  保存志愿活动的基本信息，
 * Integer volunteerId 人员表 volunteer 主键 volunteer_id
 * Student student 关联学生 student_id 关联学生的主键 student_id
 * Activity activity 关联活动 activity_id 关联活动的主键 activity_id
 * String degree 级别
 * String type 类型
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
    //@JoinColumn(name = "volunteerId")
    private Activity activity;

    private String degree;
    private String type;
}
