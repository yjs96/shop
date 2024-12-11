package com.shop.userservice.controller;

import com.shop.userservice.dto.UserDto;
import com.shop.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto.UserResponse> signUp(@RequestBody UserDto.SignUpRequest request) {
        log.info("회원가입 요청: email={}", request.getEmail());
        try {
            UserDto.UserResponse response = userService.signUp(request);
            log.info("회원가입 성공: userId={}, email={}", response.getId(), response.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("회원가입 실패: email={}", request.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.UserResponse> login(@RequestBody UserDto.LogInRequest request) {
        log.info("로그인 시도: email={}", request.getEmail());
        try {
            UserDto.UserResponse response = userService.login(request);
            log.info("로그인 성공: userId={}, email={}", response.getId(), response.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("로그인 실패: email={}", request.getEmail(), e);
            throw e;
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.UserResponse> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
