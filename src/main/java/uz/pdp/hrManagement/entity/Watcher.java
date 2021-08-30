package uz.pdp.hrManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Watcher {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User user;

//    @CreationTimestamp
//    private Date checkTime;

    @ManyToOne
    private WatcherStatus watcherStatus;
}
