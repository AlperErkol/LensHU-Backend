package com.example.dto;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String email;
    private String newPassword;
    private String verifyNewPassword;
    public boolean checkIfPasswordsMatch()
    {
        return this.newPassword.equals(this.verifyNewPassword);
    }
}
