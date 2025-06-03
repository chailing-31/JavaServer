package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Technology竞赛表实体类 保存竞赛的基本信息
 */
@Getter
@Setter
@Entity
@Table(name = "technology", uniqueConstraints = {
        @UniqueConstraint(columnNames = "num")
})

public class Technology {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer technologyId;

    @NotBlank
    @Size(max = 20)
    private String num; // 科技成果编号

    @NotBlank
    @Size(max = 100)
    private String name; // 科技成果名称

    @Size(max = 50)
    private String type; // 科技成果类型

//    @Size(max = 20)
//    private String startTime; // 开始时间
//
//    @Size(max = 20)
//    private String endTime; // 结束时
//    @Size(max = 20)
//    private String registrationDeadline; // 报名截止时间
//    @Size(max = 200)
//    private String location; // 竞赛地点
//    @Size(max = 100)
//    private String organizer; // 主办方
//    @Size(max = 2000)
//    private String awards; // 奖项设置
//    @Size(max = 2000)
//    private String requirements; // 参赛要求
    @Size(max = 2000)
    private String description; // 科技成果简介
//    @Size(max = 20)
//    private String status; // 竞赛状态：未开始、进行中、已结束
//
//    private Integer maxParticipants; // 最大参赛人数
    @Size(max = 20)
    private String createTime; // 创建时间
}
