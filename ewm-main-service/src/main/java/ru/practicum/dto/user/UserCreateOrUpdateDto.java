package ru.practicum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UserCreateOrUpdateDto {
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;

    @NotEmpty
    @Email
    @Size(min = 6, max = 254)
    private String email;
}