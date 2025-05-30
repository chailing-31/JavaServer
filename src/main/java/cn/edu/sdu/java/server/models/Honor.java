package cn.edu.sdu.java.server.models;

import org.springframework.format.annotation.DateTimeFormat;
import cn.edu.sdu.java.server.models.Student;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Honor 荣誉奖项实体类
 */
@Entity
@Table(name = "honor")
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "honor_id")
    private Integer honorId;

    @NotBlank(message = "荣誉编号不能为空")
    @Column(name = "num", length = 20, unique = true)
    private String num;

    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "荣誉名称不能为空")
    @Column(name = "honor_name", length = 100)
    private String honorName;

    @NotBlank(message = "荣誉类型不能为空")
    @Column(name = "honor_type", length = 20)
    private String honorType;

    @NotBlank(message = "荣誉等级不能为空")
    @Column(name = "honor_level", length = 20)
    private String honorLevel;

    @Column(name = "awarding_organization", length = 100)
    private String awardingOrganization;

    @NotNull(message = "获奖日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "award_date")
    private LocalDate awardDate;

    @Column(name = "certificate_number", length = 50)
    private String certificateNumber;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @Column(name = "status", length = 20)
    private String status = "有效";

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // 关联学生信息
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    public Honor() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getHonorId() {
        return honorId;
    }

    public void setHonorId(Integer honorId) {
        this.honorId = honorId;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getHonorName() {
        return honorName;
    }

    public void setHonorName(String honorName) {
        this.honorName = honorName;
    }

    public String getHonorType() {
        return honorType;
    }

    public void setHonorType(String honorType) {
        this.honorType = honorType;
    }

    public String getHonorLevel() {
        return honorLevel;
    }

    public void setHonorLevel(String honorLevel) {
        this.honorLevel = honorLevel;
    }

    public String getAwardingOrganization() {
        return awardingOrganization;
    }

    public void setAwardingOrganization(String awardingOrganization) {
        this.awardingOrganization = awardingOrganization;
    }

    public LocalDate getAwardDate() {
        return awardDate;
    }

    public void setAwardDate(LocalDate awardDate) {
        this.awardDate = awardDate;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}