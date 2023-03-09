package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {
    private String password;
    private String confirmPassword;
    private String token;
    public boolean checkIfPasswordsMatch()
    {
        return this.password.equals(this.confirmPassword);
    }
}
