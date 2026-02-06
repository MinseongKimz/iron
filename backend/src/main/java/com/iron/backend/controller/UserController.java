package com.iron.backend.controller;

import com.iron.backend.dto.UserDto;
import com.iron.backend.dto.auth.LoginRequest;
import com.iron.backend.dto.auth.LoginResponse;
import com.iron.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request.getUsername(), request.getApiKey()));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestParam Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
