package org.example.userservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.userservice.enums.UserIdentificationType;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
  String name;
  String email;
  @NotNull String phoneNo;
  @NotNull String password;
  @NotNull UserIdentificationType userIdentificationType;
  @NotNull String userIdentificationValue;
}
