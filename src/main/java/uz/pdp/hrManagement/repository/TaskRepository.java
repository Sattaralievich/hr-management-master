package uz.pdp.hrManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import uz.pdp.hrManagement.entity.Task;

import java.util.Set;
import java.util.UUID;

@Repository
@EnableJpaRepositories
public interface TaskRepository extends JpaRepository<Task, UUID> {
    boolean existsByUsersId(UUID users_id);

    Set<Task> findAllByUsersId(UUID users_id);
}
