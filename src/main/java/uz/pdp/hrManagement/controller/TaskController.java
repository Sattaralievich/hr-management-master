package uz.pdp.hrManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.hrManagement.entity.Task;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.payload.ReportDto;
import uz.pdp.hrManagement.payload.TaskDto;
import uz.pdp.hrManagement.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    TaskService taskService;


    @GetMapping(value = "/{pageNumber}/{pageSize}")
    public HttpEntity<?> getAll(@PathVariable Integer pageNumber, @PathVariable Integer pageSize) {
        Page<Task> tasks = taskService.getAll(pageNumber, pageSize);
        return ResponseEntity.ok(tasks);
    }

//    @GetMapping("/byUser/{userId}")
//    public HttpEntity<?> getAllByUserId(@PathVariable UUID userId) {
//        List<Task> tasks = taskService.getAllByUserId(userId);
//        return ResponseEntity.ok(tasks);
//    }

    //ATTACH TASK TO MANAGER OR STAFF
    @PostMapping("/add")
    public HttpEntity<?> add(
            @RequestBody TaskDto taskDto) {
        ApiResponse response = taskService.add(taskDto);
        return ResponseEntity.status(response.isStatus() ? 201 : 409).body(response);
    }

    @GetMapping("/verifyTask")
    public HttpEntity<?> verifyTask(@RequestParam String taskId, @RequestParam String userId) {
        ApiResponse response = taskService.verifyTask(UUID.fromString(taskId), UUID.fromString(userId));
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    //SEND REPORT ABOUT COMPLETED TASK TO MANAGER OR DIRECTOR
    @PostMapping("/sendReport")
    public HttpEntity<?> sendReport(@RequestParam UUID taskId, @RequestBody ReportDto reportDto) {
        ApiResponse response = taskService.sendReport(taskId, reportDto);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    @PostMapping("/changeTask")
    public HttpEntity<?> changeTask(@RequestParam String taskId, @RequestParam String userId) {
        ApiResponse response = taskService.changeTask(UUID.fromString(taskId), UUID.fromString(userId));
        return ResponseEntity.status(response.isStatus() ? 202 : 409).body(response);
    }


}