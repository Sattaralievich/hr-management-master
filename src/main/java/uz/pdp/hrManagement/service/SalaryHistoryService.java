package uz.pdp.hrManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.hrManagement.entity.SalaryHistory;
import uz.pdp.hrManagement.entity.enums.RolName;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.repository.SalaryHistoryRepository;
import uz.pdp.hrManagement.utils.CommonUtils;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SalaryHistoryService {
    @Autowired
    SalaryHistoryRepository salaryHistoryRepository;

    public ApiResponse getAllByMonth(Date minDate, Date maxDate) {
        Map<String, Object> contextHolder = CommonUtils.getPrincipalAndRoleFromSecurityContextHolder();
//        User principalUser = (User) contextHolder.get("principalUser");
        RolName principalUserRol = (RolName) contextHolder.get("principalUserRol");

        assert principalUserRol != null;
        if (principalUserRol.equals(RolName.ROL_STAFF) || principalUserRol.equals(RolName.ROL_MANAGER)) {
            return new ApiResponse("You can not see staff's salary history ", false);
        }

        Set<SalaryHistory> salaryHistoryList = salaryHistoryRepository.findAllByDateBetween(minDate, maxDate);
        return new ApiResponse("Salary histories", true, salaryHistoryList);
    }

    public ApiResponse getAllByUserId(UUID userId) {
        Map<String, Object> contextHolder = CommonUtils.getPrincipalAndRoleFromSecurityContextHolder();
//        User principalUser = (User) contextHolder.get("principalUser");
        RolName principalUserRol = (RolName) contextHolder.get("principalUserRol");

        assert principalUserRol != null;
        if (principalUserRol.equals(RolName.ROL_STAFF) || principalUserRol.equals(RolName.ROL_MANAGER)) {
            return new ApiResponse("You can not see staff's salary history ", false);
        }

        Set<SalaryHistory> salaryHistoryList = salaryHistoryRepository.findAllByUserId(userId);
        return new ApiResponse("Salary histories", true, salaryHistoryList);
    }
}
