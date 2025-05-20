package cn.edu.sdu.java.server.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    @NotBlank
    @Size(max = 100)
    private String eventName;

    @Size(max = 20)
    private String eventType;

    @Size(max = 50)
    private String location;

    private String startTime;
    private String endTime;

    private Integer maxParticipants;
    private Integer currentParticipants = 0;
    @Size(max = 20)
    private String num;

    @Size(max = 50)
    private String name;

}



