package uz.pdp.hrManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.hrManagement.payload.ApiResponse;
import uz.pdp.hrManagement.payload.LoginDto;
import uz.pdp.hrManagement.payload.PasswordDto;
import uz.pdp.hrManagement.payload.RegisterDto;
import uz.pdp.hrManagement.service.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public HttpEntity<?> register(@RequestBody RegisterDto registerDto) {
        ApiResponse response = authService.register(registerDto);
        return ResponseEntity.status(response.isStatus() ? 201 : 409).body(response);
    }

    @GetMapping("/verifyEmail")
    public HttpEntity<?> verifyEmail(@RequestParam String emailCode, @RequestParam String email) {
        ApiResponse response = authService.verifyEmail(emailCode, email);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    @PostMapping("/verifyEmail")
    public HttpEntity<?> setPassword(@RequestBody PasswordDto passwordDto) {
        ApiResponse response = authService.setPassword(passwordDto);
        return ResponseEntity.status(response.isStatus() ? 200 : 409).body(response);
    }

    @PostMapping("/login")
    public HttpEntity<?> login(@RequestBody LoginDto loginDto) {
        ApiResponse response = authService.login(loginDto);
        return ResponseEntity.status(response.isStatus() ? 200 : 401).body(response);
    }

}