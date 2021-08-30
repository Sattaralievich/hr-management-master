package uz.pdp.hrManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.hrManagement.entity.User;
import uz.pdp.hrManagement.entity.enums.RolName;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.repository.UsersRepository;
import uz.pdp.hrManagement.utils.CommonUtils;

import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    UsersRepository usersRepository;


    public ApiResponse getAllStaff() {
        Map<String, Object> contextHolder = CommonUtils.getPrincipalAndRoleFromSecurityContextHolder();
//        User principalUser = (User) contextHolder.get("principalUser");
        RolName principalUserRole = (RolName) contextHolder.get("principalUserRol");

        assert principalUserRole != null;
        if (principalUserRole.equals(RolName.ROL_STAFF) || principalUserRole.equals(RolName.ROL_MANAGER)) {
            return new ApiResponse("You don not have empowerment to see staff information", false);
        }
        Set<User> staffList = usersRepository.findAllByRolesId(4);
        return new ApiResponse("Staff", true, staffList);
    }
}

