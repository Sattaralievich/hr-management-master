package uz.pdp.hrManagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import uz.pdp.hrManagement.entity.enums.RolName;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Rol implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private RolName rolName;

    public Rol(RolName rolName) {
        this.rolName = rolName;
    }

    @Override
    public String getAuthority() {
        return rolName.name();
    }
}
