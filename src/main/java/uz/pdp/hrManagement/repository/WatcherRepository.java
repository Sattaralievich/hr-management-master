package uz.pdp.hrManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uz.pdp.hrManagement.entity.Watcher;

import java.util.UUID;

@Repository
@RepositoryRestResource(path = "watcher",collectionResourceRel = "list")
public interface WatcherRepository extends JpaRepository<Watcher, UUID> {
}
