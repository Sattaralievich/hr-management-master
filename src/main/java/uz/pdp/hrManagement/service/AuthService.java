package uz.pdp.hrManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.hrManagement.entity.Rol;
import uz.pdp.hrManagement.entity.User;
import uz.pdp.hrManagement.entity.Watcher;
import uz.pdp.hrManagement.entity.enums.RolName;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.payload.LoginDto;
import uz.pdp.hrManagement.payload.PasswordDto;
import uz.pdp.hrManagement.payload.RegisterDto;
import uz.pdp.hrManagement.repository.RolRepository;
import uz.pdp.hrManagement.repository.UsersRepository;
import uz.pdp.hrManagement.repository.WatcherRepository;
import uz.pdp.hrManagement.security.JwtProvider;
import uz.pdp.hrManagement.utils.CommonUtils;

import java.util.*;

@Service
public class AuthService implements UserDetailsService {

    final UsersRepository usersRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    WatcherRepository watcherRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MailService mailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired(required = false)
    public AuthService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }


    public ApiResponse register(RegisterDto registerDto) {

        Map<String, Object> contextHolder = CommonUtils.getPrincipalAndRoleFromSecurityContextHolder();
//        User principalUser = (User) contextHolder.get("principalUser");
        RolName principalUserRol = (RolName) contextHolder.get("principalUserRol");

        Rol rol = rolRepository.getById(registerDto.getRolId());
        RolName userRol = rol.getRolName();

        assert principalUserRol != null;

        //DIRECTOR CAN ADD MANAGER OR STAFF,
        boolean isDirectorAuthority = principalUserRol.equals(RolName.ROL_DIRECTOR) &&
                (userRol.equals(RolName.ROL_HR_MANAGER) || userRol.equals(RolName.ROL_MANAGER) || userRol.equals(RolName.ROL_STAFF));

        //HR MANAGER ONLY CAN ADD STAFF
        boolean isHRManagerAuthority = principalUserRol.equals(RolName.ROL_HR_MANAGER) && userRol.equals(RolName.ROL_STAFF);

        // IF CONDITION WILL BE TRUE , SO PRINCIPAL USER IS MANAGER OR STAFF. THEREFORE THEY CAN'T ADD ANYONE
        if (!(isDirectorAuthority || isHRManagerAuthority)) {
            return new ApiResponse("Your position is " + principalUserRol + ".You do not have the authority to add a user with such a role", false);
        }


        boolean existsByEmail = usersRepository.existsByEmail(registerDto.getEmail());
        if (existsByEmail) {
            return new ApiResponse("Email already exists", false);
        }

        User user = new User();
        user.setFirstname(registerDto.getFirstname());
        user.setLastname(registerDto.getLastname());
        user.setEmail(registerDto.getEmail());


        Set<Rol> rolSet = new HashSet<Rol>(Collections.singleton(rol));
        user.setRols(rolSet);

        user.setEmailCode(UUID.randomUUID().toString());

        User savedUser = usersRepository.save(user);
        if (rol.getRolName().equals(RolName.ROL_STAFF)) {
            Watcher watcher = new Watcher();
            watcher.setUser(savedUser);
            watcherRepository.save(watcher);
        }

        String subject = "Confirm Account";

        String text = "Your login: " + user.getEmail() + "\n" +
                "Confirm => http://localhost:8080/api/auth/verifyEmail?emailCode=" + user.getEmailCode() + "&email=" + user.getEmail();
        mailService.sendEmail(user.getEmail(), subject, text);

        return new ApiResponse("Successfully registered. A confirmation message has been sent to email to activate the account", true);
    }


    public ApiResponse verifyEmail(String emailCode, String email) {
        Optional<User> optionalUser = usersRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmailCode(null);
            usersRepository.save(user);
            return new ApiResponse("Account confirmed", true);
        }

        return new ApiResponse("Account already confirmed", false);
    }

    public ApiResponse setPassword(PasswordDto passwordDto) {
        Optional<User> optionalUser = usersRepository.findById(passwordDto.getUserId());
        if (optionalUser.isEmpty()) {
            return new ApiResponse("User not found", false);
        }

        User user = optionalUser.get();
        if (user.getPassword() != null) {
            return new ApiResponse("You have already set password", false);

        }
        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        usersRepository.save(user);

        return new ApiResponse("Successfully set password. Go to login to enter to system", true);
    }

    public ApiResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(),
                    loginDto.getPassword()));
            User user = (User) authentication.getPrincipal();
            String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRols().toString());
            return new ApiResponse("Token", true, token);
        } catch (BadCredentialsException badCredentialsException) {
            return new ApiResponse("Email or password incorrect", true);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        throw new UsernameNotFoundException(email + " not found");
    }


}