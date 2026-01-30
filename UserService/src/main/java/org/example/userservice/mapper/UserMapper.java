package org.example.userservice.mapper;

import lombok.experimental.UtilityClass;
import org.example.userservice.dto.CreateUserRequest;
import org.example.userservice.enums.UserStatus;
import org.example.userservice.model.User;

@UtilityClass
public class UserMapper {
    public User mapToUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNo(request.getPhoneNo())
                .userIdentificationType(request.getUserIdentificationType())
                .userIdentificationValue(request.getUserIdentificationValue())
                .userStatus(UserStatus.ACTIVE).build();

    }
}
