package cn.edu.sdu.java.server.models;

/*
 * Activity 活动实体类  保存活动的基本信息，
 * Integer activityId 活动表 activity 主键 activity_id
 * String num 活动编号
 * String name 活动名称
 * String time 时间
 * Integer duration 时长
 * Activity preActivity 前序课程 pre_activity_id 关联前序课程的主键 activity_id
 */

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(	name = "activity",
        uniqueConstraints = {
        })

public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;
    @NotBlank
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;
    private String time;
    private Integer duration;
    @ManyToOne
    @JoinColumn(name="pre_activity_id")
    private Activity preActivity;
    @Size(max = 12)
    private String activityPath;

}
