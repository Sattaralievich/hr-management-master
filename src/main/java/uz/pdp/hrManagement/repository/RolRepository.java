package uz.pdp.hrManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.hrManagement.entity.Rol;
import uz.pdp.hrManagement.entity.enums.RolName;


public interface RolRepository extends JpaRepository<Rol, Integer> {
    Rol findByRolName(RolName rolName);
}
