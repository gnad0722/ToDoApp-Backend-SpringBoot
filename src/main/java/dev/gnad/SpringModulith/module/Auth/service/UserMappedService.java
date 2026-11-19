package dev.gnad.SpringModulith.module.Auth.service;

import dev.gnad.SpringModulith.common.policy.PolicyFacade;
import dev.gnad.SpringModulith.module.Auth.domain.dto.RegisterDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.UpdateUserDto;
import dev.gnad.SpringModulith.module.Auth.domain.dto.UserResponseDto;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMappedService {

    private final PolicyFacade policyFacade;

    public UserMappedService(PolicyFacade policyFacade) {
        this.policyFacade = policyFacade;
    }

    public User toObject(RegisterDto registerDto){
            User newUser = new User();
            newUser.setEmail(registerDto.email());
            newUser.setPassword(registerDto.password());
            newUser.setUsername(registerDto.username());
            this.policyFacade.validateBirthday(registerDto.birthday());
            newUser.setBirthday(registerDto.birthday());
            return newUser;
    }

    public UserResponseDto toResponse(User user){
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBirthday(),
                user.getCreatedAt(),
                user.getLastModifiedAt());
    }

    public void patch(UpdateUserDto updateUserDto, User user){
        if (updateUserDto.username() != null){
            user.setUsername(updateUserDto.username());
        }
        if(updateUserDto.birthday() != null){
            policyFacade.validateBirthday(updateUserDto.birthday());
            user.setBirthday(updateUserDto.birthday());
        }
    }
}
