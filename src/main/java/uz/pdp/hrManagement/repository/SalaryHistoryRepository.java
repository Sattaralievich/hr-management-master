package uz.pdp.hrManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uz.pdp.hrManagement.entity.SalaryHistory;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "salaryHistory", collectionResourceRel = "list")
public interface SalaryHistoryRepository extends JpaRepository<SalaryHistory, UUID> {
    Set<SalaryHistory> findAllByDateBetween(Date minDate, Date maxDate);

    Set<SalaryHistory> findAllByUserId(UUID user_id);
}
