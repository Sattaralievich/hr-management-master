package uz.pdp.hrManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.security.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SalaryHistory {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Date date;

    @ManyToOne
    private User user;

    @CreatedBy
    private UUID createdBy;

    @LastModifiedBy
    private UUID updatedBy;

//    @Column(updatable = false, nullable = false)
//    @CreationTimestamp
//    private Timestamp createdAt;
//
//    @UpdateTimestamp
//    private Timestamp updatedAt;

}

