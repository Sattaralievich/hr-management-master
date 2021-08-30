package uz.pdp.hrManagement.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import uz.pdp.hrManagement.service.UserService;

@RepositoryRestController
@RequiredArgsConstructor
public class UserController {

    @Autowired
    UserService userService;


}
