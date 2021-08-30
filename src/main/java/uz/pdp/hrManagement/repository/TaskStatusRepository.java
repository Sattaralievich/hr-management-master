package uz.pdp.hrManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.hrManagement.entity.TaskStatus;
import uz.pdp.hrManagement.entity.enums.TaskType;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Integer> {
    TaskStatus findByName(TaskType name);
}
