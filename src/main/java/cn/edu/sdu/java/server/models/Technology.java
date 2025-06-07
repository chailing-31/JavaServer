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

    @Size(max = 2000)
    private String description; // 科技成果简介

    @Size(max = 20)
    private String createTime; // 创建时间
}
