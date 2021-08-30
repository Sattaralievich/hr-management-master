package uz.pdp.hrManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.pdp.hrManagement.entity.WatcherStatus;
import uz.pdp.hrManagement.entity.enums.WatcherStatusType;

@Repository
public interface WatcherStatusRepository extends JpaRepository<WatcherStatus, Integer> {
    WatcherStatus findByWatcherStatusType(WatcherStatusType type);
}
