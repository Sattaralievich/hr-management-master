package uz.pdp.hrManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.hrManagement.entity.WorkTimeHistory;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.service.SalaryHistoryService;
import uz.pdp.hrManagement.service.TaskService;
import uz.pdp.hrManagement.service.UserService;
import uz.pdp.hrManagement.service.WorkTimeHistoryService;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@RestController("api/management")
public class ManagementController {
    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @Autowired
    WorkTimeHistoryService workTimeHistoryService;

    @Autowired
    SalaryHistoryService salaryHistoryService;

    @GetMapping("/staff")
    public HttpEntity<?> getAllStaff() {
        ApiResponse response = userService.getAllStaff();
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    @GetMapping("/workTimeHistory/{userId}")
    public Set<WorkTimeHistory> getWorkTimeHistoriesByUserId(@PathVariable UUID userId) {
        return workTimeHistoryService.getAllByUserId(userId);
    }


    @GetMapping("/task/{userId}")
    public HttpEntity<?> getTasksByUserId(@PathVariable UUID userId) {
        ApiResponse response = taskService.getAllByUserId(userId);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }


    @GetMapping("/salaryHistory/byMonth")
    public HttpEntity<?> getSalaryHistoriesByMonth(@RequestParam Date minDate, @RequestParam Date maxDate) {
        ApiResponse response = salaryHistoryService.getAllByMonth(minDate, maxDate);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }


    @GetMapping("/salaryHistory/byUserId/{userId}")
    public HttpEntity<?> getSalaryHistoriesByUserId(@PathVariable UUID userId) {
        ApiResponse response = salaryHistoryService.getAllByUserId(userId);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }
}
