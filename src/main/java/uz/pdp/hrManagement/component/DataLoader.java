package uz.pdp.hrManagement.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.hrManagement.entity.Rol;
import uz.pdp.hrManagement.entity.User;
import uz.pdp.hrManagement.entity.enums.RolName;
import uz.pdp.hrManagement.repository.RolRepository;
import uz.pdp.hrManagement.repository.UsersRepository;

import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    RolRepository rolRepository;

    @Value("${spring.sql.init.mode}")
    private String initialMode;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {
            User user = new User(
                    "Asliddin",
                    "Choriyev",
                    "@asliddinchoriyev@gmail.com",
                    passwordEncoder.encode("7777")
            );
            Rol director = rolRepository.findByRolName(RolName.ROL_DIRECTOR);
            user.setRols(Collections.singleton(director));
            user.setEnabled(true);
            usersRepository.save(user);
        }
    }
}

