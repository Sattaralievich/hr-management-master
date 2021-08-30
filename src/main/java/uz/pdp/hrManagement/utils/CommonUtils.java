package uz.pdp.hrManagement.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.pdp.hrManagement.entity.Rol;
import uz.pdp.hrManagement.entity.User;
import uz.pdp.hrManagement.entity.enums.RolName;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CommonUtils {

    public static Map<String, Object> getPrincipalAndRoleFromSecurityContextHolder() {
        Map<String, Object> map = new HashMap<String, Object>();

        User principalUser = null;
        RolName principalUserRol = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        principalUser = (User) authentication.getPrincipal();

        Set<Rol> userPrincipalRols = principalUser.getRols();
        for (Rol rol : userPrincipalRols) {
            principalUserRol = rol.getRolName();
        }
        map.put("principalUser", principalUser);
        map.put("principalUserRol", principalUserRol);
        return map;
    }


    public static Integer generateCode() {

        return new Random().nextInt((999999 - 100000) + 1) + 100000;
    }
}