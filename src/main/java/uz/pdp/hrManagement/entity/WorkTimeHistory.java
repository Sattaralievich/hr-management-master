package uz.pdp.hrManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class WorkTimeHistory {
    @Id
    @GeneratedValue
    private UUID id;

    private Date date;

    private String entryTime;

    private String departureTime;

    @ManyToOne
    private User user;
}
