package uz.pdp.hrManagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uz.pdp.hrManagement.entity.WorkTimeHistory;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.payload.WatcherDto;
import uz.pdp.hrManagement.service.WorkTimeHistoryService;

import java.util.Set;
import java.util.UUID;

@RepositoryRestController
@RequiredArgsConstructor
public class WorkTimeHistoryController {

    private final WorkTimeHistoryService workTimeHistoryService;

    @GetMapping("/{userId}")
    public HttpEntity<?> getAllByUserId(@PathVariable UUID userId) {
        Set<WorkTimeHistory> workTimeHistories = workTimeHistoryService.getAllByUserId(userId);
        return ResponseEntity.ok(workTimeHistories);
    }


    @PostMapping("/workTimeHistory/entry")
    public HttpEntity<?> entry(@RequestBody WatcherDto watcherDto) {
        ApiResponse response = workTimeHistoryService.entry(watcherDto);
        return ResponseEntity.status(response.isStatus() ? 201 : 409).body(response);
    }

    @PostMapping("/workTimeHistory/exit")
    public HttpEntity<?> exit(@RequestBody WatcherDto watcherDto) {
        ApiResponse response = workTimeHistoryService.exit(watcherDto);
        return ResponseEntity.status(response.isStatus() ? 201 : 409).body(response);
    }
}


