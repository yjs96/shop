package com.shop.userservice.service;

import com.shop.userservice.dto.UserDto;
import com.shop.userservice.entity.User;
import com.shop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto.UserResponse signUp(UserDto.SignUpRequest request) {
        log.debug("회원가입 처리 시작: {}", request);
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("이미 존재하는 이메일: {}", request.getEmail());
            throw new RuntimeException("Email already exists");
        }
        log.info("새로운 사용자 생성 중...");
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .build();

        User savedUser = userRepository.save(user);

        return convertToDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDto.UserResponse login(UserDto.LogInRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Incorrect password");
        }

        return convertToDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto.UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToDto(user);
    }

    private UserDto.UserResponse convertToDto(User user) {
        UserDto.UserResponse response = new UserDto.UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        return response;
    }
}
