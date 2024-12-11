package com.shop.userservice.dto;

import lombok.Data;

public class UserDto {
    @Data
    public static class SignUpRequest {
        private String email;
        private String password;
        private String name;
    }

    @Data
    public static class LogInRequest {
        private String email;
        private String password;
    }

    @Data
    public static class UserResponse {
        private Long id;
        private String email;
        private String name;
    }
}
