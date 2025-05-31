package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "teacher")
public class Teacher {
    @Id
    private Integer personId;

    @OneToOne
    @JoinColumn(name="personId")
    @JsonIgnore
    private Person person;


    @Column(nullable = false, length = 50)
    private String email;

    @OneToMany(mappedBy = "approver")
    private List<LeaveApplication> approvedApplications = new ArrayList<>();


}
