package com.shop.userservice.service;

import com.shop.userservice.dto.UserDto;
import com.shop.userservice.entity.User;
import com.shop.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto.UserResponse signUp(UserDto.SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

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
