package com.shop.userservice.controller;

import com.shop.userservice.dto.UserDto;
import com.shop.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto.UserResponse> signup(@RequestBody UserDto.SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.UserResponse> login(@RequestBody UserDto.LogInRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.UserResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
