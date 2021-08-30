package uz.pdp.hrManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.hrManagement.entity.User;
import uz.pdp.hrManagement.entity.Watcher;
import uz.pdp.hrManagement.entity.WorkTimeHistory;
import uz.pdp.hrManagement.entity.enums.RolName;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.payload.WatcherDto;
import uz.pdp.hrManagement.repository.UsersRepository;
import uz.pdp.hrManagement.repository.WatcherRepository;
import uz.pdp.hrManagement.repository.WatcherStatusRepository;
import uz.pdp.hrManagement.repository.WorkTimeHistoryRepository;
import uz.pdp.hrManagement.utils.CommonUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WorkTimeHistoryService {
    @Autowired
    WorkTimeHistoryRepository workTimeHistoryRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    WatcherRepository watcherRepository;

    @Autowired
    WatcherStatusRepository watcherStatusRepository;

    public Set<WorkTimeHistory> getAllByUserId(UUID userId) {
        Map<String, Object> contextHolder = CommonUtils.getPrincipalAndRoleFromSecurityContextHolder();
//        User principalUser = (User) contextHolder.get("principalUser");
        RolName principalUserRol = (RolName) contextHolder.get("principalUserRol");


        assert principalUserRol != null;
        if (principalUserRol.equals(RolName.ROL_STAFF) || principalUserRol.equals(RolName.ROL_MANAGER)) {
            return null;
        }

        return workTimeHistoryRepository.findAllByUserId(userId);
    }

    public ApiResponse entry(WatcherDto watcherDto) {
        Optional<Watcher> optionalWatcher = watcherRepository.findById(watcherDto.getWatcherId());
        if (optionalWatcher.isEmpty()) {
            return new ApiResponse("Watcher not found", false);
        }
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        String entryTime = timeFormatter.format(date);
        String formatDate = dateFormatter.format(date);

        Watcher watcher = optionalWatcher.get();
        User user = watcher.getUser();
        boolean existsByDateAndUserId = workTimeHistoryRepository.existsByDateAndUserId(java.sql.Date.valueOf(formatDate), user.getId());

        if (existsByDateAndUserId) {
            return new ApiResponse("You have already entered", false);
        }

        watcher.setWatcherStatus(watcherStatusRepository.getById(watcherDto.getWatcherStatusId()));

        java.sql.Date workTimeDate = new java.sql.Date(date.getTime());
        WorkTimeHistory workTimeHistory = new WorkTimeHistory();
        workTimeHistory.setDate(workTimeDate);
        workTimeHistory.setEntryTime(entryTime);
        workTimeHistory.setUser(user);
        WorkTimeHistory savedWorkTime = workTimeHistoryRepository.save(workTimeHistory);


        return new ApiResponse("Staff entered", true, savedWorkTime);
    }

    public ApiResponse exit(WatcherDto watcherDto) {
        Optional<Watcher> optionalWatcher = watcherRepository.findById(watcherDto.getWatcherId());
        if (optionalWatcher.isEmpty()) {
            return new ApiResponse("Watcher not found", false);
        }
        Watcher watcher = optionalWatcher.get();
        watcher.setWatcherStatus(watcherStatusRepository.getById(watcherDto.getWatcherStatusId()));
        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        String formatDate = dateFormatter.format(date);
        String departureTime = timeFormatter.format(date);

        User user = watcher.getUser();

        Optional<WorkTimeHistory> optionalWorkTimeHistory =
                workTimeHistoryRepository.findByDateAndUserId(java.sql.Date.valueOf(formatDate), user.getId());
        if (optionalWorkTimeHistory.isEmpty())
            return new ApiResponse("This user did not come yet", false);

        WorkTimeHistory workTimeHistory = optionalWorkTimeHistory.get();
        workTimeHistory.setDepartureTime(departureTime);
        WorkTimeHistory savedWorkTime = workTimeHistoryRepository.save(workTimeHistory);

        return new ApiResponse("Staff exited", true, savedWorkTime);
    }

}
