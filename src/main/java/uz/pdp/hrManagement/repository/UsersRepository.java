package uz.pdp.hrManagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import uz.pdp.hrManagement.entity.User;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Repository
@EnableJpaRepositories
@RepositoryRestResource(path = "users", collectionResourceRel = "list")
public interface UsersRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    boolean existsById(UUID id);

    Optional<User> findByEmailAndEmailCode(String email, String emailCode);

    Optional<User> findByEmail(String email);

    @Query(value = "select email from users where id =?1", nativeQuery = true)
    String getEmailById(UUID id);

    @Query(value = "select firstname,lastname from users where id =?1", nativeQuery = true)
    String getFullNameById(UUID id);


    @Query(value = "select distinct u.id,\n" +
            "                u.firstname,\n" +
            "                u.lastname,\n" +
            "                u.email,\n" +
            "                u.password,\n" +
            "                u.created_at,\n" +
            "                u.updated_at,\n" +
            "                u.account_non_expired,\n" +
            "                u.account_non_locked,\n" +
            "                u.credentials_non_expired,\n" +
            "                u.enabled,\n" +
            "                u.email_code\n" +
            "from users u\n" +
            "         join users_roles ur on u.id = ur.users_id\n" +
            "where ur.roles_id = ?1", nativeQuery = true)
    Set<User> findAllByRolesId(Integer roleId);

}
