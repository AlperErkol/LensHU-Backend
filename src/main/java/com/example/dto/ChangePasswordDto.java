package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {
    private String email;
    private String currentPassword;
    private String password;
    private String confirmPassword;
    public boolean checkIfPasswordsMatch()
    {
        return this.password.equals(this.confirmPassword);
    }
}
