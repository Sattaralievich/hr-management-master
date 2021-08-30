package uz.pdp.hrManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.pdp.hrManagement.entity.Rol;
import uz.pdp.hrManagement.entity.Task;
import uz.pdp.hrManagement.entity.User;
import uz.pdp.hrManagement.entity.enums.RolName;
import uz.pdp.hrManagement.entity.enums.TaskType;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.payload.ReportDto;
import uz.pdp.hrManagement.payload.TaskDto;
import uz.pdp.hrManagement.repository.RolRepository;
import uz.pdp.hrManagement.repository.TaskRepository;
import uz.pdp.hrManagement.repository.TaskStatusRepository;
import uz.pdp.hrManagement.repository.UsersRepository;
import uz.pdp.hrManagement.utils.CommonUtils;

import java.util.*;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    RolRepository rolRepository;
    @Autowired
    TaskStatusRepository taskStatusRepository;
    @Autowired
    MailService mailService;

    public Page<Task> getAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return taskRepository.findAll(pageable);
    }

    public ApiResponse add(TaskDto taskDto) {
        for (UUID userIdIterator : taskDto.getUsers()) {
            Optional<User> optionalUser = usersRepository.findById(userIdIterator);
            if (optionalUser.isEmpty())
                return new ApiResponse("User not found", false);

            Map<String, Object> contextHolder = CommonUtils.getPrincipalAndRoleFromSecurityContextHolder();
            RolName principalUserRol = (RolName) contextHolder.get("principalUserRol");

            RolName userRol = null;
            User user = optionalUser.get();
            Set<Rol> rols = user.getRols();
            for (Rol rol : rols) {
                userRol = rol.getRolName();
            }

            assert principalUserRol != null;
            //DIRECTOR CAN ATTACH TASK TO  MANAGER OR STAFF,
            boolean isDirectorAuthority = principalUserRol.equals(RolName.ROL_DIRECTOR) &&
                    (Objects.equals(userRol, RolName.ROL_HR_MANAGER) || Objects.equals(userRol, RolName.ROL_MANAGER) || Objects.equals(userRol, RolName.ROL_STAFF));

            //MANAGER ONLY CAN ATTACH TASK TO STAFF
            boolean isManagerAuthority = (principalUserRol.equals(RolName.ROL_HR_MANAGER) || principalUserRol.equals(RolName.ROL_MANAGER)) &&
                    Objects.equals(userRol, RolName.ROL_STAFF);

            // IF CONDITION WILL BE TRUE , SO PRINCIPAL USER IS STAFF. THEREFORE HE/SHE CAN'T ATTACH TASK ANYONE
            if (!(isDirectorAuthority || isManagerAuthority)) {
                return new ApiResponse("Your position is " + principalUserRol + ". You do not have the authority to attach a user with such a role", false);
            }

            Task task = new Task();
            task.setName(taskDto.getName());
            task.setComment(taskDto.getComment());
            task.setExpireDate(taskDto.getExpireDate());
            task.setTaskStatus(taskStatusRepository.findByName(TaskType.NEW));

            Task savedTask = taskRepository.save(task);

            String emailById = user.getEmail();
            String taskId = savedTask.getId().toString();
            String userId = user.getId().toString();

            String subject = "A new task has been added to you";
            String text = "Name: " + savedTask.getName() + "\n" +
                    "Comment: " + savedTask.getComment() + "\n" +
                    "Expire date: " + savedTask.getExpireDate() + "\n" +
                    "Task status: " + savedTask.getTaskStatus().getName() + "\n" +
                    "Confirm => http://localhost:8081/api/task/verifyTask?taskId=" + taskId + "&userId=" + userId;

            mailService.sendEmail(emailById, subject, text);

            return new ApiResponse("Task sent to the user!. Confirm message", true, savedTask);

        }
        return new ApiResponse("All tasks are sent to users:)", true);
    }

    public ApiResponse verifyTask(UUID taskId, UUID userId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return new ApiResponse("Task not found", false);
        }

        boolean existsByUserId = taskRepository.existsByUsersId(userId);
        if (existsByUserId) {
            return new ApiResponse("User has already task that is in progress", false);
        }

        boolean existsById = usersRepository.existsById(userId);
        if (!existsById) {
            return new ApiResponse("User not found", false);
        }

        Task task = optionalTask.get();
        task.setTaskStatus(taskStatusRepository.findByName(TaskType.PROCESS));
        task.setUsers(Collections.singleton(usersRepository.getById(userId)));
        taskRepository.save(task);
        return new ApiResponse("Task attached you", true);
    }

    public ApiResponse sendReport(UUID taskId, ReportDto reportDto) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return new ApiResponse("Task not found", false);
        }

        Task task = optionalTask.get();
        task.setTaskStatus(taskStatusRepository.findByName(TaskType.DONE));
        Task savedTask = taskRepository.save(task);

        String emailById = usersRepository.getEmailById(task.getCreatedBy());

        if (emailById.isEmpty()) {
            return new ApiResponse("Director or manager not found", false);
        }

        for (User user : savedTask.getUsers()) {
            String fullNameById = usersRepository.getFullNameById(user.getId());

            String subject = "Task completed";
            String text = "Name: " + savedTask.getName() + "\n" +
                    "Comment: " + savedTask.getComment() + "\n" +
                    "Expire date: " + savedTask.getExpireDate() + "\n" +
                    "Task status: " + savedTask.getTaskStatus() + "\n" +
                    "Full name : " + fullNameById + "\n" +
                    "Conclusion:" + reportDto.getConclusion();

            mailService.sendEmail(emailById, subject, text);

            return new ApiResponse("Sent report", true);
        }

        return new ApiResponse("All reports sent", true);
    }

    public ApiResponse getAllByUserId(UUID userId) {
        Map<String, Object> contextHolder = CommonUtils.getPrincipalAndRoleFromSecurityContextHolder();
//        User principalUser = (User) contextHolder.get("principalUser");
        RolName principalUserRol = (RolName) contextHolder.get("principalUserRol");


        assert principalUserRol != null;
        if (principalUserRol.equals(RolName.ROL_STAFF) || principalUserRol.equals(RolName.ROL_MANAGER)) {
            return new ApiResponse("You can not see staff's task information", false);
        }

        Set<Task> taskList = taskRepository.findAllByUsersId(userId);
        return new ApiResponse("Staff's tasks", true, taskList);
    }

    public ApiResponse changeTask(UUID taskId, UUID userId) {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            return new ApiResponse("Task not found", false);
        }
        Task task = optionalTask.get();
        if (task.getTaskStatus().getName().equals(TaskType.DONE)) {
            return new ApiResponse("Task already done", false);
        }
        Optional<User> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new ApiResponse("User not found", false);
        }
        boolean existsByUserId = taskRepository.existsByUsersId(userId);
        if (existsByUserId) {
            return new ApiResponse("User has already task that is in progress", false);
        }

        task.setUsers(Collections.singleton(optionalUser.get()));
        Task changedTask = taskRepository.save(task);

        return new ApiResponse("Task changed to another user", true, changedTask);
    }


}
