package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.module.Auth.domain.dto.RegisterDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.UserResponseDto;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMappedService {

    public User toObject(RegisterDto registerDto){
            User newUser = new User();
            newUser.setEmail(registerDto.email());
            newUser.setPassword(registerDto.password());
            newUser.setUsername(registerDto.username());
            return newUser;
    }

    public UserResponseDto toResponse(User user){
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getLastModifiedAt());
    }


}
