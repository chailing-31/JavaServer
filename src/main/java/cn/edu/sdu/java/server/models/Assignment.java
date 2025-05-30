package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Assignment作业表实体类 保存作业的基本信息
 */
@Getter
@Setter
@Entity
@Table(name = "assignment", uniqueConstraints = {
        @UniqueConstraint(columnNames = "num")
})
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer assignmentId;

    @NotBlank
    @Size(max = 20)
    private String num; // 作业编号

    @NotBlank
    @Size(max = 200)
    private String title; // 作业标题

    private Integer courseId; // 所属课程ID

    @ManyToOne
    @JoinColumn(name = "courseId", insertable = false, updatable = false)
    @JsonIgnore
    private Course course; // 关联课程

    private Integer teacherId; // 发布教师ID

    @Size(max = 20)
    private String publishTime; // 发布时间

    @Size(max = 20)
    private String deadline; // 截止时间

    @Size(max = 5000)
    private String content; // 作业内容

    @Size(max = 2000)
    private String requirements; // 作业要求

    private Integer totalScore; // 总分

    @Size(max = 20)
    private String status; // 状态：草稿、已发布、已截止

    @Size(max = 500)
    private String attachmentPath; // 附件路径

    @Size(max = 50)
    private String submissionType; // 提交方式：在线提交、文件上传、邮件提交
}